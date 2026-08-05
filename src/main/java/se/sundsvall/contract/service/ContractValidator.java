package se.sundsvall.contract.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Validates cross-OBJECT business constraints on a fully mapped {@link ContractEntity} before it is persisted. These
 * guard against contract data that would otherwise be rejected by the downstream billing pipeline
 * (BillingDataCollector / BillingPreprocessor) for contracts that are to be invoiced.
 *
 * <p>
 * Only rules that span multiple objects, or that depend on the previously stored state, live here: the
 * PRIMARY_BILLING_PARTY rule (invoicing + stakeholders), the property-designation name rule (a sent-in designation must
 * not have a whitespace-only name), and the endDate rule (which must be compared against the previously stored
 * endDate).
 * On a patch the relevant data may live on the existing contract and not be re-sent, so the merged entity is the only
 * place these can be evaluated correctly. Single-object/field constraints (e.g. the fee index trio and
 * additionalInformation size) are declared as bean-validation annotations on the API model instead.
 * </p>
 */
@Component
public class ContractValidator {

	static final String PRIMARY_BILLING_PARTY_MESSAGE = "A stakeholder with role PRIMARY_BILLING_PARTY is required when both invoicing interval and invoicedIn are set.";
	static final String PRIMARY_BILLING_PARTY_NAME_MESSAGE = "The PRIMARY_BILLING_PARTY stakeholder must have an organization name, or both a first and last name.";
	static final String PRIMARY_BILLING_PARTY_PARTY_ID_MESSAGE = "The PRIMARY_BILLING_PARTY stakeholder must have a partyId.";
	static final String PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE = "The PRIMARY_BILLING_PARTY stakeholder must have an address with streetAddress, postalCode and town.";
	static final String PROPERTY_DESIGNATION_BLANK_MESSAGE = "Property designation name must not be blank.";
	static final String END_DATE_MESSAGE = "endDate must not be set to a date before today's date";

	private final Clock clock;

	public ContractValidator() {
		this(Clock.systemDefaultZone());
	}

	ContractValidator(final Clock clock) {
		this.clock = clock;
	}

	/**
	 * Validates the given entity, throwing a {@link ConstraintViolationProblem} (HTTP 400) listing every violation if
	 * any rule fails.
	 *
	 * @param  contract                   the fully mapped contract entity to validate
	 * @param  previousEndDate            the endDate currently stored for this contract, or {@code null} for a new
	 *                                    contract; an unchanged (already past) endDate is allowed
	 * @throws ConstraintViolationProblem if one or more constraints are violated
	 */
	public void validate(final ContractEntity contract, final LocalDate previousEndDate) {
		final var violations = new ArrayList<Violation>();

		validatePrimaryBillingParty(contract, violations);
		validatePropertyDesignations(contract, violations);
		validateEndDate(contract, previousEndDate, violations);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationProblem(BAD_REQUEST, violations);
		}
	}

	/**
	 * The endDate may not be set or changed to a date before today. An endDate that is left unchanged from what was
	 * already stored is allowed, so that contracts whose endDate has already passed (e.g. terminated ones) can still be
	 * updated. A {@code null} endDate is always allowed.
	 */
	private void validateEndDate(final ContractEntity contract, final LocalDate previousEndDate, final List<Violation> violations) {
		final var endDate = contract.getEndDate();
		final var changed = !Objects.equals(endDate, previousEndDate);
		if (endDate != null && changed && endDate.isBefore(LocalDate.now(clock))) {
			violations.add(new Violation("endDate", END_DATE_MESSAGE));
		}
	}

	/**
	 * When a contract is set up for invoicing (both interval and invoicedIn present), it must have a stakeholder with
	 * the {@link StakeholderRole#PRIMARY_BILLING_PARTY} role — otherwise billing has no recipient. Everything
	 * BillingDataCollector puts on the billing record's {@code recipient} is read off that single stakeholder, and
	 * since contract-sourced billing records are always of type {@code EXTERNAL} the whole set of
	 * "mandatory for EXTERNAL billing record" constraints applies to it:
	 * <ul>
	 * <li>a usable recipient name — an organization name, or both a first and last name — otherwise the record is
	 * rejected with "recipient must either have an organization name or a first and last name defined"</li>
	 * <li>a {@code partyId}; BillingDataCollector never populates {@code recipient.legalId}, so partyId is the only
	 * identifier that can satisfy "mandatory for EXTERNAL billing record if legalId is null"</li>
	 * <li>an address carrying streetAddress, postalCode and town, which become {@code addressDetails.street},
	 * {@code .postalCode} and {@code .city} — rejected with "Street, postal code and city must all be present in
	 * recipient.addressDetails for EXTERNAL billing record". A stakeholder with no address at all is worse still: it
	 * makes BillingDataCollector fail with a 500 before the record is even sent.</li>
	 * </ul>
	 *
	 * <p>
	 * All aspects are evaluated against the <em>first</em> stakeholder carrying the role, because that is the one
	 * BillingDataCollector picks (its mapper does a {@code findFirst} over the roles). Validating any other
	 * stakeholder would let through contracts that billing then rejects.
	 * </p>
	 */
	private void validatePrimaryBillingParty(final ContractEntity contract, final List<Violation> violations) {
		final var invoicing = contract.getInvoicing();
		final var invoicingComplete = nonNull(invoicing) && nonNull(invoicing.getInvoiceInterval()) && nonNull(invoicing.getInvoicedIn());
		if (!invoicingComplete) {
			return;
		}

		final var billingParty = ofNullable(contract.getStakeholders()).orElse(List.of()).stream()
			.filter(stakeholder -> nonNull(stakeholder.getRoles()) && stakeholder.getRoles().contains(StakeholderRole.PRIMARY_BILLING_PARTY))
			.findFirst();

		if (billingParty.isEmpty()) {
			violations.add(new Violation("stakeholders", PRIMARY_BILLING_PARTY_MESSAGE));
			return;
		}

		final var stakeholder = billingParty.get();
		if (!hasUsableRecipientName(stakeholder)) {
			violations.add(new Violation("stakeholders", PRIMARY_BILLING_PARTY_NAME_MESSAGE));
		}
		if (!isNotBlank(stakeholder.getPartyId())) {
			violations.add(new Violation("stakeholders", PRIMARY_BILLING_PARTY_PARTY_ID_MESSAGE));
		}
		if (!hasUsableRecipientAddress(stakeholder)) {
			violations.add(new Violation("stakeholders", PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE));
		}
	}

	private static boolean hasUsableRecipientName(final StakeholderEntity stakeholder) {
		final var hasOrganizationName = isNotBlank(stakeholder.getOrganizationName());
		final var hasFullName = isNotBlank(stakeholder.getFirstName()) && isNotBlank(stakeholder.getLastName());
		return hasOrganizationName || hasFullName;
	}

	/**
	 * A missing address and an address missing any of the three mandatory fields are reported identically — from the
	 * caller's point of view the remedy is the same, and the {@code careOf}, {@code country}, {@code attention} and
	 * {@code type} fields are not required by billing.
	 */
	private static boolean hasUsableRecipientAddress(final StakeholderEntity stakeholder) {
		return ofNullable(stakeholder.getAddress())
			.filter(address -> isNotBlank(address.getStreetAddress()))
			.filter(address -> isNotBlank(address.getPostalCode()))
			.filter(address -> isNotBlank(address.getTown()))
			.isPresent();
	}

	private static boolean isNotBlank(final String value) {
		return value != null && !value.isBlank();
	}

	/**
	 * Property designations are not required, and there is no longer any requirement tied to the lease type. However, a
	 * designation that <em>is</em> sent in must carry a real name. Elements whose name is {@code null} or the empty
	 * string are dropped during mapping and never reach this point; a name consisting solely of whitespace is kept and
	 * rejected here, so that no blank designation rows are ever persisted.
	 */
	private void validatePropertyDesignations(final ContractEntity contract, final List<Violation> violations) {
		final var hasBlankName = ofNullable(contract.getPropertyDesignations()).orElse(List.of()).stream()
			.map(PropertyDesignationEmbeddable::getName)
			.anyMatch(name -> nonNull(name) && name.isBlank());

		if (hasBlankName) {
			violations.add(new Violation("propertyDesignations", PROPERTY_DESIGNATION_BLANK_MESSAGE));
		}
	}
}
