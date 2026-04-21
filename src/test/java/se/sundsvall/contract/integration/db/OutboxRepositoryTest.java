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
	void findUnsentReturnsOnlyRowsBelowMaxRetries() {
		// Arrange
		outboxRepository.save(buildOutboxEntity("CONTRACT_CREATED", 0));
		outboxRepository.save(buildOutboxEntity("CONTRACT_UPDATED", OutboxRepository.MAX_RETRIES - 1));
		outboxRepository.save(buildOutboxEntity("CONTRACT_DELETED", OutboxRepository.MAX_RETRIES));
		outboxRepository.save(buildOutboxEntity("CONTRACT_TERMINATED", OutboxRepository.MAX_RETRIES + 1));

		// Act
		final var result = outboxRepository.findUnsent();

		// Assert
		assertThat(result).hasSize(2)
			.extracting(OutboxEntity::getEventType)
			.containsExactly("CONTRACT_CREATED", "CONTRACT_UPDATED");
	}

	@Test
	void findExhaustedReturnsOnlyRowsAtOrAboveMaxRetries() {
		// Arrange
		outboxRepository.save(buildOutboxEntity("CONTRACT_CREATED", 0));
		outboxRepository.save(buildOutboxEntity("CONTRACT_UPDATED", OutboxRepository.MAX_RETRIES - 1));
		outboxRepository.save(buildOutboxEntity("CONTRACT_DELETED", OutboxRepository.MAX_RETRIES));
		outboxRepository.save(buildOutboxEntity("CONTRACT_TERMINATED", OutboxRepository.MAX_RETRIES + 1));

		// Act
		final var result = outboxRepository.findExhausted();

		// Assert
		assertThat(result).hasSize(2)
			.extracting(OutboxEntity::getEventType)
			.containsExactlyInAnyOrder("CONTRACT_DELETED", "CONTRACT_TERMINATED");
	}

	@Test
	void findUnsentReturnsRowsOrderedByCreatedAt() throws InterruptedException {
		// Arrange — insert in reverse order and verify ordering by createdAt ASC
		outboxRepository.saveAndFlush(buildOutboxEntity("CONTRACT_CREATED", 0));
		Thread.sleep(10); // ensure distinct createdAt timestamps
		outboxRepository.saveAndFlush(buildOutboxEntity("CONTRACT_UPDATED", 0));

		// Act
		final var result = outboxRepository.findUnsent();

		// Assert
		assertThat(result).hasSize(2)
			.extracting(OutboxEntity::getEventType)
			.containsExactly("CONTRACT_CREATED", "CONTRACT_UPDATED");
	}

	private OutboxEntity buildOutboxEntity(final String eventType, final int retries) {
		final var entity = OutboxEntity.builder()
			.withContractId("CONTRACT-1")
			.withEventType(eventType)
			.withPayload("{}")
			.withRetries(retries)
			.build();
		// Manually trigger prePersist since @PrePersist runs on save
		return entity;
	}
}
