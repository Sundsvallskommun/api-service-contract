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
import static se.sundsvall.contract.model.enums.ContractType.LAND_LEASE;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.model.enums.LandLeaseType.SITELEASEHOLD;
import static se.sundsvall.contract.model.enums.Status.TERMINATED;
import static se.sundsvall.contract.model.enums.UsufructType.FISHING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;

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
		var type = LAND_LEASE;
		var version = 1;
		var status = TERMINATED;
		var municipalityId = "1984";
		var contractId = "2024-12345";
		var id = 1L;
		var indexTerms = List.of(
			TermGroup.builder()
				.withHeader("Some index terms")
				.withTerms(List.of(
					Term.builder()
						.withName("Some index term")
						.withDescription("Some description")
						.build()))
				.build());
		var description = "description";
		var additionalTerms = List.of(
			TermGroup.builder()
				.withHeader("Some additional terms")
				.withTerms(List.of(
					Term.builder()
						.withName("Some additional term")
						.withDescription("Some description")
						.build()))
				.build());
		var extraParameters = List.of(
			ExtraParameterGroup.builder()
				.withName("someExtraParameterGroup")
				.withParameters(Map.of("someParameter", "someValue"))
				.build());
		var stakeholders = List.of(StakeholderEntity.builder().build());
		var landLeaseType = SITELEASEHOLD;
		var leasehold = LeaseholdEntity.builder().build();
		var usufructType = FISHING;
		var externalReferenceId = "externalReferenceId";
		var propertyDesignations = List.of("propertyDesignations", "otherPropertyDesignation");
		var objectIdentity = "objectIdentity";
		var leaseDuration = 3;
		var fees = Fees.builder()
			.withCurrency("SEK")
			.withYearly(BigDecimal.valueOf(4350))
			.withMonthly(BigDecimal.valueOf(375))
			.withTotal(BigDecimal.valueOf(52200))
			.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
			.withIndexYear(2023)
			.withIndexNumber(2)
			.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
			.build();
		var invoiceInterval = QUARTERLY;
		var invoicedIn = ADVANCE;
		var start = now();
		var end = now();
		var autoExtend = true;
		var leaseExtension = 4;
		var periodOfNotice = 1;
		var area = 1;
		var areaData = new FeatureCollection();

		var contract = ContractEntity.builder()
			.withId(id)
			.withContractId(contractId)
			.withType(type)
			.withVersion(version)
			.withStatus(status)
			.withMunicipalityId(municipalityId)
			.withIndexTerms(indexTerms)
			.withDescription(description)
			.withAdditionalTerms(additionalTerms)
			.withExtraParameters(extraParameters)
			.withStakeholders(stakeholders)
			.withLandLeaseType(landLeaseType)
			.withLeasehold(leasehold)
			.withUsufructType(usufructType)
			.withExternalReferenceId(externalReferenceId)
			.withPropertyDesignations(propertyDesignations)
			.withObjectIdentity(objectIdentity)
			.withLeaseDuration(leaseDuration)
			.withFees(fees)
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
		assertThat(contract.getContractId()).isEqualTo(contractId);
		assertThat(contract.getType()).isEqualTo(type);
		assertThat(contract.getVersion()).isEqualTo(version);
		assertThat(contract.getStatus()).isEqualTo(status);
		assertThat(contract.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(contract.getIndexTerms()).isEqualTo(indexTerms);
		assertThat(contract.getDescription()).isEqualTo(description);
		assertThat(contract.getAdditionalTerms()).isEqualTo(additionalTerms);
		assertThat(contract.getExtraParameters()).isEqualTo(extraParameters);
		assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		assertThat(contract.getLandLeaseType()).isEqualTo(landLeaseType);
		assertThat(contract.getLeasehold()).isEqualTo(leasehold);
		assertThat(contract.getUsufructType()).isEqualTo(usufructType);
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
		assertThat(contract.getPeriodOfNotice()).isEqualTo(periodOfNotice);
		assertThat(contract.getArea()).isEqualTo(area);
		assertThat(contract.getAreaData()).isEqualTo(areaData);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContractEntity.builder().build()).hasAllNullFieldsOrPropertiesExcept("signedByWitness", "version");
	}
}
