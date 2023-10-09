package se.sundsvall.contract.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.contract.TestFactory.getLandLeaseContractEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.integration.db.specification.ContractSpecification;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/ContractRepositoryTest.sql"
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
		assertThat(result.get().getId()).isEqualTo(1L);
	}

	@Test
	void testFindWithAllParameters() {

		final var request = new ContractRequest("40f14de9-815d-44a5-a34d-b1d38b628e07"
			, "771122-1234", "SUNDSVALL GRANLO 2:1", "MK-TEST0001"
			, "2023-10-10", LandLeaseType.LEASEHOLD);

		final var result = contractRepository.findAll(ContractSpecification.createContractSpecification(request));
		assertThat(result).hasSize(1);
	}

	@Test
	void findByID() {
		assertThat(contractRepository.findById(1L)).isPresent();
	}

	@Test
	void findByIdNotFound() {
		assertThat(contractRepository.findById(123L)).isNotPresent();
	}


	@Test
	void testUpdate() {
		final var entity = TestFactory.getLandLeaseContractEntity();

		final var persistedEntity = contractRepository.saveAndFlush(entity);

		assertThat(persistedEntity).usingRecursiveComparison().isEqualTo(entity);
		assertThat(persistedEntity.getId()).isNotZero();

		persistedEntity.setDescription("Updated description");

		final var updatedEntity = contractRepository.saveAndFlush(persistedEntity);

		assertThat(updatedEntity).usingRecursiveComparison().isEqualTo(persistedEntity);
		assertThat(updatedEntity.getDescription()).isEqualTo("Updated description");
	}

	@Test
	void testDelete() {
		assertThat(contractRepository.findById(2L)).isPresent();

		contractRepository.deleteById(2L);

		assertThat(contractRepository.findById(2L)).isNotPresent();
	}

}
