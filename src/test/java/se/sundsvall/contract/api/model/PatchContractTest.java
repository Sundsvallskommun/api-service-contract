package se.sundsvall.contract.api.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.TermGroup;

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
import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.LeaseType.LAND_LEASE_MISC;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;

class PatchContractTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(PatchContract.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testSetAndGetAllFields() {

		final var description = "A simple description of the contract";
		final var externalReferenceId = "123";
		final var leaseType = LAND_LEASE_MISC;
		final var objectIdentity = "909a6a80-d1a4-90ec-e040-ed8f66444c3f";
		final var status = ACTIVE;
		final var type = PURCHASE_AGREEMENT;
		final var leasehold = new Leasehold();
		final var additionalTerms = List.of(new TermGroup());
		final var extraParameters = List.of(new ExtraParameterGroup());
		final var indexTerms = List.of(new TermGroup());
		final var propertyDesignations = List.of(new PropertyDesignation("SUNDSVALL NORRMALM 1:1", "Sundsvall"));
		final var stakeholders = List.of(new Stakeholder());
		final var notice = Notice.builder().build();
		final var extension = Extension.builder().build();
		final var fees = Fees.builder().build();
		final var invoicing = new Invoicing();
		final var startDate = LocalDate.of(2020, 1, 1);
		final var endDate = LocalDate.of(2022, 12, 31);
		final var currentPeriod = Period.builder().build();
		final var area = 150;
		final var signedByWitness = Boolean.TRUE;
		final var areaData = new FeatureCollection();

		final var patch = PatchContract.builder()
			.withDescription(description)
			.withExternalReferenceId(externalReferenceId)
			.withLeaseType(leaseType)
			.withObjectIdentity(objectIdentity)
			.withStatus(status)
			.withType(type)
			.withLeasehold(leasehold)
			.withAdditionalTerms(additionalTerms)
			.withExtraParameters(extraParameters)
			.withIndexTerms(indexTerms)
			.withPropertyDesignations(propertyDesignations)
			.withStakeholders(stakeholders)
			.withExtension(extension)
			.withFees(fees)
			.withInvoicing(invoicing)
			.withStartDate(startDate)
			.withEndDate(endDate)
			.withNotice(notice)
			.withCurrentPeriod(currentPeriod)
			.withArea(area)
			.withSignedByWitness(signedByWitness)
			.withAreaData(areaData)
			.build();

		assertThat(patch.getDescription()).isEqualTo(description);
		assertThat(patch.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(patch.getLeaseType()).isEqualTo(leaseType);
		assertThat(patch.getObjectIdentity()).isEqualTo(objectIdentity);
		assertThat(patch.getStatus()).isEqualTo(status);
		assertThat(patch.getType()).isEqualTo(type);
		assertThat(patch.getLeasehold()).isEqualTo(leasehold);
		assertThat(patch.getAdditionalTerms()).isEqualTo(additionalTerms);
		assertThat(patch.getExtraParameters()).isEqualTo(extraParameters);
		assertThat(patch.getIndexTerms()).isEqualTo(indexTerms);
		assertThat(patch.getPropertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(patch.getStakeholders()).isEqualTo(stakeholders);
		assertThat(patch.getExtension()).isEqualTo(extension);
		assertThat(patch.getFees()).isEqualTo(fees);
		assertThat(patch.getInvoicing()).isEqualTo(invoicing);
		assertThat(patch.getStartDate()).isEqualTo(startDate);
		assertThat(patch.getEndDate()).isEqualTo(endDate);
		assertThat(patch.getNotice()).isEqualTo(notice);
		assertThat(patch.getCurrentPeriod()).isEqualTo(currentPeriod);
		assertThat(patch.getArea()).isEqualTo(area);
		assertThat(patch.getSignedByWitness()).isEqualTo(signedByWitness);
		assertThat(patch.getAreaData()).isEqualTo(areaData);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PatchContract.builder().build()).hasAllNullFieldsOrProperties();
	}
}
