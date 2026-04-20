package se.sundsvall.contract.scheduler;

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
import se.sundsvall.contract.integration.billingdatacollector.HttpBillingEventPublisher;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxDispatcherTest {

	private static final String CONTRACT_ID = "CONTRACT-1";
	private static final String MUNICIPALITY_ID = "2281";
	private static final LocalDate START_DATE = LocalDate.of(2020, 1, 1);
	private static final LocalDate END_DATE = LocalDate.of(2026, 6, 30);

	@Mock
	private OutboxRepository outboxRepositoryMock;

	@Mock
	private HttpBillingEventPublisher publisherMock;

	private ObjectMapper objectMapper;
	private OutboxDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		dispatcher = new OutboxDispatcher(outboxRepositoryMock, publisherMock, objectMapper);
	}

	@Test
	void dispatchWithEmptyOutbox() {
		// Arrange
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of());

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxRepositoryMock).findUnsent();
		verifyNoInteractions(publisherMock);
	}

	@Test
	void dispatchContractCreatedEventSuccessfully() throws Exception {
		// Arrange
		final var event = new ContractCreatedEvent(CONTRACT_ID, MUNICIPALITY_ID, ContractType.LEASE_AGREEMENT, Status.ACTIVE,
			START_DATE, END_DATE, null, null, InvoicedIn.ADVANCE, IntervalType.QUARTERLY, LeaseType.LAND_LEASE_RESIDENTIAL);
		final var entity = buildOutboxEntity("CONTRACT_CREATED", objectMapper.writeValueAsString(event));
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void dispatchContractUpdatedEventSuccessfully() throws Exception {
		// Arrange
		final var event = new ContractUpdatedEvent(CONTRACT_ID, MUNICIPALITY_ID, ContractType.LEASE_AGREEMENT, Status.ACTIVE,
			START_DATE, END_DATE, null, null, InvoicedIn.ARREARS, IntervalType.YEARLY, LeaseType.LAND_LEASE_RESIDENTIAL);
		final var entity = buildOutboxEntity("CONTRACT_UPDATED", objectMapper.writeValueAsString(event));
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void dispatchContractDeletedEventSuccessfully() throws Exception {
		// Arrange
		final var event = new ContractDeletedEvent(CONTRACT_ID, MUNICIPALITY_ID);
		final var entity = buildOutboxEntity("CONTRACT_DELETED", objectMapper.writeValueAsString(event));
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void dispatchContractTerminatedEventSuccessfully() throws Exception {
		// Arrange
		final var event = new ContractTerminatedEvent(CONTRACT_ID, MUNICIPALITY_ID, END_DATE, InvoicedIn.ADVANCE, IntervalType.QUARTERLY);
		final var entity = buildOutboxEntity("CONTRACT_TERMINATED", objectMapper.writeValueAsString(event));
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(publisherMock).publish(event);
		verify(outboxRepositoryMock).delete(entity);
		verify(outboxRepositoryMock, never()).save(any());
	}

	@Test
	void dispatchFailureIncrementsRetriesAndSavesError() throws Exception {
		// Arrange
		final var event = new ContractTerminatedEvent(CONTRACT_ID, MUNICIPALITY_ID, END_DATE, InvoicedIn.ADVANCE, IntervalType.QUARTERLY);
		final var entity = buildOutboxEntity("CONTRACT_TERMINATED", objectMapper.writeValueAsString(event));
		final var exception = new RuntimeException("billing-data-collector unavailable");
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));
		doThrow(exception).when(publisherMock).publish(any());

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxRepositoryMock, never()).delete(any());
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRetries()).isEqualTo(1);
		assertThat(captor.getValue().getLastError()).isEqualTo("billing-data-collector unavailable");
	}

	@Test
	void dispatchWithUnknownEventTypeSavesError() {
		// Arrange
		final var entity = buildOutboxEntity("UNKNOWN_EVENT_TYPE", "{}");
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxRepositoryMock, never()).delete(any());
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRetries()).isEqualTo(1);
		assertThat(captor.getValue().getLastError()).isNotBlank();
	}

	@Test
	void dispatchStopsRetryingAfterMaxRetries() throws Exception {
		// Arrange — entity already at 4 retries (next failure will be 5, findUnsent won't return it again)
		final var event = new ContractTerminatedEvent(CONTRACT_ID, MUNICIPALITY_ID, END_DATE, InvoicedIn.ADVANCE, IntervalType.QUARTERLY);
		final var entity = OutboxEntity.builder()
			.withContractId(CONTRACT_ID)
			.withEventType("CONTRACT_TERMINATED")
			.withPayload(objectMapper.writeValueAsString(event))
			.withRetries(4)
			.build();
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity));
		doThrow(new RuntimeException("still failing")).when(publisherMock).publish(any());

		// Act
		dispatcher.dispatch();

		// Assert — saved with retries = 5, findUnsent will no longer pick it up
		final var captor = ArgumentCaptor.forClass(OutboxEntity.class);
		verify(outboxRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRetries()).isEqualTo(5);
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
