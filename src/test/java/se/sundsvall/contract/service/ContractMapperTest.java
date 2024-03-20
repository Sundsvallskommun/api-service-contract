package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.createLandLeaseContract;
import static se.sundsvall.contract.TestFactory.createLandLeaseContractEntity;
import static se.sundsvall.contract.TestFactory.createUpdatedLandLeaseContract;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;

class ContractMapperTest {

	private final ContractMapper contractMapper = new ContractMapper();

	@Test
	void toContractDto() {
		var entity = createLandLeaseContractEntity();

		var result = contractMapper.toContractDto(entity, List.of());

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("type");
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("type", "attachments")
			.isEqualTo(entity);
	}

	@Test
	void toContractDto_NullValues() {
		var entity = LandLeaseContractEntity.builder().build();

		var result = contractMapper.toContractDto(entity, null);

		assertThat(result).isNotNull().hasAllNullFieldsOrPropertiesExcept("type", "version", "signedByWitness");
	}

	@Test
	void toContractEntity() {
		var dto = createLandLeaseContract();

		var result = contractMapper.toContractEntity("1984", dto);

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "type", "attachments");
		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id", "type", "attachments", "stakeholders.id", "leaseFees.landLeaseContractId")
			.withEnumStringComparison()
			.isEqualTo(dto);
	}

	@Test
	void updateContractEntity() {
		var entity = createLandLeaseContractEntity();
		var dto = createUpdatedLandLeaseContract();

		var result = contractMapper.updateContractEntity(entity, dto);

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "type", "attachments", "contractId");
		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id", "type", "attachments", "stakeholders.id", "leaseFees.landLeaseContractId", "contractId")
			.withEnumStringComparison()
			.isEqualTo(dto);
	}

	@Test
	void toContractEntity_NullValues() {
		var dto = LandLeaseContract.builder().build();

		var result = contractMapper.toContractEntity("1984", dto);

		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("id", "municipalityId", "attachments", "version")
			.isEqualTo(dto);
	}

	@Test
	void updateContractEntity_NullValues() {
		var entity = LandLeaseContractEntity.builder().build();
		var dto = LandLeaseContract.builder().build();

		var result = contractMapper.updateContractEntity(entity, dto);

		assertThat(result)
			.usingRecursiveComparison()
			.ignoringFields("type", "id", "version", "attachments")
			.isEqualTo(dto);
	}
}
