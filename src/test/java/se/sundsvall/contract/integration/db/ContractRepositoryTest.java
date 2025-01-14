package se.sundsvall.contract.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.contract.TestFactory.createContractEntity;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.contract.api.model.ContractRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
@Import(ObjectMapper.class) // Needed since we inject an ObjectMapper in the ExtraParameterGroupConverter
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
	void testFindWithAllParameters() {
		var request = ContractRequest.builder()
			.withContractId("2024-12345")
			.withPartyId("40f14de6-815d-44b2-a34d-b1d38b628e07")
			.withOrganizationNumber("771122-1234")
			.withPropertyDesignations(List.of("SUNDSVALL NORRMALM 1:1", "SUNDSVALL NORRMALM 2:1"))
			.withExternalReferenceId("MK-TEST0001")
			.withEnd(LocalDate.of(2023, 10, 10))
			.build();

		var result = contractRepository.findAll(createContractSpecification("1984", request));

		assertThat(result).hasSize(1);
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
		attachmentRepository.deleteAllByContractId("2024-12345");

		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 1)).isPresent();
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 2)).isPresent();
		contractRepository.deleteAllByMunicipalityIdAndContractId("1984", "2024-12345");
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 1)).isNotPresent();
		assertThat(contractRepository.findByMunicipalityIdAndContractIdAndVersion("1984", "2024-12345", 2)).isNotPresent();
	}
}
