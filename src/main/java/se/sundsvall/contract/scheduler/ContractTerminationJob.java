package se.sundsvall.contract.scheduler;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;

@Component
public class ContractTerminationJob {

	private static final Logger LOG = LoggerFactory.getLogger(ContractTerminationJob.class);

	private final ContractRepository contractRepository;
	private final ContractTerminationWorker contractTerminationWorker;

	public ContractTerminationJob(
		final ContractRepository contractRepository,
		final ContractTerminationWorker contractTerminationWorker) {
		this.contractRepository = contractRepository;
		this.contractTerminationWorker = contractTerminationWorker;
	}

	@Dept44Scheduled(
		name = "contract-termination",
		cron = "${scheduler.contract-termination.cron}",
		lockAtMostFor = "${scheduler.contract-termination.lock-at-most-for}")
	public void run() {
		final var expiredContracts = contractRepository.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now());
		LOG.info("Found {} contract(s) to terminate", expiredContracts.size());
		expiredContracts.forEach(contractTerminationWorker::terminate);

		final var expiredPeriodContracts = contractRepository.findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status.ACTIVE, LocalDate.now());
		LOG.info("Found {} non-auto-extending contract(s) with expired period to terminate", expiredPeriodContracts.size());
		expiredPeriodContracts.forEach(contractTerminationWorker::terminate);
	}
}
