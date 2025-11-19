package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class ContractRequestTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ContractRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("limit", "page"),
			hasValidBeanEqualsExcluding("limit", "page"),
			hasValidBeanToStringExcluding("limit", "page")));
	}

	@Test
	void testBuilderMethods() {
		var contractId = "2024-12345";
		var partyId = "partyId";
		var organizationNumber = "organizationNumber";
		var propertyDesignations = List.of("propertyDesignation1", "propertyDesignation2");
		var externalReferenceId = "externalReferenceId";
		var endDate = LocalDate.of(2023, 10, 10);
		var leaseType = LeaseType.SITE_LEASE_COMMERCIAL;
		var term = "term";

		var request = ContractRequest.builder()
			.withContractId(contractId)
			.withPartyId(partyId)
			.withOrganizationNumber(organizationNumber)
			.withPropertyDesignations(propertyDesignations)
			.withExternalReferenceId(externalReferenceId)
			.withEnd(endDate)
			.withLeaseType(leaseType.name())
			.withTerm(term)
			.build();

		assertThat(request).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(request.getContractId()).isEqualTo(contractId);
		assertThat(request.getPartyId()).isEqualTo(partyId);
		assertThat(request.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(request.getPropertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(request.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(request.getEnd()).isEqualTo(endDate);
		assertThat(request.getLeaseType()).isEqualTo(leaseType.name());
		assertThat(request.getTerm()).isEqualTo(term);
	}

	@Test
	void testContractRequest_landLeaseType_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = ContractRequest.class.getDeclaredField("leaseType")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(LeaseType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}
}
