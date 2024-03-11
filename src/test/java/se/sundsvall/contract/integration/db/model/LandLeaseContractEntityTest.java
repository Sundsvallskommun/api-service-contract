package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.api.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.api.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.api.model.enums.LandLeaseType.SITELEASEHOLD;
import static se.sundsvall.contract.api.model.enums.Status.TERMINATED;
import static se.sundsvall.contract.api.model.enums.UsufructType.FISHING;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;

class LandLeaseContractEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
		registerValueGenerator(() -> Duration.of(new Random().nextLong(), SECONDS), Duration.class);
	}

	@Test
	void testBean() {
		assertThat(LandLeaseContractEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("areaData", "attachments", "stakeholders"),
			hasValidBeanEqualsExcluding("areaData", "attachments", "stakeholders"),
			hasValidBeanToStringExcluding("areaData", "attachments", "stakeholders")));
	}

	@Test
	void testBuilderMethods() {
		final var version = 1;
		final var status = TERMINATED;
		final var municipalityId = "1984";
		final var id = "2024-12345";
		final var caseId = 1L;
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
		final var stakeholders = List.of(StakeholderEntity.builder().build());
		final var attachments = List.of(AttachmentEntity.builder().build());
		final var landLeaseType = SITELEASEHOLD;
		final var leaseholdType = LeaseholdEntity.builder().build();
		final var usufructType = FISHING;
		final var externalReferenceId = "externalReferenceId";
		final var propertyDesignation = "propertyDesignation";
		final var objectIdentity = "objectIdentity";
		final var leaseDuration = 3;
		final var rental = BigDecimal.valueOf(2.0);
		final var invoiceInterval = QUARTERLY;
		final var invoicedIn = ADVANCE;
		final var start = now();
		final var end = now();
		final var autoExtend = true;
		final var leaseExtension = 4;
		final var periodOfNotice = 1;
		final var area = 1;
		final var areaData = new FeatureCollection();

		final var contract = LandLeaseContractEntity.builder()
			.withId(id)
			.withVersion(version)
			.withStatus(status)
			.withMunicipalityId(municipalityId)
			.withCaseId(caseId)
			.withIndexTerms(indexTerms)
			.withDescription(description)
			.withAdditionalTerms(additionalTerms)
			.withExtraParameters(extraParameters)
			.withStakeholders(stakeholders)
			.withAttachments(attachments)
			.withLandLeaseType(landLeaseType)
			.withLeaseholdType(leaseholdType)
			.withUsufructType(usufructType)
			.withExternalReferenceId(externalReferenceId)
			.withPropertyDesignation(propertyDesignation)
			.withObjectIdentity(objectIdentity)
			.withLeaseDuration(leaseDuration)
			.withRental(rental)
			.withInvoicing(InvoicingEntity.builder()
				.withInvoiceInterval(invoiceInterval)
				.withInvoicedIn(invoicedIn)
				.build())
			.withStart(start)
			.withEnd(end)
			.withAutoExtend(autoExtend)
			.withLeaseExtension(leaseExtension)
			.withPeriodOfNotice(periodOfNotice)
			.withArea(area)
			.withAreaData(areaData)
			.build();

		assertThat(contract).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(contract.getId()).isEqualTo(id);
		assertThat(contract.getVersion()).isEqualTo(version);
		assertThat(contract.getStatus()).isEqualTo(status);
		assertThat(contract.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(contract.getCaseId()).isEqualTo(caseId);
		assertThat(contract.getIndexTerms()).isEqualTo(indexTerms);
		assertThat(contract.getDescription()).isEqualTo(description);
		assertThat(contract.getAdditionalTerms()).isEqualTo(additionalTerms);
		assertThat(contract.getExtraParameters()).isEqualTo(extraParameters);
		assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		assertThat(contract.getAttachments()).isEqualTo(attachments);
		assertThat(contract.getLandLeaseType()).isEqualTo(landLeaseType);
		assertThat(contract.getLeaseholdType()).isEqualTo(leaseholdType);
		assertThat(contract.getUsufructType()).isEqualTo(usufructType);
		assertThat(contract.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(contract.getPropertyDesignation()).isEqualTo(propertyDesignation);
		assertThat(contract.getObjectIdentity()).isEqualTo(objectIdentity);
		assertThat(contract.getLeaseDuration()).isEqualTo(leaseDuration);
		assertThat(contract.getRental()).isEqualTo(rental);
		assertThat(contract.getInvoicing()).satisfies(invoicing -> {
			assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval);
			assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn);
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
		assertThat(LandLeaseContractEntity.builder().build()).hasAllNullFieldsOrPropertiesExcept("signedByWitness");
	}
}
