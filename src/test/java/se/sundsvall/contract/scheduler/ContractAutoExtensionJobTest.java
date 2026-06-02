package se.sundsvall.contract.scheduler;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.Status;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractAutoExtensionJobTest {

	@Mock
	private ContractRepository contractRepositoryMock;

	@Mock
	private ContractAutoExtensionWorker contractAutoExtensionWorkerMock;

	private ContractAutoExtensionJob job;

	@BeforeEach
	void setUp() {
		job = new ContractAutoExtensionJob(contractRepositoryMock, contractAutoExtensionWorkerMock);
	}

	@Test
	void runWithNoContractsToExtend() {
		// Arrange
		when(contractRepositoryMock.findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());

		// Act
		job.run();

		// Assert
		verify(contractRepositoryMock).findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now());
		verifyNoInteractions(contractAutoExtensionWorkerMock);
	}

	@Test
	void runExtendsEligibleContract() {
		// Arrange
		final var contract = ContractEntity.builder().withContractId("CONTRACT-1").build();
		when(contractRepositoryMock.findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract));

		// Act
		job.run();

		// Assert
		verify(contractAutoExtensionWorkerMock).extend(contract);
	}

	@Test
	void runExtendsMultipleEligibleContracts() {
		// Arrange
		final var contract1 = ContractEntity.builder().withContractId("CONTRACT-1").build();
		final var contract2 = ContractEntity.builder().withContractId("CONTRACT-2").build();
		when(contractRepositoryMock.findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract1, contract2));

		// Act
		job.run();

		// Assert
		verify(contractAutoExtensionWorkerMock).extend(contract1);
		verify(contractAutoExtensionWorkerMock).extend(contract2);
	}

	@Test
	void runContinuesWithRemainingContractsWhenOneThrows() {
		// Arrange
		final var contract1 = ContractEntity.builder().withContractId("CONTRACT-1").build();
		final var contract2 = ContractEntity.builder().withContractId("CONTRACT-2").build();
		when(contractRepositoryMock.findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract1, contract2));
		doThrow(new RuntimeException("DB error")).when(contractAutoExtensionWorkerMock).extend(contract1);

		// Act
		job.run();

		// Assert – contract2 is still processed despite contract1 failing
		verify(contractAutoExtensionWorkerMock).extend(contract1);
		verify(contractAutoExtensionWorkerMock).extend(contract2);
	}
}
