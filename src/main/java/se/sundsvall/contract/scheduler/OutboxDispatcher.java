package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.billingdatacollector.BillingEventPublisher;
import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;

@Component
public class OutboxDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(OutboxDispatcher.class);
	private static final int MAX_LAST_ERROR_LENGTH = 512;
	private static final String JOB_NAME = "outbox-dispatcher";

	private final OutboxRepository outboxRepository;
	private final BillingEventPublisher publisher;
	private final ObjectMapper objectMapper;
	private final Dept44HealthUtility dept44HealthUtility;

	public OutboxDispatcher(
		final OutboxRepository outboxRepository,
		final BillingEventPublisher publisher,
		final ObjectMapper objectMapper,
		final Dept44HealthUtility dept44HealthUtility) {
		this.outboxRepository = outboxRepository;
		this.publisher = publisher;
		this.objectMapper = objectMapper;
		this.dept44HealthUtility = dept44HealthUtility;
	}

	@Dept44Scheduled(
		name = JOB_NAME,
		cron = "${scheduler.outbox.dispatch.cron:0 */5 * * * *}",
		lockAtMostFor = "${scheduler.outbox.dispatch.lock-at-most-for:PT30S}")
	public void dispatch() {
		final var exhausted = outboxRepository.findExhausted();
		if (!exhausted.isEmpty()) {
			dept44HealthUtility.setHealthIndicatorUnhealthy(JOB_NAME,
				"Found %d exhausted outbox record(s) that require manual intervention".formatted(exhausted.size()));
		}
		outboxRepository.findUnsent().forEach(this::process);
	}

	private void process(final OutboxEntity entity) {
		try {
			publisher.publish(deserialize(entity));
			outboxRepository.delete(entity);
			LOG.info("Successfully dispatched outbox event {} for contract {}", entity.getEventType(), entity.getContractId());
		} catch (final Exception e) {
			final var newRetries = entity.getRetries() + 1;
			entity.setRetries(newRetries);
			entity.setLastError(truncate(e.getMessage()));
			outboxRepository.save(entity);
			if (newRetries >= OutboxRepository.MAX_RETRIES) {
				LOG.error("Outbox event {} for contract {} has exhausted all {} retries and will no longer be retried. Manual intervention required. Last error: {}",
					entity.getEventType(), entity.getContractId(), OutboxRepository.MAX_RETRIES, e.getMessage());
			} else {
				LOG.warn("Failed to dispatch outbox event {} for contract {}, retries: {}", entity.getEventType(), entity.getContractId(), newRetries, e);
			}
		}
	}

	private static String truncate(final String message) {
		return message == null ? null : message.substring(0, Math.min(message.length(), MAX_LAST_ERROR_LENGTH));
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
