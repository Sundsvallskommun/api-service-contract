package se.sundsvall.contract.scheduler;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

	@InjectMocks
	private OutboxDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of());
		when(outboxRepositoryMock.findUnhealthy()).thenReturn(List.of());
	}

	@Test
	void dispatchWithEmptyOutbox() {
		// Arrange
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of());

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxRepositoryMock).findUnsent();
		verify(outboxRepositoryMock).findUnhealthy();
		verifyNoInteractions(outboxWorkerMock);
		verifyNoInteractions(dept44HealthUtilityMock);
	}

	@Test
	void dispatchDelegatesToWorkerForEachEntry() {
		// Arrange
		final var entity1 = buildOutboxEntity("CREATED");
		final var entity2 = buildOutboxEntity("UPDATED");
		when(outboxRepositoryMock.findUnsent()).thenReturn(List.of(entity1, entity2));

		// Act
		dispatcher.dispatch();

		// Assert
		verify(outboxWorkerMock).process(entity1);
		verify(outboxWorkerMock).process(entity2);
	}

	@Test
	void dispatchSetsHealthUnhealthyWhenUnhealthyRecordsExist() {
		// Arrange
		final var unhealthyEntity = buildOutboxEntity("TERMINATED");
		when(outboxRepositoryMock.findUnhealthy()).thenReturn(List.of(unhealthyEntity));
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
