package se.sundsvall.contract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.PatchContract;
import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.model.Action;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withMunicipalityId;
import static se.sundsvall.contract.service.businessrule.model.Action.CREATE;
import static se.sundsvall.contract.service.businessrule.model.Action.DELETE;
import static se.sundsvall.contract.service.businessrule.model.Action.UPDATE;
import static se.sundsvall.contract.service.mapper.DtoMapper.toBusinessruleParameters;
import static se.sundsvall.contract.service.mapper.DtoMapper.toContractDto;
import static se.sundsvall.contract.service.mapper.EntityMapper.patchContractEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.toContractEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.updateContractEntity;

/**
 * Service for managing contracts.
 */
@Service
public class ContractService {

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId '%s' is not present within municipality '%s'.";

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final OutboxRepository outboxRepository;
	private final List<BusinessruleInterface> businessRules;
	private final ObjectMapper objectMapper;
	private final ContractValidator contractValidator;

	public ContractService(
		final ContractRepository contractRepository,
		final AttachmentRepository attachmentRepository,
		final OutboxRepository outboxRepository,
		final List<BusinessruleInterface> businessRules,
		final ObjectMapper objectMapper,
		final ContractValidator contractValidator) {

		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
		this.outboxRepository = outboxRepository;
		this.businessRules = businessRules;
		this.objectMapper = objectMapper;
		this.contractValidator = contractValidator;
	}

	/**
	 * Creates a new contract and applies matching business rules.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contract       the contract data
	 * @return                the generated contract id
	 */
	@Transactional
	public String createContract(final String municipalityId, final Contract contract) {
		final var contractEntity = toContractEntity(municipalityId, contract);

		// Validate billing constraints on the mapped entity before persisting (no previous endDate for a new contract)
		contractValidator.validate(contractEntity, null);

		// Save the entity based on the incoming request
		contractRepository.save(contractEntity);

		// Apply matching businessrules
		applyBusinessrules(contractEntity, CREATE);

		// If entity attributes have been altered by business rules, persist the changes and finally return the id
		final var savedContractId = contractRepository.save(contractEntity).getContractId();

		// Notify billing
		outboxRepository.save(toOutboxEntity(contractEntity, ContractCreatedEvent.of(
			contractEntity.getContractId(),
			contractEntity.getMunicipalityId())));

		return savedContractId;
	}

	/**
	 * Retrieves a contract by its id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                the contract
	 */
	@Transactional(readOnly = true)
	public Contract getContract(final String municipalityId, final String contractId) {
		return contractRepository.findByMunicipalityIdAndContractId(municipalityId, contractId)
			.map(entity -> toContractDto(entity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractId)))
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());
	}

	/**
	 * Retrieves a paginated list of contracts, optionally filtered.
	 *
	 * @param  municipalityId the municipality id
	 * @param  filter         optional specification filter
	 * @param  pageable       pagination parameters
	 * @return                a page of contracts
	 */
	@Transactional(readOnly = true)
	public Page<Contract> getContracts(final String municipalityId, final Specification<ContractEntity> filter, final Pageable pageable) {
		var specification = withMunicipalityId(municipalityId);

		if (filter != null) {
			specification = specification.and(filter);
		}

		return contractRepository.findAll(specification, pageable)
			.map(contractEntity -> toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractEntity.getContractId())));
	}

	/**
	 * Patches an existing contract in place, applying only the non-null fields from the payload and triggering matching
	 * business rules.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param patch          the partial contract data to apply
	 */
	@Transactional
	public void patchContract(final String municipalityId, final String contractId, final PatchContract patch) {
		final var existingEntity = findContract(municipalityId, contractId);

		// Capture the previously stored endDate before the patch mutates the entity in place
		final var previousEndDate = existingEntity.getEndDate();

		// Apply the patch payload in place on the existing entity
		patchContractEntity(existingEntity, patch);

		validateApplyRulesAndNotify(existingEntity, previousEndDate);
	}

	/**
	 * Updates a contract in place by replacing its data with the given contract and triggering matching business rules.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param contract       the updated contract data
	 */
	@Transactional
	public void updateContract(final String municipalityId, final String contractId, final Contract contract) {
		final var existingEntity = findContract(municipalityId, contractId);

		// Capture the previously stored endDate before the update overwrites the entity in place
		final var previousEndDate = existingEntity.getEndDate();

		// Replace the existing entity's fields with the incoming contract data (in place — same row)
		updateContractEntity(existingEntity, contract);

		validateApplyRulesAndNotify(existingEntity, previousEndDate);
	}

	/**
	 * Deletes a contract and all its attachments, applying matching business rules before deletion.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 */
	@Transactional
	public void deleteContract(final String municipalityId, final String contractId) {
		final var contractEntity = findContract(municipalityId, contractId);

		// Apply matching businessrules
		applyBusinessrules(contractEntity, DELETE);

		// Notify billing before deletion
		outboxRepository.save(toOutboxEntity(contractEntity, ContractDeletedEvent.of(
			contractEntity.getContractId(),
			contractEntity.getMunicipalityId())));

		attachmentRepository.deleteAllByMunicipalityIdAndContractId(contractEntity.getMunicipalityId(), contractEntity.getContractId());
		contractRepository.deleteAllByMunicipalityIdAndContractId(contractEntity.getMunicipalityId(), contractEntity.getContractId());
	}

	/**
	 * Shared tail of {@link #patchContract} and {@link #updateContract}: validate the mutated entity, apply UPDATE
	 * business rules, write the billing outbox event and persist the changes in place.
	 */
	private void validateApplyRulesAndNotify(final ContractEntity entity, final LocalDate previousEndDate) {
		contractValidator.validate(entity, previousEndDate);
		applyBusinessrules(entity, UPDATE);
		outboxRepository.save(toOutboxEntity(entity, ContractUpdatedEvent.of(entity.getContractId(), entity.getMunicipalityId())));
		contractRepository.save(entity);
	}

	private ContractEntity findContract(final String municipalityId, final String contractId) {
		return contractRepository.findByMunicipalityIdAndContractId(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());
	}

	private void applyBusinessrules(ContractEntity contractEntity, Action action) {
		businessRules.stream()
			.filter(rule -> rule.appliesTo(contractEntity))
			.forEach(rule -> rule.apply(toBusinessruleParameters(contractEntity, action)));
	}

	private OutboxEntity toOutboxEntity(final ContractEntity contract, final BillingEvent event) {
		try {
			return OutboxEntity.builder()
				.withContractId(contract.getContractId())
				.withEventType(event.eventType())
				.withPayload(objectMapper.writeValueAsString(event))
				.build();
		} catch (final JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize %s event for contract %s".formatted(event.eventType(), contract.getContractId()), e);
		}
	}
}
