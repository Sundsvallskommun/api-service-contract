package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.TestFactory.getLandLeaseContract;
import static se.sundsvall.contract.TestFactory.getLandLeaseContractEntity;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.LandLeaseType.SITELEASEHOLD;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.UsufructType.HUNTING;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	@Mock
	private ContractRepository mockContractRepository;

	@InjectMocks
	private ContractService contractService;

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	@Test
	void createContract() {
		final var contract = LandLeaseContract.builder()
			.withCaseId(1L)
			.withLandLeaseType(SITELEASEHOLD.name())
			.withUsufructType(HUNTING.name())
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY.name())
				.withInvoicedIn(ARREARS.name())
				.build())
			.withStatus(ACTIVE.name())
			.build();

		when(mockContractRepository.save(any(LandLeaseContractEntity.class)))
			.thenReturn(LandLeaseContractEntity.builder().withContractId(CONTRACT_ID).build());

		final var result = contractService.createContract(MUNICIPALITY_ID, contract);

		assertThat(result).isEqualTo(CONTRACT_ID);

		verify(mockContractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void getContract() {
		final var entity = getLandLeaseContractEntity();
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.of(entity));

  		final var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID);

		assertThat(result).isNotNull();
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.isEqualTo(entity);

		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void testGetContract_shouldThrow404_whenNoMatch() {
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void getContracts() {
		final var entity = getLandLeaseContractEntity();
		when(mockContractRepository.findAll(Mockito.<Specification<ContractEntity>>any())).thenReturn(List.of(entity));

		final var request = new ContractRequest("contractId", "propertyDesignation", "organizationNumber",
			List.of("propertyDesignation1", "propertyDesignation2"), "externalReferenceId", LocalDate.of(2023, 10, 10), SITELEASEHOLD.name());

		final var result = contractService.getContracts(MUNICIPALITY_ID, request);

		assertThat(result).isNotNull().hasSize(1).element(0).isNotNull();
		assertThat(result.getFirst())
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.isEqualTo(entity);

		verify(mockContractRepository).findAll(Mockito.<Specification<ContractEntity>>any());
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void updateContract() {
		try (var mapper = mockStatic(ContractMapper.class)) {
			mapper.when(() -> ContractMapper.toDto(any(LandLeaseContractEntity.class))).thenCallRealMethod();
			mapper.when(() -> ContractMapper.updateEntity(any(LandLeaseContractEntity.class), any(LandLeaseContract.class))).thenCallRealMethod();

			final var entity = getLandLeaseContractEntity();
			when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(any(String.class), any(String.class))).thenReturn(Optional.of(entity));
			final var contract = getLandLeaseContract();

			contractService.updateContract("1984", "2024-12345", contract);

			verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
			verify(mockContractRepository).save(any(LandLeaseContractEntity.class));
			verifyNoMoreInteractions(mockContractRepository);
		}
	}

	@Test
	void testDeleteContract() {
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);
		contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID);

		verify(mockContractRepository).deleteAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void testDeleteContract_shouldThrow404_whenNoMatch() {
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}
}
