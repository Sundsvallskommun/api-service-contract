package se.sundsvall.contract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Diff;
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
import se.sundsvall.contract.integration.db.projection.ContractVersionProjection;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.model.Action;
import se.sundsvall.contract.service.diff.Differ;
import se.sundsvall.dept44.problem.Problem;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withMunicipalityId;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withOnlyLatestVersion;
import static se.sundsvall.contract.service.businessrule.model.Action.CREATE;
import static se.sundsvall.contract.service.businessrule.model.Action.DELETE;
import static se.sundsvall.contract.service.businessrule.model.Action.UPDATE;
import static se.sundsvall.contract.service.mapper.DtoMapper.toBusinessruleParameters;
import static se.sundsvall.contract.service.mapper.DtoMapper.toContractDto;
import static se.sundsvall.contract.service.mapper.EntityMapper.patchContractEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.toContractEntity;

/**
 * Service for managing contracts.
 */
@Service
public class ContractService {

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId '%s' is not present within municipality '%s'.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND = "Contract with contractId '%s', version '%d' is not present within municipality '%s'.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_DIFF_SINGLE_PROBLEM = "Diff operation cannot be performed: only one version of contract with contractId '%s' exists in municipality '%s'.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_DIFF_VERSION_NOT_FOUND = "Diff operation cannot be performed: version '%d' of contract with contractId '%s' does not exist in municipality '%s'.";
	private static final Sort VERSION_ASC = Sort.by("version").ascending();

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final OutboxRepository outboxRepository;
	private final List<BusinessruleInterface> businessRules;
	private final Differ differ;
	private final ObjectMapper objectMapper;
	private final ContractValidator contractValidator;

	public ContractService(
		final ContractRepository contractRepository,
		final AttachmentRepository attachmentRepository,
		final OutboxRepository outboxRepository,
		final List<BusinessruleInterface> businessRules,
		final Differ differ,
		final ObjectMapper objectMapper,
		final ContractValidator contractValidator) {

		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
		this.outboxRepository = outboxRepository;
		this.businessRules = businessRules;
		this.differ = differ;
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
		// Map to entity, explicitly starting at version 1
		final var contractEntity = toContractEntity(municipalityId, contract);
		contractEntity.setVersion(1);

		// Validate billing constraints on the mapped entity before persisting (no previous endDate for a new contract)
		contractValidator.validate(contractEntity, null);

		// Save entity to create an initial version based on incoming request
		contractRepository.save(contractEntity);

		// Apply matching businessrules
		applyBusinessrules(contractEntity, CREATE);

		// If entity attributes has been altered by business rules, save them as a new version and finally return id
		final var savedContractId = contractRepository.save(contractEntity).getContractId();

		// Notify billing
		outboxRepository.save(toOutboxEntity(contractEntity, ContractCreatedEvent.of(
			contractEntity.getContractId(),
			contractEntity.getMunicipalityId())));

		return savedContractId;
	}

	/**
	 * Retrieves a contract by its id, optionally at a specific version.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  version        optional version number, or null for the latest version
	 * @return                the contract
	 */
	@Transactional(readOnly = true)
	public Contract getContract(final String municipalityId, final String contractId, final Integer version) {
		final Optional<ContractEntity> contractEntity;

		if (version == null) {
			contractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId);
		} else {
			contractEntity = contractRepository.findByMunicipalityIdAndContractIdAndVersion(municipalityId, contractId, version);
		}

		return contractEntity
			.map(entity -> toContractDto(entity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractId)))
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(version != null ? CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND.formatted(contractId, version, municipalityId) : CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
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
		// Combine mandatory specifications with the optional filter
		var specification = withOnlyLatestVersion()
			.and(withMunicipalityId(municipalityId));

		if (filter != null) {
			specification = specification.and(filter);
		}

		// Get all contracts and map to DTOs
		return contractRepository.findAll(specification, pageable)
			.map(contractEntity -> toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractEntity.getContractId())));
	}

	/**
	 * Patches an existing contract in place, applying only the non-null fields from the payload.
	 * Does not create a new version, but triggers matching business rules the same way as a create.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param patch          the partial contract data to apply
	 */
	@Transactional
	public void patchContract(final String municipalityId, final String contractId, final PatchContract patch) {
		final var existingEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());

		// Capture the previously stored endDate before the patch mutates the entity in place
		final var previousEndDate = existingEntity.getEndDate();

		// Apply the patch payload in place on the existing entity (version preserved)
		patchContractEntity(existingEntity, patch);

		// Validate billing constraints on the merged entity before persisting
		contractValidator.validate(existingEntity, previousEndDate);

		// Apply matching businessrules
		applyBusinessrules(existingEntity, UPDATE);

		// Notify billing
		outboxRepository.save(toOutboxEntity(existingEntity, ContractUpdatedEvent.of(
			existingEntity.getContractId(),
			existingEntity.getMunicipalityId())));

		// Save changes on the existing entity — no new version is created
		contractRepository.save(existingEntity);
	}

	/**
	 * Compares two versions of a contract and returns their differences.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  oldVersion     optional old version number, or null for the version before the new version
	 * @param  newVersion     optional new version number, or null for the latest version
	 * @return                the diff result containing changes between the two versions
	 */
	@Transactional(readOnly = true)
	public Diff diffContract(final String municipalityId, final String contractId, final Integer oldVersion, final Integer newVersion) {
		final var availableVersions = contractRepository.findByMunicipalityIdAndContractId(municipalityId, contractId, VERSION_ASC)
			.stream()
			.map(ContractVersionProjection::getVersion)
			.toList();

		if (availableVersions.size() < 2) {
			throw Problem.valueOf(BAD_REQUEST, CONTRACT_ID_MUNICIPALITY_ID_DIFF_SINGLE_PROBLEM.formatted(contractId, municipalityId));
		}

		final var actualNewVersion = ofNullable(newVersion).orElse(availableVersions.getLast());
		if (!availableVersions.contains(actualNewVersion)) {
			throw Problem.valueOf(BAD_REQUEST, CONTRACT_ID_MUNICIPALITY_ID_DIFF_VERSION_NOT_FOUND.formatted(actualNewVersion, contractId, municipalityId));
		}

		final var actualOldVersion = ofNullable(oldVersion).orElse(actualNewVersion - 1);
		if (!availableVersions.contains(actualOldVersion)) {
			throw Problem.valueOf(BAD_REQUEST, CONTRACT_ID_MUNICIPALITY_ID_DIFF_VERSION_NOT_FOUND.formatted(actualOldVersion, contractId, municipalityId));
		}

		final var newContract = getContract(municipalityId, contractId, actualNewVersion);
		final var oldContract = getContract(municipalityId, contractId, actualOldVersion);
		final var changes = differ.diff(oldContract, newContract, List.of("$.id", "$.version"));

		return new Diff(actualOldVersion, actualNewVersion, changes, availableVersions);
	}

	/**
	 * Deletes a contract and all its attachments, applying matching business rules before deletion.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 */
	@Transactional
	public void deleteContract(final String municipalityId, final String contractId) {
		// Fetch contract
		final var contractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());

		// Apply matching businessrules
		applyBusinessrules(contractEntity, DELETE);

		// Notify billing before deletion
		outboxRepository.save(toOutboxEntity(contractEntity, ContractDeletedEvent.of(
			contractEntity.getContractId(),
			contractEntity.getMunicipalityId())));

		attachmentRepository.deleteAllByMunicipalityIdAndContractId(contractEntity.getMunicipalityId(), contractEntity.getContractId());
		contractRepository.deleteAllByMunicipalityIdAndContractId(contractEntity.getMunicipalityId(), contractEntity.getContractId());
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
