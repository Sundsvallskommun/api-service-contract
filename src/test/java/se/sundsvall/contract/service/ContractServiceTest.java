package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.TestFactory.getLandLeaseContract;
import static se.sundsvall.contract.TestFactory.getLandLeaseContractEntity;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	@Mock
	private ContractRepository contractRepository;

	@InjectMocks
	private ContractService contractService;

	@Test
	void createContract() {

		final var contract = LandLeaseContract.builder()
			.withCaseId(1L)
			.withLandLeaseType(LandLeaseType.SITELEASEHOLD.name())
			.withUsufructType(UsufructType.HUNTING.name())
			.withInvoiceInterval(IntervalType.QUARTERLY.name())
			.withStatus(Status.ACTIVE.name())
			.build();

		when(contractRepository.save(any(LandLeaseContractEntity.class))).thenReturn(LandLeaseContractEntity.builder().withId(1L).build());

		final var result = contractService.createContract("1984", contract);

		assertThat(result).isEqualTo(1L);

		verify(contractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void getContract() {

		final var entity = getLandLeaseContractEntity();
		when(contractRepository.findById(any(Long.class))).thenReturn(Optional.of(entity));

		final var result = contractService.getContract("1984", 1L);

		assertThat(result).isNotNull();
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.isEqualTo(entity);

		verify(contractRepository).findById(any(Long.class));
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void getContracts() {
		final var entity = getLandLeaseContractEntity();
		when(contractRepository.findAll(Mockito.<Specification<ContractEntity>>any())).thenReturn(List.of(entity));

		final var request = new ContractRequest("propertyDesignation", "organizationNumber", "propertyDesignation", "externalReferenceId", " yyyy-MM-dd", LandLeaseType.SITELEASEHOLD.name());

		final var result = contractService.getContracts("1984", request);

		assertThat(result).isNotNull().hasSize(1).element(0).isNotNull();
		assertThat(result.getFirst())
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.isEqualTo(entity);

		verify(contractRepository).findAll(Mockito.<Specification<ContractEntity>>any());
		verifyNoMoreInteractions(contractRepository);
	}

	@Test
	void updateContract() {
		try (var mapper = mockStatic(ContractMapper.class)) {
			mapper.when(() -> ContractMapper.toDto(any(LandLeaseContractEntity.class))).thenCallRealMethod();
			mapper.when(() -> ContractMapper.updateEntity(any(LandLeaseContractEntity.class), any(LandLeaseContract.class))).thenCallRealMethod();

			final var entity = getLandLeaseContractEntity();
			when(contractRepository.findById(any(Long.class))).thenReturn(Optional.of(entity));
			final var contract = getLandLeaseContract();

			contractService.updateContract("1984", 1L, contract);

			verify(contractRepository).findById(any(Long.class));
			verify(contractRepository).save(any(LandLeaseContractEntity.class));
			verifyNoMoreInteractions(contractRepository);
		}
	}
}
