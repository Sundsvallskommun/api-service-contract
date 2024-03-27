package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.LandLeaseType.SITELEASEHOLD;
import static se.sundsvall.contract.model.enums.Status.TERMINATED;
import static se.sundsvall.contract.model.enums.UsufructType.FISHING;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.LeaseFees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.contract.model.enums.UsufructType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class LandLeaseContractTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
		registerValueGenerator(() -> Duration.of(new Random().nextLong(), SECONDS), Duration.class);
	}

	@Test
	void testBean() {
		assertThat(LandLeaseContract.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testLandLeseContract_landLeaseType_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = LandLeaseContract.class.getDeclaredField("landLeaseType")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(LandLeaseType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}

	@Test
	void testLandLeseContract_usufructType_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = LandLeaseContract.class.getDeclaredField("usufructType")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(UsufructType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}

	@Test
	void testBuilderMethods() {
		final var version = 1;
		final var status = TERMINATED;
		final var municipalityId = "1984";
		final var caseId = 1L;
		final var contractId = "2024-12345";
		final var indexTerms = List.of(
			TermGroup.builder()
				.withHeader("Some index terms")
				.withTerms(List.of(
					Term.builder()
						.withName("Some index term")
						.withDescription("Some description")
						.build()))
				.build());
		final var description = "description";
		final var additionalTerms = List.of(
			TermGroup.builder()
				.withHeader("Some additional terms")
				.withTerms(List.of(
					Term.builder()
						.withName("Some additional term")
						.withDescription("Some description")
						.build()))
				.build());
		final var extraParameters = Map.of("someParameter", "someValue");
		final var stakeholders = List.of(Stakeholder.builder().build());
		final var landLeaseType = SITELEASEHOLD;
		final var leasehold = Leasehold.builder().build();
		final var usufructType = FISHING;
		final var externalReferenceId = "externalReferenceId";
		final var propertyDesignations = List.of("propertyDesignations", "otherPropertyDesignation");
		final var objectIdentity = "objectIdentity";
		final var leaseDuration = 3;
		final var leaseFees = LeaseFees.builder()
			.withCurrency("SEK")
			.withYearly(BigDecimal.valueOf(4350))
			.withMonthly(BigDecimal.valueOf(375))
			.withTotal(BigDecimal.valueOf(52200))
			.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
			.withIndexYear(2023)
			.withIndexNumber(2)
			.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
			.build();
		final var invoiceInterval = QUARTERLY;
		final var invoicedIn = ARREARS;
		final var start = now();
		final var end = now();
		final var autoExtend = true;
		final var leaseExtension = 4;
		final var periodOfNotice = 1;
		final var area = 1;
		final var areaData = new FeatureCollection();

		final var contract = LandLeaseContract.builder()
			.withContractId(contractId)
			.withVersion(version)
			.withStatus(status.name())
			.withMunicipalityId(municipalityId)
			.withCaseId(caseId)
			.withIndexTerms(indexTerms)
			.withDescription(description)
			.withAdditionalTerms(additionalTerms)
			.withExtraParameters(extraParameters)
			.withStakeholders(stakeholders)
			.withLandLeaseType(landLeaseType.name())
			.withLeasehold(leasehold)
			.withUsufructType(usufructType.name())
			.withExternalReferenceId(externalReferenceId)
			.withPropertyDesignations(propertyDesignations)
			.withObjectIdentity(objectIdentity)
			.withLeaseDuration(leaseDuration)
			.withLeaseFees(leaseFees)
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(invoiceInterval.name())
				.withInvoicedIn(invoicedIn.name())
				.build())
			.withStart(start)
			.withEnd(end)
			.withAutoExtend(autoExtend)
			.withLeaseExtension(leaseExtension)
			.withPeriodOfNotice(periodOfNotice)
			.withArea(area)
			.withAreaData(areaData)
			.build();

		assertThat(contract).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(contract.getVersion()).isEqualTo(version);
		assertThat(contract.getStatus()).isEqualTo(status.name());
		assertThat(contract.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(contract.getCaseId()).isEqualTo(caseId);
		assertThat(contract.getIndexTerms()).isEqualTo(indexTerms);
		assertThat(contract.getDescription()).isEqualTo(description);
		assertThat(contract.getAdditionalTerms()).isEqualTo(additionalTerms);
		assertThat(contract.getExtraParameters()).isEqualTo(extraParameters);
		assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		assertThat(contract.getLandLeaseType()).isEqualTo(landLeaseType.name());
		assertThat(contract.getLeasehold()).isEqualTo(leasehold);
		assertThat(contract.getUsufructType()).isEqualTo(usufructType.name());
		assertThat(contract.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(contract.getPropertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(contract.getObjectIdentity()).isEqualTo(objectIdentity);
		assertThat(contract.getLeaseDuration()).isEqualTo(leaseDuration);
		assertThat(contract.getLeaseFees()).isEqualTo(leaseFees);
		assertThat(contract.getInvoicing()).satisfies(invoicing -> {
			assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval.name());
			assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn.name());
		});
		assertThat(contract.getStart()).isEqualTo(start);
		assertThat(contract.getEnd()).isEqualTo(end);
		assertThat(contract.getAutoExtend()).isEqualTo(autoExtend);
		assertThat(contract.getLeaseExtension()).isEqualTo(leaseExtension);
		assertThat(contract.getPeriodOfNotice()).isEqualTo(periodOfNotice);
		assertThat(contract.getArea()).isEqualTo(area);
		assertThat(contract.getAreaData()).isEqualTo(areaData);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(LandLeaseContract.builder().build()).hasAllNullFieldsOrPropertiesExcept("signedByWitness");
	}
}
