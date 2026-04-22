package se.sundsvall.contract.scheduler;

import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;

@Component
public class OutboxDispatcher {

	private static final String JOB_NAME = "outbox-dispatcher";

	private final OutboxRepository outboxRepository;
	private final OutboxWorker outboxWorker;
	private final Dept44HealthUtility dept44HealthUtility;

	public OutboxDispatcher(
		final OutboxRepository outboxRepository,
		final OutboxWorker outboxWorker,
		final Dept44HealthUtility dept44HealthUtility) {
		this.outboxRepository = outboxRepository;
		this.outboxWorker = outboxWorker;
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
		outboxRepository.findUnsent().forEach(outboxWorker::process);
	}
}
