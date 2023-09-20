package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.contract.api.model.ContractHolder;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.enums.LandLeaseType;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	@InjectMocks
	private ContractService contractService;

	@Test
	void createContract() {

		final var contract = LandLeaseContract.builder()
			.withCaseId(1L)
			.build();

		final var result = contractService.createContract(contract);

		assertThat(result).isEqualTo(1L);
	}

	@Test
	void getContract() {

		final var result = contractService.getContract(1L);

		assertThat(result).isNotNull();
		assertThat(result.getCaseId()).isEqualTo(1L);
	}

	@Test
	void getContracts() {

		final var request = new ContractRequest("propertyDesignation", "organizationNumber", "propertyDesignation", "externalReferenceId", " yyyy-MM-dd", LandLeaseType.SITELEASEHOLD);

		final var result = contractService.getContracts(request);

		assertThat(result).isNotNull().hasSize(1).element(0).isNotNull();
		assertThat(result.get(0).getPropertyDesignation()).isEqualTo("propertyDesignation");
	}

	@Test
	void updateContract() {

		final var contractHolder = ContractHolder.builder()
			.withLandLeaseContracts(List.of(LandLeaseContract.builder()
				.withCaseId(1L)
				.build()))
			.build();

		contractService.updateContract(1L, contractHolder);
	}

}
