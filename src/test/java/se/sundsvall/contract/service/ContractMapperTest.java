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
		//Arrange
		var entity = createLandLeaseContractEntity();

		//Act
		var result = contractMapper.toContractDto(entity, List.of());

		//Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("type");
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("type", "attachments", "attachmentMetaData")
			.isEqualTo(entity);
	}

	@Test
	void toContractDto_NullValues() {
		//Arrange
		var entity = LandLeaseContractEntity.builder().build();

		//Act
		var result = contractMapper.toContractDto(entity, null);

		//Assert
		assertThat(result).isNotNull().hasAllNullFieldsOrPropertiesExcept("type", "version", "signedByWitness");
	}

	@Test
	void toContractEntity() {
		//Arrange
		var dto = createLandLeaseContract();

		//Act
		var result = contractMapper.toContractEntity("1984", dto);

		//Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "type", "attachments");
		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id", "type", "attachments", "stakeholders.id", "leaseFees.landLeaseContractId")
			.withEnumStringComparison()
			.isEqualTo(dto);
	}

	@Test
	void updateContractEntity() {
		//Arrange
		var entity = createLandLeaseContractEntity();
		var dto = createUpdatedLandLeaseContract();

		//Act
		var result = contractMapper.updateContractEntity(entity, dto);

		//Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id", "type", "attachments", "contractId");
		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id", "type", "attachments", "stakeholders.id", "leaseFees.landLeaseContractId", "contractId")
			.withEnumStringComparison()
			.isEqualTo(dto);
	}

	@Test
	void toContractEntity_NullValues() {
		//Arrange
		var dto = LandLeaseContract.builder().build();

		//Act
		var result = contractMapper.toContractEntity("1984", dto);

		//Assert
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("id", "municipalityId", "attachments", "version")
			.isEqualTo(dto);
	}

	@Test
	void updateContractEntity_NullValues() {
		//Arrange
		var entity = LandLeaseContractEntity.builder().build();
		var dto = LandLeaseContract.builder().build();

		//Act
		var result = contractMapper.updateContractEntity(entity, dto);

		//Assert
		assertThat(result)
			.usingRecursiveComparison()
			.ignoringFields("type", "id", "version", "attachments")
			.isEqualTo(dto);
	}
}
