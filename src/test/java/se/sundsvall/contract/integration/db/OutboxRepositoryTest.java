package se.sundsvall.contract.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.contract.integration.db.model.OutboxEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql"
})
class OutboxRepositoryTest {

	@Autowired
	private OutboxRepository outboxRepository;

	@Test
	void findUnsentReturnsAllRows() {
		// Arrange
		outboxRepository.save(buildOutboxEntity("CREATED", 0));
		outboxRepository.save(buildOutboxEntity("UPDATED", OutboxRepository.UNHEALTHY_THRESHOLD - 1));
		outboxRepository.save(buildOutboxEntity("DELETED", OutboxRepository.UNHEALTHY_THRESHOLD));
		outboxRepository.save(buildOutboxEntity("TERMINATED", OutboxRepository.UNHEALTHY_THRESHOLD + 1));

		// Act
		final var result = outboxRepository.findUnsent();

		// Assert
		assertThat(result).hasSize(4)
			.extracting(OutboxEntity::getEventType)
			.containsExactly("CREATED", "UPDATED", "DELETED", "TERMINATED");
	}

	@Test
	void findUnhealthyReturnsOnlyRowsAtOrAboveUnhealthyThreshold() {
		// Arrange
		outboxRepository.save(buildOutboxEntity("CREATED", 0));
		outboxRepository.save(buildOutboxEntity("UPDATED", OutboxRepository.UNHEALTHY_THRESHOLD - 1));
		outboxRepository.save(buildOutboxEntity("DELETED", OutboxRepository.UNHEALTHY_THRESHOLD));
		outboxRepository.save(buildOutboxEntity("TERMINATED", OutboxRepository.UNHEALTHY_THRESHOLD + 1));

		// Act
		final var result = outboxRepository.findUnhealthy();

		// Assert
		assertThat(result).hasSize(2)
			.extracting(OutboxEntity::getEventType)
			.containsExactlyInAnyOrder("DELETED", "TERMINATED");
	}

	private OutboxEntity buildOutboxEntity(final String eventType, final int retries) {
		return OutboxEntity.builder()
			.withContractId("CONTRACT-1")
			.withEventType(eventType)
			.withPayload("{}")
			.withRetries(retries)
			.build();
	}
}
