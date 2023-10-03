package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.getLandLeaseContract;
import static se.sundsvall.contract.TestFactory.getLandLeaseContractEntity;
import static se.sundsvall.contract.TestFactory.getUpdatedLandLeaseContract;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;

class ContractMapperTest {


	@Test
	void toDto() {

		final var entity = getLandLeaseContractEntity();

		final var result = ContractMapper.toDto(entity);

		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result).usingRecursiveComparison().isEqualTo(entity);
	}

	@Test
	void toEntity() {

		final var dto = getLandLeaseContract();

		final var result = ContractMapper.toEntity(dto);

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id", "attachments.id", "stakeholders.id")
			.isEqualTo(dto);
	}

	@Test
	void updateEntity() {

		final var entity = getLandLeaseContractEntity();
		final var dto = getUpdatedLandLeaseContract();

		final var result = ContractMapper.updateEntity(entity, dto);

		assertThat(result).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id", "attachments.id", "stakeholders.id")
			.isEqualTo(dto);

	}

	@Test
	void toDto_NullValues() {

		final var entity = LandLeaseContractEntity.builder().build();

		final var result = ContractMapper.toDto(entity);

		assertThat(result).usingRecursiveComparison().isEqualTo(entity);
	}

	@Test
	void toEntity_NullValues() {

		final var dto = LandLeaseContract.builder().build();

		final var result = ContractMapper.toEntity(dto);

		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id")
			.isEqualTo(dto);
	}

	@Test
	void updateEntity_NullValues() {

		final var entity = LandLeaseContractEntity.builder().build();
		final var dto = LandLeaseContract.builder().build();

		final var result = ContractMapper.updateEntity(entity, dto);

		assertThat(result).usingRecursiveComparison()
			.ignoringFields("id")
			.isEqualTo(dto);
	}

}
