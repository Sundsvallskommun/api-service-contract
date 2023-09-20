package se.sundsvall.contract.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.enums.LandLeaseType;

class ContractRequestTest {

	@Test
	void testRecord() {

		final var personId = "personId";
		final var organizationNumber = "organizationNumber";
		final var propertyDesignation = "propertyDesignation";
		final var externalReferenceId = "externalReferenceId";
		final var endDate = "YYYY-MMM-DD";
		final var landLeaseType = LandLeaseType.SITELEASEHOLD;

		final var record = new ContractRequest(personId, organizationNumber, propertyDesignation, externalReferenceId, endDate, landLeaseType);
		assertThat(record).isNotNull().hasNoNullFieldsOrProperties();

		assertThat(record.personId()).isEqualTo(personId);
		assertThat(record.organizationNumber()).isEqualTo(organizationNumber);
		assertThat(record.propertyDesignation()).isEqualTo(propertyDesignation);
		assertThat(record.externalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(record.end()).isEqualTo(endDate);
		assertThat(record.landLeaseType()).isEqualTo(landLeaseType);

	}


}
