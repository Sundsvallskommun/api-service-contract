package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class LandLeaseContractTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
		registerValueGenerator(() -> Duration.of(new Random().nextLong(), ChronoUnit.SECONDS), Duration.class);
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
	void testLandLeseContract_invoiceInterval_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = LandLeaseContract.class.getDeclaredField("invoiceInterval")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(IntervalType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}

	@Test
	void testBuilderMethods() {
		final var version = 1;
		final var status = Status.TERMINATED;

		final var caseId = 1L;
		final var indexTerms = "indexTerms";
		final var description = "description";
		final var additionalTerms = "additionalTerms";
		final var stakeholders = List.of(Stakeholder.builder().build());
		final var attachments = List.of(Attachment.builder().build());
		final var landLeaseType = LandLeaseType.SITELEASEHOLD;
		final var leaseholdType = Leasehold.builder().build();
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

		final var contract = LandLeaseContract.builder()
			.withVersion(version)
			.withStatus(status.name())
			.withCaseId(caseId)
			.withIndexTerms(indexTerms)
			.withDescription(description)
			.withAdditionalTerms(additionalTerms)
			.withStakeholders(stakeholders)
			.withAttachments(attachments)
			.withLandLeaseType(landLeaseType.name())
			.withLeaseholdType(leaseholdType)
			.withUsufructType(usufructType.name())
			.withExternalReferenceId(externalReferenceId)
			.withPropertyDesignation(propertyDesignation)
			.withObjectIdentity(objectIdentity)
			.withLeaseDuration(leaseDuration)
			.withRental(rental)
			.withInvoiceInterval(invoiceInterval.name())
			.withStart(start)
			.withEnd(end)
			.withAutoExtend(autoExtend)
			.withLeaseExtension(leaseExtension)
			.withPeriodOfNotice(periodOfNotice)
			.withArea(area)
			.withAreaData(areaData)
			.build();

		assertThat(contract).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(contract.getVersion()).isEqualTo(version);
		assertThat(contract.getStatus()).isEqualTo(status.name());
		assertThat(contract.getCaseId()).isEqualTo(caseId);
		assertThat(contract.getIndexTerms()).isEqualTo(indexTerms);
		assertThat(contract.getDescription()).isEqualTo(description);
		assertThat(contract.getAdditionalTerms()).isEqualTo(additionalTerms);
		assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		assertThat(contract.getAttachments()).isEqualTo(attachments);
		assertThat(contract.getLandLeaseType()).isEqualTo(landLeaseType.name());
		assertThat(contract.getLeaseholdType()).isEqualTo(leaseholdType);
		assertThat(contract.getUsufructType()).isEqualTo(usufructType.name());
		assertThat(contract.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(contract.getPropertyDesignation()).isEqualTo(propertyDesignation);
		assertThat(contract.getObjectIdentity()).isEqualTo(objectIdentity);
		assertThat(contract.getLeaseDuration()).isEqualTo(leaseDuration);
		assertThat(contract.getRental()).isEqualTo(rental);
		assertThat(contract.getInvoiceInterval()).isEqualTo(invoiceInterval.name());
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
		assertThat(LandLeaseContract.builder().build()).hasAllNullFieldsOrProperties();
	}


}
