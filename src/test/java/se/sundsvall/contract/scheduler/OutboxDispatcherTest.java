package se.sundsvall.contract.scheduler;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxDispatcherTest {

	private static final String CONTRACT_ID = "CONTRACT-1";

	@Mock
	private OutboxRepository outboxRepositoryMock;

	@Mock
	private OutboxWorker outboxWorkerMock;

	@Mock
	private Dept44HealthUtility dept44HealthUtilityMock;

	private OutboxDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		dispatcher = new OutboxDispatcher(outboxRepositoryMock, outboxWorkerMock, dept44HealthUtilityMock);
		when(outboxRepositoryMock.findExhausted()).thenReturn(List.of());
	}

	@Test
	void dispatchWithEmptyOutbox() {
		// Arrange
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of());

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxRepositoryMock).findExhausted();
		verify(outboxRepositoryMock).findUnsent();
		verifyNoInteractions(outboxWorkerMock);
		verifyNoInteractions(dept44HealthUtilityMock);
	}

	@Test
	void dispatchDelegatesToWorkerForEachEntry() {
		// Arrange
		final var entity1 = buildOutboxEntity("CONTRACT_CREATED");
		final var entity2 = buildOutboxEntity("CONTRACT_UPDATED");
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity1, entity2));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxWorkerMock).process(entity1);
		verify(outboxWorkerMock).process(entity2);
	}

	@Test
	void dispatchSetsHealthUnhealthyWhenExhaustedRecordsExist() {
		// Arrange
		final var exhaustedEntity = buildOutboxEntity("CONTRACT_TERMINATED");
		when(outboxRepositoryMock.findExhausted()).thenReturn(List.of(exhaustedEntity));
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of());

		// Act
		dispatcher.dispatch();

		// Assert
		verify(dept44HealthUtilityMock).setHealthIndicatorUnhealthy(eq("outbox-dispatcher"), contains("1"));
		verifyNoInteractions(outboxWorkerMock);
	}

	private OutboxEntity buildOutboxEntity(final String eventType) {
		return OutboxEntity.builder()
			.withContractId(CONTRACT_ID)
			.withEventType(eventType)
			.withPayload("{}")
			.build();
	}
}
