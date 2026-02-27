package se.sundsvall.contract.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.contract.TestFactory.createContractEntity;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withMunicipalityId;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withOnlyLatestVersion;

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

	@Test
	void createContract() {
		var entity = createContractEntity();
		entity.getStakeholders().getFirst().setId(null);    // Clear the id
		var savedEntity = contractRepository.save(entity);

		var result = contractRepository.findById(savedEntity.getId());
		assertThat(result).isPresent();
	}

	@Test
	void testFindWithMunicipalityIdAndLatestVersion() {
		var specification = withOnlyLatestVersion().and(withMunicipalityId("1984"));

		var result = contractRepository.findAll(specification);

		assertThat(result).isNotEmpty();
	}

	@Test
	void findByMunicipalityIdAndIdAndVersion() {
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 1)).isPresent();
	}

	@Test
	void findByMunicipalityIdAndIdAndVersionNotFound() {
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 12345)).isNotPresent();
	}

	@Test
	void testDeleteAllByMunicipalityIdAndContractId() {
		attachmentRepository.deleteAllByMunicipalityIdAndContractId("1984", "2024-12345");

		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 1)).isPresent();
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 2)).isPresent();
		contractRepository.deleteAllByMunicipalityIdAndContractId("1984", "2024-12345");
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 1)).isNotPresent();
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 2)).isNotPresent();
	}
}
