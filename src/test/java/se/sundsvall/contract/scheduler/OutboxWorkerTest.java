package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.billingdatacollector.BillingEventPublisher;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.OutboxEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxWorkerTest {

	private static final String CONTRACT_ID = "CONTRACT-1";
	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private OutboxRepository outboxRepositoryMock;

	@Mock
	private BillingEventPublisher publisherMock;

	private ObjectMapper objectMapper;
	private OutboxWorker worker;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		worker = new OutboxWorker(outboxRepositoryMock, publisherMock, objectMapper);
	}

	@Test
	void processContractCreatedEventSuccessfully() throws Exception {
		// Arrange
		final var event = ContractCreatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("CREATED", objectMapper.writeValueAsString(event));

		// Act
		worker.process(entity);

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void processContractUpdatedEventSuccessfully() throws Exception {
		// Arrange
		final var event = ContractUpdatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("UPDATED", objectMapper.writeValueAsString(event));

		// Act
		worker.process(entity);

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void processContractDeletedEventSuccessfully() throws Exception {
		// Arrange
		final var event = ContractDeletedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("DELETED", objectMapper.writeValueAsString(event));

		// Act
		worker.process(entity);

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void processContractTerminatedEventSuccessfully() throws Exception {
		// Arrange
		final var event = ContractTerminatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("TERMINATED", objectMapper.writeValueAsString(event));

		// Act
		worker.process(entity);

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void processFailureIncrementsRetriesAndSavesError() throws Exception {
		// Arrange
		final var event = ContractTerminatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("TERMINATED", objectMapper.writeValueAsString(event));
		doThrow(new RuntimeException("billing-data-collector unavailable")).when(publisherMock).publish(any());

		// Act
		worker.process(entity);

		// Assert
		verify(outboxRepositoryMock, never()).delete(any());
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRetries()).isEqualTo(1);
		assertThat(captor.getValue().getLastError()).isEqualTo("billing-data-collector unavailable");
	}

	@Test
	void processTruncatesLongErrorMessages() throws Exception {
		// Arrange
		final var event = ContractTerminatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("TERMINATED", objectMapper.writeValueAsString(event));
		doThrow(new RuntimeException("x".repeat(1000))).when(publisherMock).publish(any());

		// Act
		worker.process(entity);

		// Assert
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getLastError()).hasSize(512);
	}

	@Test
	void processWithUnknownEventTypeSavesError() {
		// Arrange
		final var entity = buildOutboxEntity("UNKNOWN_EVENT_TYPE", "{}");

		// Act
		worker.process(entity);

		// Assert
		verify(outboxRepositoryMock, never()).delete(any());
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRetries()).isEqualTo(1);
		assertThat(captor.getValue().getLastError()).isNotBlank();
	}

	@Test
	void processStopsRetryingAfterMaxRetries() throws Exception {
		// Arrange
		final var event = ContractTerminatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = OutboxEntity.builder()
			.withContractId(CONTRACT_ID)
			.withEventType("TERMINATED")
			.withPayload(objectMapper.writeValueAsString(event))
			.withRetries(OutboxRepository.MAX_RETRIES - 1)
			.build();
		doThrow(new RuntimeException("still failing")).when(publisherMock).publish(any());

		// Act
		worker.process(entity);

		// Assert
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRetries()).isEqualTo(OutboxRepository.MAX_RETRIES);
	}

	private OutboxEntity buildOutboxEntity(final String eventType, final String payload) {
		return OutboxEntity.builder()
			.withContractId(CONTRACT_ID)
			.withEventType(eventType)
			.withPayload(payload)
			.withRetries(0)
			.build();
	}
}
