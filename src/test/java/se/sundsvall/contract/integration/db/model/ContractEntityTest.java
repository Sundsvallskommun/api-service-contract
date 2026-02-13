package se.sundsvall.contract.integration.db.model;

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
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_ADDITIONAL;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_INDEX;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.model.enums.Status.TERMINATED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.TimeUnit;

class ContractEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(ContractEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testPrePersist() {
		var entity = ContractEntity.builder().build();
		assertThat(entity.getVersion()).isOne();
		entity.prePersist();
		assertThat(entity.getVersion()).isEqualTo(2);
	}

	@Test
	void testBuilderMethods() {
		var type = ContractType.LEASE_AGREEMENT;
		var version = 1;
		var status = TERMINATED;
		var municipalityId = "1984";
		var contractId = "2024-12345";
		var id = 1L;
		var termGroups = List.of(
			TermGroupEntity.builder()
				.withHeader("Some index terms")
				.withType(TYPE_INDEX)
				.withTerms(List.of(
					TermEmbeddable.builder()
						.withName("Some index term")
						.withDescription("Some description")
						.build()))
				.build(),
			TermGroupEntity.builder()
				.withHeader("Some additional terms")
				.withType(TYPE_ADDITIONAL)
				.withTerms(List.of(
					TermEmbeddable.builder()
						.withName("Some additional term")
						.withDescription("Some description")
						.build()))
				.build());
		var description = "description";
		var extraParameters = List.of(
			ExtraParameterGroupEntity.builder()
				.withName("someExtraParameterGroup")
				.withParameters(Map.of("someParameter", "someValue"))
				.build());
		var stakeholders = List.of(StakeholderEntity.builder().build());
		var leaseType = LeaseType.LEASEHOLD;
		var leasehold = LeaseholdEmbeddable.builder().build();
		var externalReferenceId = "externalReferenceId";
		var propertyDesignations = List.of(
			PropertyDesignationEmbeddable.builder()
				.withName("propertyDesignationName")
				.withDistrict("propertyDesignationDistrict")
				.build(),
			PropertyDesignationEmbeddable.builder()
				.withName("otherPropertyDesignationName")
				.withDistrict("otherPropertyDesignationDistrict")
				.build());
		var objectIdentity = "objectIdentity";
		var leaseDuration = 3;
		var leaseDurationUnit = TimeUnit.MONTHS;
		var fees = FeesEmbeddable.builder()
			.withCurrency("SEK")
			.withYearly(BigDecimal.valueOf(4350))
			.withMonthly(BigDecimal.valueOf(375))
			.withTotal(BigDecimal.valueOf(52200))
			.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
			.withIndexType("KPI 80")
			.withIndexYear(2023)
			.withIndexationRate(BigDecimal.valueOf(0.5))
			.withIndexNumber(2)
			.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
			.build();
		var invoiceInterval = QUARTERLY;
		var invoicedIn = ADVANCE;
		var start = now();
		var end = now();
		var autoExtend = true;
		var leaseExtension = 4;
		var leaseExtensionUnit = TimeUnit.YEARS;
		var noticeTerms = List.of(NoticeTermEmbeddable.builder().build());
		var area = 1;
		var areaData = new FeatureCollection();

		var contract = ContractEntity.builder()
			.withId(id)
			.withContractId(contractId)
			.withType(type)
			.withVersion(version)
			.withStatus(status)
			.withMunicipalityId(municipalityId)
			.withTermGroups(termGroups)
			.withDescription(description)
			.withExtraParameters(extraParameters)
			.withStakeholders(stakeholders)
			.withLeaseType(leaseType)
			.withLeasehold(leasehold)
			.withExternalReferenceId(externalReferenceId)
			.withPropertyDesignations(propertyDesignations)
			.withObjectIdentity(objectIdentity)
			.withLeaseDuration(leaseDuration)
			.withLeaseDurationUnit(leaseDurationUnit)
			.withFees(fees)
			.withInvoicing(InvoicingEmbeddable.builder()
				.withInvoiceInterval(invoiceInterval)
				.withInvoicedIn(invoicedIn)
				.build())
			.withStart(start)
			.withEnd(end)
			.withAutoExtend(autoExtend)
			.withLeaseExtension(leaseExtension)
			.withLeaseExtensionUnit(leaseExtensionUnit)
			.withNoticeTerms(noticeTerms)
			.withArea(area)
			.withAreaData(areaData)
			.build();

		assertThat(contract).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(contract.getId()).isEqualTo(id);
		assertThat(contract.getContractId()).isEqualTo(contractId);
		assertThat(contract.getType()).isEqualTo(type);
		assertThat(contract.getVersion()).isEqualTo(version);
		assertThat(contract.getStatus()).isEqualTo(status);
		assertThat(contract.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(contract.getTermGroups()).isEqualTo(termGroups);
		assertThat(contract.getDescription()).isEqualTo(description);
		assertThat(contract.getExtraParameters()).isEqualTo(extraParameters);
		assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		assertThat(contract.getLeaseType()).isEqualTo(leaseType);
		assertThat(contract.getLeasehold()).isEqualTo(leasehold);
		assertThat(contract.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(contract.getPropertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(contract.getObjectIdentity()).isEqualTo(objectIdentity);
		assertThat(contract.getLeaseDuration()).isEqualTo(leaseDuration);
		assertThat(contract.getFees()).isEqualTo(fees);
		assertThat(contract.getInvoicing()).satisfies(invoicing -> {
			assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval);
			assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn);
		});
		assertThat(contract.getStart()).isEqualTo(start);
		assertThat(contract.getEnd()).isEqualTo(end);
		assertThat(contract.getAutoExtend()).isEqualTo(autoExtend);
		assertThat(contract.getLeaseExtension()).isEqualTo(leaseExtension);
		assertThat(contract.getNoticeTerms()).isEqualTo(noticeTerms);
		assertThat(contract.getArea()).isEqualTo(area);
		assertThat(contract.getAreaData()).isEqualTo(areaData);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContractEntity.builder().build()).hasAllNullFieldsOrPropertiesExcept("signedByWitness", "version");
	}
}
