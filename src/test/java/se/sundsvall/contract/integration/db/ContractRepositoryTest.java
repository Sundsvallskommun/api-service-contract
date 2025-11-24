package se.sundsvall.contract.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static se.sundsvall.contract.TestFactory.createContractEntity;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
	"/db/scripts/testdata-junit.sql"
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
			.withTerm("No pole vaulting indoors")
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

	@Test
	void testFindContractWithHeaderTerm() {
		var contractRequest = ContractRequest.builder().build();
		contractRequest.setTerm("More information");

		var result = contractRepository.findAll(createContractSpecification("1984", contractRequest));

		assertThat(result).hasSize(1);
	}

	/**
	 * Tests that we can find values in the index_terms and additional_terms fields: "header", "description" and
	 * "term". The values are unique for each test case (except for the first one) to make sure we find the correct values.
	 * 
	 * @param term          what to search for
	 * @param expectedCount expected number of contracts found
	 */
	@ParameterizedTest
	@MethodSource("termsProvider")
	void testFindContracts(final String term, final int expectedCount) {
		var contractRequest = ContractRequest.builder().build();
		contractRequest.setTerm(term);

		var result = contractRepository.findAll(createContractSpecification("1984", contractRequest));

		assertThat(result).hasSize(expectedCount);
	}

	private static Stream<Arguments> termsProvider() {
		// term, expected count
		return Stream.of(
			Arguments.of("Basic", 2),                   // Matches on both index_terms and additional_terms: "header": "Additional Basic Terms"
			Arguments.of("Terms here", 1),              // Matches on index_terms: "header": "Basic Terms Here"
			Arguments.of("description for", 1),         // Matches on index_terms: "description": "Description for basic terms"
			Arguments.of("Some Parties", 1),            // Matches on index_terms: "term": "Some Parties"
			Arguments.of("Additional Basic", 1),        // Matches on additional_terms: "header": "Additional Basic Terms"
			Arguments.of("pöle vaulting", 1),           // Matches on additional_terms: "description": "No pöle vaulting indoors"
			Arguments.of("Respected", 1),               // Matches on additional_terms: "term": "Respected by all parties"
			Arguments.of("you have no power here", 0)   // No match..
		);
	}

}
