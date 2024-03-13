package se.sundsvall.contract.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class ContractRequestTest {

	@Test
	void testRecord() {

		final var personId = "personId";
		final var organizationNumber = "organizationNumber";
		final var propertyDesignations = List.of("propertyDesignation1", "propertyDesignation2");
		final var externalReferenceId = "externalReferenceId";
		final var endDate = LocalDate.of(2023, 10, 10);
		final var landLeaseType = LandLeaseType.SITELEASEHOLD;

		final var record = new ContractRequest(personId, organizationNumber, propertyDesignations, externalReferenceId, endDate, landLeaseType.name());
		assertThat(record).isNotNull().hasNoNullFieldsOrProperties();

		assertThat(record.personId()).isEqualTo(personId);
		assertThat(record.organizationNumber()).isEqualTo(organizationNumber);
		assertThat(record.propertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(record.externalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(record.end()).isEqualTo(endDate);
		assertThat(record.landLeaseType()).isEqualTo(landLeaseType.name());
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
