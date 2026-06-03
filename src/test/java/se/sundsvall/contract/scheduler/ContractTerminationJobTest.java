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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractTerminationJobTest {

	@Mock
	private ContractRepository contractRepositoryMock;

	@Mock
	private ContractTerminationWorker contractTerminationWorkerMock;

	private ContractTerminationJob job;

	@BeforeEach
	void setUp() {
		job = new ContractTerminationJob(contractRepositoryMock, contractTerminationWorkerMock);
	}

	@Test
	void runWithNoExpiredContracts() {
		// Arrange
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());
		when(contractRepositoryMock.findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());

		// Act
		job.run();

		// Assert
		verify(contractRepositoryMock).findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now());
		verify(contractRepositoryMock).findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now());
		verifyNoInteractions(contractTerminationWorkerMock);
	}

	@Test
	void runTerminatesExpiredContracts() {
		// Arrange
		final var contract = ContractEntity.builder().withContractId("CONTRACT-1").build();
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract));
		when(contractRepositoryMock.findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());

		// Act
		job.run();

		// Assert
		verify(contractTerminationWorkerMock).terminate(contract);
	}

	@Test
	void runTerminatesMultipleExpiredContracts() {
		// Arrange
		final var contract1 = ContractEntity.builder().withContractId("CONTRACT-1").build();
		final var contract2 = ContractEntity.builder().withContractId("CONTRACT-2").build();
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract1, contract2));
		when(contractRepositoryMock.findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());

		// Act
		job.run();

		// Assert
		verify(contractTerminationWorkerMock).terminate(contract1);
		verify(contractTerminationWorkerMock).terminate(contract2);
	}

	@Test
	void runTerminatesNonAutoExtendingContractsWithExpiredPeriod() {
		// Arrange – autoExtend=false contract whose currentPeriodEndDate has passed but endDate has not
		final var contract = ContractEntity.builder().withContractId("CONTRACT-3").build();
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());
		when(contractRepositoryMock.findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract));

		// Act
		job.run();

		// Assert
		verify(contractTerminationWorkerMock).terminate(contract);
	}
}
