package se.sundsvall.contract.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

	private static StakeholderEntity namedBillingParty() {
		return StakeholderEntity.builder()
			.withRoles(List.of(StakeholderRole.PRIMARY_BILLING_PARTY))
			.withOrganizationName("Sundsvalls kommun")
			.build();
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
		final var contract = ContractEntity.builder()
			.withInvoicing(completeInvoicing())
			.withStakeholders(List.of(stakeholderWithRoles(StakeholderRole.PRIMARY_BILLING_PARTY)))
			.build();

		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> validator.validate(contract, null))
			.satisfies(problem -> assertThat(problem.getViolations()).extracting(Violation::message)
				.contains(ContractValidator.PRIMARY_BILLING_PARTY_NAME_MESSAGE));
	}

	@Test
	void billingPartyWithFirstAndLastNamePasses() {
		final var billingParty = StakeholderEntity.builder()
			.withRoles(List.of(StakeholderRole.PRIMARY_BILLING_PARTY))
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
		final var billingParty = StakeholderEntity.builder()
			.withRoles(List.of(StakeholderRole.PRIMARY_BILLING_PARTY))
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
