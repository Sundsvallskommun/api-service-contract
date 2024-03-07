package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import org.assertj.core.api.Assertions;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;

class LandLeaseContractEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
		registerValueGenerator(() -> Duration.of(new Random().nextLong(), ChronoUnit.SECONDS), Duration.class);
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
		final var status = Status.TERMINATED;
		final var id = 1L;
		final var caseId = 1L;
		final var indexTerms = "indexTerms";
		final var description = "description";
		final var additionalTerms = "additionalTerms";
		final var stakeholders = List.of(StakeholderEntity.builder().build());
		final var attachments = List.of(AttachmentEntity.builder().build());
		final var landLeaseType = LandLeaseType.SITELEASEHOLD;
		final var leaseholdType = LeaseholdEntity.builder().build();
		final var usufructType = UsufructType.FISHING;
		final var externalReferenceId = "externalReferenceId";
		final var propertyDesignation = "propertyDesignation";
		final var objectIdentity = "objectIdentity";
		final var leaseDuration = 3;
		final var rental = BigDecimal.valueOf(2.0);
		final var invoiceInterval = IntervalType.QUARTERLY;
		final var start = now();
		final var end = now();
		final var autoExtend = true;
		final var leaseExtension = 4;
		final var periodOfNotice = 1;
		final var area = 1;
		final var areaData = new FeatureCollection();

		final var contract = LandLeaseContractEntity.builder()
			.withVersion(version)
			.withStatus(status)
			.withId(id)
			.withCaseId(caseId)
			.withIndexTerms(indexTerms)
			.withDescription(description)
			.withAdditionalTerms(additionalTerms)
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
			.withInvoiceInterval(invoiceInterval)
			.withStart(start)
			.withEnd(end)
			.withAutoExtend(autoExtend)
			.withLeaseExtension(leaseExtension)
			.withPeriodOfNotice(periodOfNotice)
			.withArea(area)
			.withAreaData(areaData)
			.build();

		Assertions.assertThat(contract).isNotNull().hasNoNullFieldsOrProperties();
		Assertions.assertThat(contract.getVersion()).isEqualTo(version);
		Assertions.assertThat(contract.getStatus()).isEqualTo(status);
		Assertions.assertThat(contract.getCaseId()).isEqualTo(caseId);
		Assertions.assertThat(contract.getIndexTerms()).isEqualTo(indexTerms);
		Assertions.assertThat(contract.getDescription()).isEqualTo(description);
		Assertions.assertThat(contract.getAdditionalTerms()).isEqualTo(additionalTerms);
		Assertions.assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		Assertions.assertThat(contract.getAttachments()).isEqualTo(attachments);
		Assertions.assertThat(contract.getLandLeaseType()).isEqualTo(landLeaseType);
		Assertions.assertThat(contract.getLeaseholdType()).isEqualTo(leaseholdType);
		Assertions.assertThat(contract.getUsufructType()).isEqualTo(usufructType);
		Assertions.assertThat(contract.getExternalReferenceId()).isEqualTo(externalReferenceId);
		Assertions.assertThat(contract.getPropertyDesignation()).isEqualTo(propertyDesignation);
		Assertions.assertThat(contract.getObjectIdentity()).isEqualTo(objectIdentity);
		Assertions.assertThat(contract.getLeaseDuration()).isEqualTo(leaseDuration);
		Assertions.assertThat(contract.getRental()).isEqualTo(rental);
		Assertions.assertThat(contract.getInvoiceInterval()).isEqualTo(invoiceInterval);
		Assertions.assertThat(contract.getStart()).isEqualTo(start);
		Assertions.assertThat(contract.getEnd()).isEqualTo(end);
		Assertions.assertThat(contract.getAutoExtend()).isEqualTo(autoExtend);
		Assertions.assertThat(contract.getLeaseExtension()).isEqualTo(leaseExtension);
		Assertions.assertThat(contract.getPeriodOfNotice()).isEqualTo(periodOfNotice);
		Assertions.assertThat(contract.getArea()).isEqualTo(area);
		Assertions.assertThat(contract.getAreaData()).isEqualTo(areaData);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		Assertions.assertThat(LandLeaseContractEntity.builder().build()).hasAllNullFieldsOrProperties();
	}


}
