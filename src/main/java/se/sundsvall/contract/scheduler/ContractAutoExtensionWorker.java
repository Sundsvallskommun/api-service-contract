package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import se.sundsvall.contract.model.enums.TimeUnit;

@Component
public class ContractAutoExtensionWorker {

	private static final Logger LOG = LoggerFactory.getLogger(ContractAutoExtensionWorker.class);

	private final ContractRepository contractRepository;
	private final OutboxRepository outboxRepository;
	private final ObjectMapper objectMapper;

	public ContractAutoExtensionWorker(
		final ContractRepository contractRepository,
		final OutboxRepository outboxRepository,
		final ObjectMapper objectMapper) {
		this.contractRepository = contractRepository;
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void extend(final ContractEntity contract) {
		if (contract.getLeaseExtension() == null || contract.getLeaseExtensionUnit() == null) {
			LOG.warn("Skipping auto-extension of contract {}: leaseExtension or leaseExtensionUnit is missing", contract.getContractId());
			return;
		}

		final var oldStart = contract.getCurrentPeriodStartDate();
		final var oldEnd = contract.getCurrentPeriodEndDate();
		final var newStart = oldEnd.plusDays(1);
		var newEnd = addExtension(oldEnd, contract.getLeaseExtension(), contract.getLeaseExtensionUnit());

		if (contract.getEndDate() != null && contract.getEndDate().isBefore(newEnd)) {
			newEnd = contract.getEndDate();
		}

		if (!newEnd.isAfter(LocalDate.now())) {
			contract.setStatus(Status.TERMINATED);
			contractRepository.save(contract);
			outboxRepository.save(toOutboxEntity(contract));
			LOG.info("Terminated auto-extending contract {}: endDate {} has passed, extension could not reach a future date", contract.getContractId(), contract.getEndDate());
			return;
		}

		contract.setCurrentPeriodStartDate(newStart);
		contract.setCurrentPeriodEndDate(newEnd);
		contractRepository.save(contract);
		LOG.info("Auto-extended contract {}: {} - {} → {} - {}", contract.getContractId(), oldStart, oldEnd, newStart, newEnd);
	}

	private LocalDate addExtension(final LocalDate date, final int amount, final TimeUnit unit) {
		return switch (unit) {
			case DAYS -> date.plusDays(amount);
			case MONTHS -> date.plusMonths(amount);
			case YEARS -> date.plusYears(amount);
		};
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
			throw new IllegalStateException("Failed to serialize TERMINATED event for contract %s".formatted(contract.getContractId()), e);
		}
	}
}
