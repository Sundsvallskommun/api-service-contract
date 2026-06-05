package se.sundsvall.contract.api.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
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
		// PatchContract fields are JsonNullable<?>; generate distinct present values so the bean matchers can
		// exercise getters/setters, equals, hashCode and toString.
		registerValueGenerator(() -> JsonNullable.of(UUID.randomUUID().toString()), JsonNullable.class);
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
			.withDescription(JsonNullable.of(description))
			.withExternalReferenceId(JsonNullable.of(externalReferenceId))
			.withLeaseType(JsonNullable.of(leaseType))
			.withObjectIdentity(JsonNullable.of(objectIdentity))
			.withStatus(JsonNullable.of(status))
			.withType(JsonNullable.of(type))
			.withLeasehold(JsonNullable.of(leasehold))
			.withAdditionalTerms(JsonNullable.of(additionalTerms))
			.withExtraParameters(JsonNullable.of(extraParameters))
			.withIndexTerms(JsonNullable.of(indexTerms))
			.withPropertyDesignations(JsonNullable.of(propertyDesignations))
			.withStakeholders(JsonNullable.of(stakeholders))
			.withExtension(JsonNullable.of(extension))
			.withFees(JsonNullable.of(fees))
			.withInvoicing(JsonNullable.of(invoicing))
			.withStartDate(JsonNullable.of(startDate))
			.withEndDate(JsonNullable.of(endDate))
			.withNotice(JsonNullable.of(notice))
			.withCurrentPeriod(JsonNullable.of(currentPeriod))
			.withArea(JsonNullable.of(area))
			.withSignedByWitness(JsonNullable.of(signedByWitness))
			.withAreaData(JsonNullable.of(areaData))
			.build();

		assertThat(patch.getDescription()).isEqualTo(JsonNullable.of(description));
		assertThat(patch.getExternalReferenceId()).isEqualTo(JsonNullable.of(externalReferenceId));
		assertThat(patch.getLeaseType()).isEqualTo(JsonNullable.of(leaseType));
		assertThat(patch.getObjectIdentity()).isEqualTo(JsonNullable.of(objectIdentity));
		assertThat(patch.getStatus()).isEqualTo(JsonNullable.of(status));
		assertThat(patch.getType()).isEqualTo(JsonNullable.of(type));
		assertThat(patch.getLeasehold()).isEqualTo(JsonNullable.of(leasehold));
		assertThat(patch.getAdditionalTerms()).isEqualTo(JsonNullable.of(additionalTerms));
		assertThat(patch.getExtraParameters()).isEqualTo(JsonNullable.of(extraParameters));
		assertThat(patch.getIndexTerms()).isEqualTo(JsonNullable.of(indexTerms));
		assertThat(patch.getPropertyDesignations()).isEqualTo(JsonNullable.of(propertyDesignations));
		assertThat(patch.getStakeholders()).isEqualTo(JsonNullable.of(stakeholders));
		assertThat(patch.getExtension()).isEqualTo(JsonNullable.of(extension));
		assertThat(patch.getFees()).isEqualTo(JsonNullable.of(fees));
		assertThat(patch.getInvoicing()).isEqualTo(JsonNullable.of(invoicing));
		assertThat(patch.getStartDate()).isEqualTo(JsonNullable.of(startDate));
		assertThat(patch.getEndDate()).isEqualTo(JsonNullable.of(endDate));
		assertThat(patch.getNotice()).isEqualTo(JsonNullable.of(notice));
		assertThat(patch.getCurrentPeriod()).isEqualTo(JsonNullable.of(currentPeriod));
		assertThat(patch.getArea()).isEqualTo(JsonNullable.of(area));
		assertThat(patch.getSignedByWitness()).isEqualTo(JsonNullable.of(signedByWitness));
		assertThat(patch.getAreaData()).isEqualTo(JsonNullable.of(areaData));
	}

	@Test
	void testNoDirtOnCreatedBean() {
		// A freshly built patch has every field "undefined" (absent), i.e. nothing is set and nothing is cleared.
		final var patch = PatchContract.builder().build();
		assertThat(patch).hasNoNullFieldsOrProperties();
		assertThat(patch.getDescription()).isEqualTo(JsonNullable.undefined());
		assertThat(patch.getFees()).isEqualTo(JsonNullable.undefined());
		assertThat(patch.getStakeholders()).isEqualTo(JsonNullable.undefined());
	}
}
