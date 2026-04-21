package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;

@Component
public class ContractTerminationJob {

	private static final Logger LOG = LoggerFactory.getLogger(ContractTerminationJob.class);

	private final ContractRepository contractRepository;
	private final OutboxRepository outboxRepository;
	private final ObjectMapper objectMapper;

	public ContractTerminationJob(
		final ContractRepository contractRepository,
		final OutboxRepository outboxRepository,
		final ObjectMapper objectMapper) {
		this.contractRepository = contractRepository;
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Dept44Scheduled(
		name = "contract-termination",
		cron = "${scheduler.contract-termination.cron:0 0 1 * * *}",
		lockAtMostFor = "${scheduler.contract-termination.lock-at-most-for:PT1H}")
	public void run() {
		final var expiredContracts = contractRepository.findByStatusAndEndDateBefore(Status.ACTIVE, LocalDate.now());
		LOG.info("Found {} contract(s) to terminate", expiredContracts.size());
		expiredContracts.forEach(this::terminate);
	}

	@Transactional
	public void terminate(final ContractEntity contract) {
		contract.setStatus(Status.TERMINATED);
		contractRepository.save(contract);
		outboxRepository.save(toOutboxEntity(contract));
		LOG.info("Terminated contract {}", contract.getContractId());
	}

	private OutboxEntity toOutboxEntity(final ContractEntity contract) {
		final var event = new ContractTerminatedEvent(
			contract.getContractId(),
			contract.getMunicipalityId(),
			contract.getEndDate(),
			contract.getInvoicing() != null ? contract.getInvoicing().getInvoicedIn() : null,
			contract.getInvoicing() != null ? contract.getInvoicing().getInvoiceInterval() : null);

		try {
			return OutboxEntity.builder()
				.withContractId(contract.getContractId())
				.withEventType(event.eventType())
				.withPayload(objectMapper.writeValueAsString(event))
				.build();
		} catch (final JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize CONTRACT_TERMINATED event for contract %s".formatted(contract.getContractId()), e);
		}
	}
}
