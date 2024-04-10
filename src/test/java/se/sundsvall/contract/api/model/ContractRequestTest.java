package se.sundsvall.contract.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class ContractRequestTest {

	@Test
	void testRecord() {
		var contractId = "2024-12345";
		var partyId = "partyId";
		var organizationNumber = "organizationNumber";
		var propertyDesignations = List.of("propertyDesignation1", "propertyDesignation2");
		var externalReferenceId = "externalReferenceId";
		var endDate = LocalDate.of(2023, 10, 10);
		var landLeaseType = LandLeaseType.SITELEASEHOLD;

		var request = new ContractRequest(contractId, partyId, organizationNumber, propertyDesignations, externalReferenceId, endDate, landLeaseType.name());

		assertThat(request).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(request.contractId()).isEqualTo(contractId);
		assertThat(request.partyId()).isEqualTo(partyId);
		assertThat(request.organizationNumber()).isEqualTo(organizationNumber);
		assertThat(request.propertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(request.externalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(request.end()).isEqualTo(endDate);
		assertThat(request.landLeaseType()).isEqualTo(landLeaseType.name());
	}

	@Test
	void testContractRequest_landLeaseType_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = ContractRequest.class.getDeclaredField("landLeaseType")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(LandLeaseType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}
}
