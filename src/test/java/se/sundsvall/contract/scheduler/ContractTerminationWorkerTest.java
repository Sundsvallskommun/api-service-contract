package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractTerminationWorkerTest {

	private static final String CONTRACT_ID = "CONTRACT-1";
	private static final String MUNICIPALITY_ID = "2281";
	private static final LocalDate END_DATE = LocalDate.of(2026, 4, 1);

	@Mock
	private ContractRepository contractRepositoryMock;

	@Mock
	private OutboxRepository outboxRepositoryMock;

	private ContractTerminationWorker worker;

	@BeforeEach
	void setUp() {
		worker = new ContractTerminationWorker(contractRepositoryMock, outboxRepositoryMock, new ObjectMapper());
	}

	@Test
	void terminateSetsStatusAndSavesOutboxEntry() {
		// Arrange
		final var contract = buildContract();
		final var outboxCaptor = ArgumentCaptor.forClass(OutboxEntity.class);

		// Act
		worker.terminate(contract);

		// Assert
		assertThat(contract.getStatus()).isEqualTo(Status.TERMINATED);
		verify(contractRepositoryMock).save(contract);
		verify(outboxRepositoryMock).save(outboxCaptor.capture());

		final var outboxEntity = outboxCaptor.getValue();
		assertThat(outboxEntity.getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(outboxEntity.getEventType()).isEqualTo("TERMINATED");
		assertThat(outboxEntity.getRetries()).isZero();
	}

	@Test
	void terminateSetsCorrectOutboxPayload() throws Exception {
		// Arrange
		final var contract = buildContract();
		final var outboxCaptor = ArgumentCaptor.forClass(OutboxEntity.class);

		// Act
		worker.terminate(contract);

		// Assert
		verify(outboxRepositoryMock).save(outboxCaptor.capture());
		final var event = new ObjectMapper().readValue(outboxCaptor.getValue().getPayload(), ContractTerminatedEvent.class);
		assertThat(event.id()).isEqualTo(CONTRACT_ID);
		assertThat(event.municipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	@Test
	void terminateShouldThrowIllegalStateWhenSerializationFails() throws Exception {
		// Arrange
		final var failingObjectMapper = mock(ObjectMapper.class);
		when(failingObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});
		final var failingWorker = new ContractTerminationWorker(contractRepositoryMock, outboxRepositoryMock, failingObjectMapper);
		final var contract = buildContract();

		// Act & Assert
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> failingWorker.terminate(contract))
			.withMessageContaining("Failed to serialize CONTRACT_TERMINATED event for contract")
			.withMessageContaining(CONTRACT_ID);
	}

	private ContractEntity buildContract() {
		return ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(Status.ACTIVE)
			.withEndDate(END_DATE)
			.build();
	}
}
