package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.TestFactory.getLandLeaseContractEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.Problem;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private ContractRepository contractRepository;

	@InjectMocks
	private ContractService contractService;

	@Test
	void createContract() {

		final var contract = LandLeaseContract.builder()
			.withCaseId(1L)
			.build();

		when(contractRepository.save(any(LandLeaseContractEntity.class))).thenReturn(LandLeaseContractEntity.builder().withId(1L).build());

		final var result = contractService.createContract(contract);

		assertThat(result).isEqualTo(1L);

		verify(contractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void getContract() {

		final var entity = getLandLeaseContractEntity();
		when(contractRepository.findById(any(Long.class))).thenReturn(Optional.of(entity));

		final var result = contractService.getContract(1L);

		assertThat(result).isNotNull();
		assertThat(result).usingRecursiveComparison().isEqualTo(entity);

		verify(contractRepository).findById(any(Long.class));
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void getContracts() {
		final var entity = getLandLeaseContractEntity();
		when(contractRepository.findAll(Mockito.<Specification<ContractEntity>>any())).thenReturn(List.of(entity));
		final var request = new ContractRequest("propertyDesignation", "organizationNumber", "propertyDesignation", "externalReferenceId", " yyyy-MM-dd", LandLeaseType.SITELEASEHOLD);
		final var result = contractService.getContracts(request);

		assertThat(result).isNotNull().hasSize(1).element(0).isNotNull();
		assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(entity);

		verify(contractRepository).findAll(Mockito.<Specification<ContractEntity>>any());
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void updateContract() throws IOException {

		final var entity = getLandLeaseContractEntity();
		when(contractRepository.findById(any(Long.class))).thenReturn(Optional.of(entity));
		final var jsonPatchArray = "[{ \"op\": \"replace\", \"path\": \"/propertyDesignation\", \"value\": \"myPatchedPropertyDesignation\" }]";
		final var patch = JsonPatch.fromJson(objectMapper.readTree(jsonPatchArray));

		contractService.updateContract(1L, patch);

		verify(contractRepository).findById(any(Long.class));
		verify(contractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void updateContract_NotFound() throws IOException {

		final var jsonPatchArray = "[{ \"op\": \"replace\", \"path\": \"/propertyDesignation\", \"value\": \"myNewValue\" }]";
		final var patch = JsonPatch.fromJson(objectMapper.readTree(jsonPatchArray));

		assertThatThrownBy(() -> contractService.updateContract(1L, patch))
			.isInstanceOf(Problem.class)
			.hasMessage("Not Found: Contract with id 1 not found");

		verify(contractRepository).findById(any(Long.class));
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void updateContract_InvalidPatch() throws IOException {

		final var entity = getLandLeaseContractEntity();
		final var jsonPatchArray = "[{ \"op\": \"replace\", \"path\": \"/InvalidPath\", \"value\": \"invalidValue\" }]";
		final var patch = JsonPatch.fromJson(objectMapper.readTree(jsonPatchArray));

		when(contractRepository.findById(any(Long.class))).thenReturn(Optional.of(entity));

		assertThatThrownBy(() -> contractService.updateContract(1L, patch))
			.isInstanceOf(Problem.class)
			.hasMessage("Internal Server Error: Failed to apply patch [op: replace; path: \"/InvalidPath\"; " +
				"value: \"invalidValue\"] with exception no such path in target JSON document");

		verify(contractRepository).findById(any(Long.class));
		verifyNoMoreInteractions(contractRepository);
	}

}
