package se.sundsvall.contract.scheduler;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;

@Component
public class ContractAutoExtensionJob {

	private static final Logger LOG = LoggerFactory.getLogger(ContractAutoExtensionJob.class);

	private final ContractRepository contractRepository;
	private final ContractAutoExtensionWorker contractAutoExtensionWorker;

	public ContractAutoExtensionJob(
		final ContractRepository contractRepository,
		final ContractAutoExtensionWorker contractAutoExtensionWorker) {
		this.contractRepository = contractRepository;
		this.contractAutoExtensionWorker = contractAutoExtensionWorker;
	}

	@Dept44Scheduled(
		name = "contract-auto-extension",
		cron = "${scheduler.contract-auto-extension.cron}",
		lockAtMostFor = "${scheduler.contract-auto-extension.lock-at-most-for}")
	public void run() {
		final var contracts = contractRepository.findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now());
		LOG.info("Found {} contract(s) to auto-extend", contracts.size());
		contracts.forEach(contract -> {
			try {
				contractAutoExtensionWorker.extend(contract);
			} catch (final Exception e) {
				LOG.error("Failed to auto-extend contract {}: {}", contract.getContractId(), e.getMessage(), e);
			}
		});
	}
}
