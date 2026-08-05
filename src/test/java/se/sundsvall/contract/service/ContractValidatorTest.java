package se.sundsvall.contract.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.contract.integration.db.model.AddressEmbeddable;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.FeesEmbeddable;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class ContractValidatorTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 6, 1);
	private static final String PARTY_ID = "40f14de9-815d-44a5-a34d-b1d38b628e07";
	private final ContractValidator validator = new ContractValidator(Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));

	private static InvoicingEmbeddable completeInvoicing() {
		return InvoicingEmbeddable.builder()
			.withInvoiceInterval(IntervalType.QUARTERLY)
			.withInvoicedIn(InvoicedIn.ARREARS)
			.build();
	}

	private static StakeholderEntity stakeholderWithRoles(final StakeholderRole... roles) {
		return StakeholderEntity.builder()
			.withRoles(List.of(roles))
			.build();
	}

	private static AddressEmbeddable completeAddress() {
		return AddressEmbeddable.builder()
			.withStreetAddress("Testvägen 18")
			.withPostalCode("123 45")
			.withTown("Sundsvall")
			.build();
	}

	/**
	 * A billing party that satisfies every requirement, so that tests can strip out exactly the one thing they are
	 * about.
	 */
	private static StakeholderEntity.StakeholderEntityBuilder validBillingPartyBuilder() {
		return StakeholderEntity.builder()
			.withRoles(List.of(StakeholderRole.PRIMARY_BILLING_PARTY))
			.withOrganizationName("Sundsvalls kommun")
			.withPartyId(PARTY_ID)
			.withAddress(completeAddress());
	}

	private static StakeholderEntity namedBillingParty() {
		return validBillingPartyBuilder().build();
	}

	// ----------------------------------------------------------------------------------------------------------
	// Happy path
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void emptyContractPasses() {
		assertThatCode(() -> validator.validate(ContractEntity.builder().build(), null)).doesNotThrowAnyException();
	}

	@Test
	void fullyValidContractPasses() {
		final var contract = ContractEntity.builder()
			.withLeaseType(LeaseType.LAND_LEASE_RESIDENTIAL)
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(namedBillingParty()))
			.withPropertyDesignations(List.of(PropertyDesignationEmbeddable.builder().withName("SUNDSVALL BALDER 5:1").build()))
			.withFees(FeesEmbeddable.builder()
				.withIndexType("KPI 80")
				.withIndexYear(2021)
				.withIndexNumber(new BigDecimal("1.00"))
				.build())
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	// ----------------------------------------------------------------------------------------------------------
	// PRIMARY_BILLING_PARTY
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void invoicingCompleteWithoutPrimaryBillingPartyIsRejected() {
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(stakeholderWithRoles(StakeholderRole.LESSEE)))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> {
				assertThat(problem.getStatus()).isEqualTo(BAD_REQUEST);
				assertThat(problem.getViolations()).extracting(Violation::field).contains("stakeholders");
			});
	}

	@Test
	void invoicingCompleteWithNamedPrimaryBillingPartyPasses() {
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(namedBillingParty()))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	@Test
	void billingPartyWithoutUsableNameIsRejected() {
		final var billingParty = validBillingPartyBuilder().withOrganizationName(null).build();
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(billingParty))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::message)
				.contains(ContractValidator.PRIMARY_BILLING_PARTY_NAME_MESSAGE));
	}

	@Test
	void billingPartyWithFirstAndLastNamePasses() {
		final var billingParty = validBillingPartyBuilder()
			.withOrganizationName(null)
			.withFirstName("Test")
			.withLastName("Testorsson")
			.build();
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(billingParty))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	@Test
	void billingPartyWithOnlyFirstNameIsRejected() {
		final var billingParty = validBillingPartyBuilder()
			.withOrganizationName(null)
			.withFirstName("Test")
			.build();
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(billingParty))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::message)
				.contains(ContractValidator.PRIMARY_BILLING_PARTY_NAME_MESSAGE));
	}

	/**
	 * BillingDataCollector reads the recipient off the first stakeholder carrying the role, so that is the one every
	 * requirement is evaluated against — a valid stakeholder further down the list must not mask an unusable first one.
	 */
	@Test
	void onlyTheFirstBillingPartyIsValidated() {
		final var unusable = validBillingPartyBuilder().withOrganizationName(null).build();
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(unusable, namedBillingParty()))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::message)
				.contains(ContractValidator.PRIMARY_BILLING_PARTY_NAME_MESSAGE));
	}

	// ----------------------------------------------------------------------------------------------------------
	// PRIMARY_BILLING_PARTY partyId — BillingDataCollector never populates recipient.legalId, so partyId is the only
	// identifier that can satisfy billing's "mandatory for EXTERNAL billing record if legalId is null"
	// ----------------------------------------------------------------------------------------------------------

	private void assertRejectedWith(final StakeholderEntity billingParty, final String expectedMessage) {
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(billingParty))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> {
				assertThat(problem.getStatus()).isEqualTo(BAD_REQUEST);
				assertThat(problem.getViolations()).extracting(Violation::field).contains("stakeholders");
				assertThat(problem.getViolations()).extracting(Violation::message).contains(expectedMessage);
			});
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"", "   "
	})
	void billingPartyWithoutPartyIdIsRejected(final String partyId) {
		assertRejectedWith(validBillingPartyBuilder().withPartyId(partyId).build(), ContractValidator.PRIMARY_BILLING_PARTY_PARTY_ID_MESSAGE);
	}

	// ----------------------------------------------------------------------------------------------------------
	// PRIMARY_BILLING_PARTY address — streetAddress, postalCode and town become addressDetails.street/.postalCode/
	// /.city on the EXTERNAL billing record and are all mandatory there
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void billingPartyWithoutAddressIsRejected() {
		assertRejectedWith(validBillingPartyBuilder().withAddress(null).build(), ContractValidator.PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE);
	}

	@Test
	void billingPartyWithEmptyAddressIsRejected() {
		assertRejectedWith(validBillingPartyBuilder().withAddress(AddressEmbeddable.builder().build()).build(),
			ContractValidator.PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"", "   "
	})
	void billingPartyWithoutStreetAddressIsRejected(final String streetAddress) {
		final var address = completeAddress();
		address.setStreetAddress(streetAddress);

		assertRejectedWith(validBillingPartyBuilder().withAddress(address).build(), ContractValidator.PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"", "   "
	})
	void billingPartyWithoutPostalCodeIsRejected(final String postalCode) {
		final var address = completeAddress();
		address.setPostalCode(postalCode);

		assertRejectedWith(validBillingPartyBuilder().withAddress(address).build(), ContractValidator.PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"", "   "
	})
	void billingPartyWithoutTownIsRejected(final String town) {
		final var address = completeAddress();
		address.setTown(town);

		assertRejectedWith(validBillingPartyBuilder().withAddress(address).build(), ContractValidator.PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE);
	}

	/**
	 * careOf, country, attention and type are not part of the billing constraints and must stay optional.
	 */
	@Test
	void billingPartyWithOnlyTheThreeMandatoryAddressFieldsPasses() {
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(validBillingPartyBuilder().withAddress(completeAddress()).build()))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	@Test
	void allBillingPartyViolationsAreReportedTogether() {
		final var billingParty = StakeholderEntity.builder()
			.withRoles(List.of(StakeholderRole.PRIMARY_BILLING_PARTY))
			.build();
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(billingParty))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::message).contains(
				ContractValidator.PRIMARY_BILLING_PARTY_NAME_MESSAGE,
				ContractValidator.PRIMARY_BILLING_PARTY_PARTY_ID_MESSAGE,
				ContractValidator.PRIMARY_BILLING_PARTY_ADDRESS_MESSAGE));
	}

	/**
	 * The new requirements are gated on the same complete-invoicing condition as the rest of the billing party rules —
	 * a contract that is not set up for invoicing is never sent to billing and must stay unaffected.
	 */
	@Test
	void incompleteInvoicingDoesNotRequirePartyIdOrAddress() {
		final var billingParty = StakeholderEntity.builder()
			.withRoles(List.of(StakeholderRole.PRIMARY_BILLING_PARTY))
			.withOrganizationName("Sundsvalls kommun")
			.build();
		final var contract = ContractEntity.builder()
			.withInvoicing(InvoicingEmbeddable.builder().withInvoiceInterval(IntervalType.QUARTERLY).build())
			.withStakeholders(List.of(billingParty))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	@Test
	void invoicingIncompleteDoesNotRequirePrimaryBillingParty() {
		final var contract = ContractEntity.builder()
			.withInvoicing(InvoicingEmbeddable.builder().withInvoiceInterval(IntervalType.QUARTERLY).build())
			.withStakeholders(List.of(stakeholderWithRoles(StakeholderRole.LESSEE)))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	@Test
	void nullInvoicingDoesNotRequirePrimaryBillingParty() {
		final var contract = ContractEntity.builder()
			.withStakeholders(List.of(stakeholderWithRoles(StakeholderRole.LESSEE)))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	// ----------------------------------------------------------------------------------------------------------
	// Property designation names must not be blank (whitespace-only); designations are never required
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void blankDesignationNameIsRejected() {
		final var contract = ContractEntity.builder()
			.withPropertyDesignations(List.of(PropertyDesignationEmbeddable.builder().withName("  ").build()))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> {
				assertThat(problem.getStatus()).isEqualTo(BAD_REQUEST);
				assertThat(problem.getViolations()).extracting(Violation::field).contains("propertyDesignations");
				assertThat(problem.getViolations()).extracting(Violation::message).contains(ContractValidator.PROPERTY_DESIGNATION_BLANK_MESSAGE);
			});
	}

	@Test
	void namedDesignationPasses() {
		final var contract = ContractEntity.builder()
			.withPropertyDesignations(List.of(PropertyDesignationEmbeddable.builder().withName("SUNDSVALL BALDER 5:1").build()))
			.build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	@ParameterizedTest
	@EnumSource(LeaseType.class)
	void designationsAreNeverRequiredRegardlessOfLeaseType(final LeaseType leaseType) {
		final var contract = ContractEntity.builder().withLeaseType(leaseType).build();

		assertThatCode(() -> validator.validate(contract, null)).doesNotThrowAnyException();
	}

	// ----------------------------------------------------------------------------------------------------------
	// Multiple violations accumulate into a single problem
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void multipleViolationsAreAccumulated() {
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(stakeholderWithRoles(StakeholderRole.LESSEE)))
			.withPropertyDesignations(List.of(PropertyDesignationEmbeddable.builder().withName("  ").build()))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::field)
				.contains("stakeholders", "propertyDesignations"));
	}

	// ----------------------------------------------------------------------------------------------------------
	// endDate must not be set/changed to a date before today (unchanged past endDate is allowed)
	// ----------------------------------------------------------------------------------------------------------

	private static ContractEntity contractWithEndDate(final LocalDate endDate) {
		return ContractEntity.builder().withEndDate(endDate).build();
	}

	@Test
	void newContractWithPastEndDateIsRejected() {
		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contractWithEndDate(TODAY.minusDays(1)), null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::field).contains("endDate"));
	}

	@Test
	void newContractWithTodayEndDateIsAccepted() {
		assertThatCode(() -> validator.validate(contractWithEndDate(TODAY), null)).doesNotThrowAnyException();
	}

	@Test
	void newContractWithFutureEndDateIsAccepted() {
		assertThatCode(() -> validator.validate(contractWithEndDate(TODAY.plusYears(1)), null)).doesNotThrowAnyException();
	}

	@Test
	void nullEndDateIsAccepted() {
		assertThatCode(() -> validator.validate(contractWithEndDate(null), null)).doesNotThrowAnyException();
	}

	@Test
	void unchangedPastEndDateIsAccepted() {
		final var pastEndDate = TODAY.minusYears(1);

		assertThatCode(() -> validator.validate(contractWithEndDate(pastEndDate), pastEndDate)).doesNotThrowAnyException();
	}

	@Test
	void changingEndDateToThePastIsRejected() {
		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contractWithEndDate(TODAY.minusDays(1)), TODAY.plusYears(1)))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::field).contains("endDate"));
	}

	@Test
	void changingEndDateToADifferentPastDateIsRejected() {
		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contractWithEndDate(TODAY.minusDays(5)), TODAY.minusDays(10)))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::field).contains("endDate"));
	}

	@Test
	void changingEndDateToTheFutureIsAccepted() {
		assertThatCode(() -> validator.validate(contractWithEndDate(TODAY.plusYears(1)), TODAY.minusYears(1))).doesNotThrowAnyException();
	}
}
