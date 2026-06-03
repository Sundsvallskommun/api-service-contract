package se.sundsvall.contract.scheduler;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.dept44.scheduling.health.Dept44HealthUtility;

@Component
public class ContractAutoExtensionJob {

	private static final Logger LOG = LoggerFactory.getLogger(ContractAutoExtensionJob.class);
	private static final String JOB_NAME = "contract-auto-extension";

	private final ContractRepository contractRepository;
	private final ContractAutoExtensionWorker contractAutoExtensionWorker;
	private final Dept44HealthUtility dept44HealthUtility;

	public ContractAutoExtensionJob(
		final ContractRepository contractRepository,
		final ContractAutoExtensionWorker contractAutoExtensionWorker,
		final Dept44HealthUtility dept44HealthUtility) {
		this.contractRepository = contractRepository;
		this.contractAutoExtensionWorker = contractAutoExtensionWorker;
		this.dept44HealthUtility = dept44HealthUtility;
	}

	@Dept44Scheduled(
		name = JOB_NAME,
		cron = "${scheduler.contract-auto-extension.cron}",
		lockAtMostFor = "${scheduler.contract-auto-extension.lock-at-most-for}")
	public void run() {
		final var contracts = contractRepository.findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now());
		LOG.info("Found {} contract(s) to auto-extend", contracts.size());

		final var failures = new AtomicInteger();
		contracts.forEach(contract -> {
			try {
				contractAutoExtensionWorker.extend(contract);
			} catch (final Exception e) {
				LOG.error("Failed to auto-extend contract {}: {}", contract.getContractId(), e.getMessage(), e);
				failures.incrementAndGet();
			}
		});

		if (failures.get() == 0) {
			dept44HealthUtility.setHealthIndicatorHealthy(JOB_NAME);
		} else {
			dept44HealthUtility.setHealthIndicatorUnhealthy(JOB_NAME,
				"Failed to auto-extend %d contract(s)".formatted(failures.get()));
		}
	}
}
