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
		final var unhealthy = outboxRepository.findUnhealthy();
		if (!unhealthy.isEmpty()) {
			dept44HealthUtility.setHealthIndicatorUnhealthy(JOB_NAME,
				"Found %d outbox record(s) with %d or more failed attempts".formatted(unhealthy.size(), OutboxRepository.UNHEALTHY_THRESHOLD));
		}
		outboxRepository.findUnsent().forEach(outboxWorker::process);
	}
}
