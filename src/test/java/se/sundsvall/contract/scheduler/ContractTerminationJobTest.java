package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractTerminationJobTest {

	private static final String CONTRACT_ID = "CONTRACT-1";
	private static final String MUNICIPALITY_ID = "2281";
	private static final LocalDate END_DATE = LocalDate.of(2026, 4, 1);

	@Mock
	private ContractRepository contractRepositoryMock;

	@Mock
	private OutboxRepository outboxRepositoryMock;

	private ContractTerminationJob job;

	@BeforeEach
	void setUp() {
		final var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		job = new ContractTerminationJob(contractRepositoryMock, outboxRepositoryMock, objectMapper);
	}

	@Test
	void runWithNoExpiredContracts() {
		// Arrange
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of());

		// Act
		job.run();

		// Assert
		verify(contractRepositoryMock).findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now());
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void runTerminatesExpiredContracts() {
		// Arrange
		final var contract = buildContract();
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract));

		// Act
		job.run();

		// Assert
		verify(contractRepositoryMock).findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now());
		verify(contractRepositoryMock).save(contract);
		verify(outboxRepositoryMock).save(any(OutboxEntity.class));
		assertThat(contract.getStatus()).isEqualTo(Status.TERMINATED);
	}

	@Test
	void terminateSetsCorrectOutboxPayload() throws Exception {
		// Arrange
		final var contract = buildContract();
		final var outboxCaptor = ArgumentCaptor.forClass(OutboxEntity.class);
		final var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

		// Act
		job.terminate(contract);

		// Assert
		verify(contractRepositoryMock).save(contract);
		verify(outboxRepositoryMock).save(outboxCaptor.capture());

		final var outboxEntity = outboxCaptor.getValue();
		assertThat(outboxEntity.getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(outboxEntity.getEventType()).isEqualTo("CONTRACT_TERMINATED");
		assertThat(outboxEntity.getRetries()).isZero();

		final var event = objectMapper.readValue(outboxEntity.getPayload(), ContractTerminatedEvent.class);
		assertThat(event.contractId()).isEqualTo(CONTRACT_ID);
		assertThat(event.municipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(event.endDate()).isEqualTo(END_DATE);
		assertThat(event.invoicedIn()).isEqualTo(InvoicedIn.ADVANCE);
		assertThat(event.invoiceInterval()).isEqualTo(IntervalType.QUARTERLY);
	}

	@Test
	void terminateWithNullInvoicing() {
		// Arrange
		final var contract = ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(Status.ACTIVE)
			.withEndDate(END_DATE)
			.build();
		final var outboxCaptor = ArgumentCaptor.forClass(OutboxEntity.class);

		// Act
		job.terminate(contract);

		// Assert
		verify(outboxRepositoryMock).save(outboxCaptor.capture());
		assertThat(outboxCaptor.getValue().getEventType()).isEqualTo("CONTRACT_TERMINATED");
	}

	@Test
	void runTerminatesMultipleExpiredContracts() {
		// Arrange
		final var contract1 = buildContract();
		final var contract2 = buildContract();
		when(contractRepositoryMock.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now())).thenReturn(List.of(contract1, contract2));

		// Act
		job.run();

		// Assert
		verify(contractRepositoryMock, times(2)).save(any(ContractEntity.class));
		verify(outboxRepositoryMock, times(2)).save(any(OutboxEntity.class));
	}

	@Test
	void terminateShouldThrowIllegalStateWhenSerializationFails() throws Exception {
		// Arrange
		final var failingObjectMapper = mock(ObjectMapper.class);
		when(failingObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});
		final var failingJob = new ContractTerminationJob(contractRepositoryMock, outboxRepositoryMock, failingObjectMapper);
		final var contract = buildContract();

		// Act & Assert
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> failingJob.terminate(contract))
			.withMessageContaining("Failed to serialize CONTRACT_TERMINATED event for contract")
			.withMessageContaining(CONTRACT_ID);
	}

	private ContractEntity buildContract() {
		return ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(Status.ACTIVE)
			.withEndDate(END_DATE)
			.withInvoicing(InvoicingEmbeddable.builder()
				.withInvoicedIn(InvoicedIn.ADVANCE)
				.withInvoiceInterval(IntervalType.QUARTERLY)
				.build())
			.build();
	}
}
