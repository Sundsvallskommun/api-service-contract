package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.billingdatacollector.HttpBillingEventPublisher;
import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.OutboxEntity;

@Component
public class OutboxDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(OutboxDispatcher.class);

	private final OutboxRepository outboxRepository;
	private final HttpBillingEventPublisher publisher;
	private final ObjectMapper objectMapper;

	public OutboxDispatcher(
		final OutboxRepository outboxRepository,
		final HttpBillingEventPublisher publisher,
		final ObjectMapper objectMapper) {
		this.outboxRepository = outboxRepository;
		this.publisher = publisher;
		this.objectMapper = objectMapper;
	}

	@Scheduled(fixedDelayString = "${scheduler.outbox.dispatch.delay-ms:30000}")
	public void dispatch() {
		outboxRepository.findUnsent().forEach(this::process);
	}

	private void process(final OutboxEntity entity) {
		try {
			publisher.publish(deserialize(entity));
			outboxRepository.delete(entity);
			LOG.info("Successfully dispatched outbox event {} for contract {}", entity.getEventType(), entity.getContractId());
		} catch (final Exception e) {
			LOG.warn("Failed to dispatch outbox event {} for contract {}, retries: {}", entity.getEventType(), entity.getContractId(), entity.getRetries() + 1, e);
			entity.setRetries(entity.getRetries() + 1);
			entity.setLastError(e.getMessage());
			outboxRepository.save(entity);
		}
	}

	private BillingEvent deserialize(final OutboxEntity entity) {
		try {
			return switch (entity.getEventType()) {
				case "CONTRACT_CREATED" -> objectMapper.readValue(entity.getPayload(), ContractCreatedEvent.class);
				case "CONTRACT_UPDATED" -> objectMapper.readValue(entity.getPayload(), ContractUpdatedEvent.class);
				case "CONTRACT_DELETED" -> objectMapper.readValue(entity.getPayload(), ContractDeletedEvent.class);
				case "CONTRACT_TERMINATED" -> objectMapper.readValue(entity.getPayload(), ContractTerminatedEvent.class);
				default -> throw new IllegalArgumentException("Unknown event type: " + entity.getEventType());
			};
		} catch (final Exception e) {
			throw new IllegalStateException("Failed to deserialize outbox event %s".formatted(entity.getEventType()), e);
		}
	}
}
