package se.sundsvall.contract.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withMunicipalityId;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.withOnlyLatestVersion;
import static se.sundsvall.contract.service.businessrule.model.Action.CREATE;
import static se.sundsvall.contract.service.businessrule.model.Action.DELETE;
import static se.sundsvall.contract.service.businessrule.model.Action.UPDATE;
import static se.sundsvall.contract.service.mapper.DtoMapper.toBusinessruleParameters;
import static se.sundsvall.contract.service.mapper.DtoMapper.toContractDto;
import static se.sundsvall.contract.service.mapper.EntityMapper.createNewContractEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.toContractEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Diff;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.projection.ContractVersionProjection;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.model.Action;
import se.sundsvall.contract.service.diff.Differ;

@Service
@Transactional
public class ContractService {

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId '%s' is not present within municipality '%s'.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND = "Contract with contractId '%s', version '%d' is not present within municipality '%s'.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_DIFF_SINGLE_PROBLEM = "Diff operation cannot be performed: only one version of contract with contractId '%s' exists in municipality '%s'.";
	private static final Sort VERSION_ASC = Sort.by("version").ascending();

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final List<BusinessruleInterface> businessRules;

	private final Differ differ;

	public ContractService(
		final ContractRepository contractRepository,
		final AttachmentRepository attachmentRepository,
		final List<BusinessruleInterface> businessRules,
		final Differ differ) {

		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
		this.businessRules = businessRules;
		this.differ = differ;
	}

	public String createContract(final String municipalityId, final Contract contract) {
		// Map to entity
		final var contractEntity = toContractEntity(municipalityId, contract);

		// Save entity to create an initial version based on incoming request
		contractRepository.save(contractEntity);

		// Apply matching businessrules
		applyBusinessrules(contractEntity, CREATE);

		// If entity attributes has been altered by business rules, save them as a new version and finally return id
		return contractRepository.save(contractEntity).getContractId();
	}

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

	@Transactional(readOnly = true)
	public Page<Contract> getContracts(final String municipalityId, final Specification<ContractEntity> filter, final Pageable pageable) {
		// Combine mandatory specifications with the optional filter
		final var specification = withOnlyLatestVersion()
			.and(withMunicipalityId(municipalityId))
			.and(filter);

		// Get all contracts and map to DTOs
		return contractRepository.findAll(specification, pageable)
			.map(contractEntity -> toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractEntity.getContractId())));
	}

	public void updateContract(final String municipalityId, final String contractId, final Contract contract) {
		final var oldContractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());

		// Create a new entity
		final var newContractEntity = createNewContractEntity(municipalityId, oldContractEntity, contract);

		// Apply matching businessrules
		applyBusinessrules(newContractEntity, UPDATE);

		// Save changes
		contractRepository.save(newContractEntity);
	}

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
		final var actualOldVersion = ofNullable(oldVersion).orElse(actualNewVersion - 1);

		final var newContract = getContract(municipalityId, contractId, actualNewVersion);
		final var oldContract = getContract(municipalityId, contractId, actualOldVersion);
		final var changes = differ.diff(oldContract, newContract, List.of("$.id", "$.version"));

		return new Diff(actualOldVersion, actualNewVersion, changes, availableVersions);
	}

	public void deleteContract(final String municipalityId, final String contractId) {
		// Fetch contract
		final var contractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());

		// Apply matching businessrules
		applyBusinessrules(contractEntity, DELETE);

		attachmentRepository.deleteAllByContractId(contractEntity.getContractId());
		contractRepository.deleteAllByMunicipalityIdAndContractId(contractEntity.getMunicipalityId(), contractEntity.getContractId());
	}

	/**
	 * Method applies all matching business rules for the sent in contract entity
	 *
	 * @param contractEntity the contract to process
	 * @param action         the performed action on the contract
	 */
	private void applyBusinessrules(ContractEntity contractEntity, Action action) {
		ofNullable(businessRules).orElse(emptyList()).stream()
			.filter(rule -> rule.appliesTo(contractEntity))
			.forEach(rule -> rule.apply(toBusinessruleParameters(contractEntity, action)));
	}
}
