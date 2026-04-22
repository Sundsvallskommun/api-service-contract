package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.Status;

@Component
public class ContractTerminationWorker {

	private static final Logger LOG = LoggerFactory.getLogger(ContractTerminationWorker.class);

	private final ContractRepository contractRepository;
	private final OutboxRepository outboxRepository;
	private final ObjectMapper objectMapper;

	public ContractTerminationWorker(
		final ContractRepository contractRepository,
		final OutboxRepository outboxRepository,
		final ObjectMapper objectMapper) {
		this.contractRepository = contractRepository;
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void terminate(final ContractEntity contract) {
		contract.setStatus(Status.TERMINATED);
		contractRepository.save(contract);
		outboxRepository.save(toOutboxEntity(contract));
		LOG.info("Terminated contract {}", contract.getContractId());
	}

	private OutboxEntity toOutboxEntity(final ContractEntity contract) {
		final var event = ContractTerminatedEvent.of(
			contract.getContractId(),
			contract.getMunicipalityId());

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
