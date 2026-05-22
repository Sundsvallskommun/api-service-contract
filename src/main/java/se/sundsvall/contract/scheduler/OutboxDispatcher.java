package se.sundsvall.contract.scheduler;

import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;

import static se.sundsvall.contract.integration.db.OutboxRepository.UNHEALTHY_THRESHOLD;

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

	// lockAtMostFor default raised to PT10M: cron fires every 5 min, and worst-case
	// dispatch (large unsent batch + slow downstream) can exceed 5 min. A 30 s lock
	// could expire mid-run, letting a second instance pick up the same rows and emit
	// duplicate billing events.
	@Dept44Scheduled(
		name = JOB_NAME,
		cron = "${scheduler.outbox.dispatch.cron:0 */5 * * * *}",
		lockAtMostFor = "${scheduler.outbox.dispatch.lock-at-most-for:PT10M}")
	public void dispatch() {
		final var unhealthy = outboxRepository.findUnhealthy();
		if (unhealthy.isEmpty()) {
			// Reset on every healthy tick so alerts auto-clear once the backlog drains;
			// without this the indicator would latch unhealthy until process restart.
			dept44HealthUtility.setHealthIndicatorHealthy(JOB_NAME);
		} else {
			dept44HealthUtility.setHealthIndicatorUnhealthy(JOB_NAME,
				"Found %d outbox record(s) with %d or more failed attempts".formatted(unhealthy.size(), UNHEALTHY_THRESHOLD));
		}
		outboxRepository.findUnsent().forEach(outboxWorker::process);
	}
}
