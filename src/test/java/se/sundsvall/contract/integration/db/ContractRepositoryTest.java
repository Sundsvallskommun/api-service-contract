package se.sundsvall.contract.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.contract.TestFactory.getLandLeaseContractEntity;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.model.enums.LandLeaseType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ContractRepositoryTest {

	@Autowired
	private ContractRepository contractRepository;

	@Test
	void createContract() {
		final var entity = getLandLeaseContractEntity();
		final var savedEntity = contractRepository.save(entity);

		final var result = contractRepository.findById(savedEntity.getId());
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo("2024-12345");
	}

	@Test
	void testFindWithAllParameters() {
		final var request = new ContractRequest("2024-12345", "40f14de6-815d-44b2-a34d-b1d38b628e07",
			"771122-1234", List.of("SUNDSVALL NORRMALM 1:1", "SUNDSVALL NORRMALM 2:1"), "MK-TEST0001",
			LocalDate.of(2023, 10, 10), LandLeaseType.LEASEHOLD.name());

		final var result = contractRepository.findAll(createContractSpecification("1984", request));

		assertThat(result).hasSize(1);
	}

	@Test
	void findByMunicipalityIdAndId() {
		assertThat(contractRepository.findByMunicipalityIdAndId("1984", "2024-12345")).isPresent();
	}

	@Test
	void findByMunicipalityIdAndIdNotFound() {
		assertThat(contractRepository.findByMunicipalityIdAndId("1984", "2024-543210")).isNotPresent();
	}

	@Test
	void testUpdate() {
		final var entity = TestFactory.getLandLeaseContractEntity();

		final var persistedEntity = contractRepository.saveAndFlush(entity);

		assertThat(persistedEntity).usingRecursiveComparison().isEqualTo(entity);
		assertThat(persistedEntity.getId()).isNotBlank();

		persistedEntity.setDescription("Updated description");

		final var updatedEntity = contractRepository.saveAndFlush(persistedEntity);

		assertThat(updatedEntity).usingRecursiveComparison().isEqualTo(persistedEntity);
		assertThat(updatedEntity.getDescription()).isEqualTo("Updated description");
	}

	@Test
	void testDelete() {
		assertThat(contractRepository.findById("2024-23456")).isPresent();

		contractRepository.deleteById("2024-23456");

		assertThat(contractRepository.findById("2024-23456")).isNotPresent();
	}
}
