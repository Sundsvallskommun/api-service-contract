package se.sundsvall.contract.integration.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.ContractType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.contract.TestFactory.createContractEntity;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withMunicipalityId;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class ContractRepositoryTest {

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private AttachmentRepository attachmentRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void createContract() {
		var entity = createContractEntity();
		entity.getStakeholders().getFirst().setId(null);    // Clear the id
		var savedEntity = contractRepository.save(entity);

		var result = contractRepository.findById(savedEntity.getId());
		assertThat(result).isPresent();
	}

	@Test
	void testFindWithMunicipalityId() {
		var specification = withMunicipalityId("1984");

		var result = contractRepository.findAll(specification);

		assertThat(result).isNotEmpty();
	}

	@Test
	void findByMunicipalityIdAndContractId() {
		assertThat(contractRepository.findByMunicipalityIdAndContractId("1984", "2024-12345")).isPresent();
	}

	@Test
	void findByMunicipalityIdAndContractIdNotFound() {
		assertThat(contractRepository.findByMunicipalityIdAndContractId("1984", "9999-99999")).isNotPresent();
	}

	@Test
	void testDeleteAllByMunicipalityIdAndContractId() {
		attachmentRepository.deleteAllByMunicipalityIdAndContractId("1984", "2024-12345");

		assertThat(contractRepository.findByMunicipalityIdAndContractId("1984", "2024-12345")).isPresent();
		contractRepository.deleteAllByMunicipalityIdAndContractId("1984", "2024-12345");
		assertThat(contractRepository.findByMunicipalityIdAndContractId("1984", "2024-12345")).isNotPresent();
	}

	@Test
	void updatePersistsChangedType() {
		final var contract = contractRepository.findByMunicipalityIdAndContractId("1984", "2024-12345").orElseThrow();
		assertThat(contract.getType()).isEqualTo(ContractType.PURCHASE_AGREEMENT);

		contract.setType(ContractType.LEASE_AGREEMENT);
		contractRepository.save(contract);

		// Force a round-trip to the database so a regression to a non-updatable `type` column would be caught
		entityManager.flush();
		entityManager.clear();

		assertThat(contractRepository.findByMunicipalityIdAndContractId("1984", "2024-12345"))
			.get()
			.extracting(ContractEntity::getType)
			.isEqualTo(ContractType.LEASE_AGREEMENT);
	}
}
