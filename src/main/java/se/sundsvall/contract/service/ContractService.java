package se.sundsvall.contract.service;

import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractPaginatedResponse;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Diff;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.projection.ContractVersionProjection;
import se.sundsvall.contract.service.diff.Differ;
import se.sundsvall.contract.service.mapper.DtoMapper;
import se.sundsvall.contract.service.mapper.EntityMapper;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

@Service
@Transactional
public class ContractService {

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s is not present within municipality %s.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND = "Contract with contractId %s, version %d is not present within municipality %s.";
	private static final Sort VERSION_ASC = Sort.by("version").ascending();

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final DtoMapper dtoMapper;
	private final EntityMapper entityMapper;
	private final Differ differ;

	public ContractService(final ContractRepository contractRepository,
		final AttachmentRepository attachmentRepository, DtoMapper dtoMapper, final EntityMapper entityMapper,
		final Differ differ) {
		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
		this.dtoMapper = dtoMapper;
		this.entityMapper = entityMapper;
		this.differ = differ;
	}

	public String createContract(final String municipalityId, final Contract contract) {
		return contractRepository.save(entityMapper.toContractEntity(municipalityId, contract)).getContractId();
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
			.map(entity -> dtoMapper.toContractDto(entity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractId)))
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(version != null ? CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND.formatted(contractId, version, municipalityId) : CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());
	}

	@Transactional(readOnly = true)
	public ContractPaginatedResponse getContracts(final String municipalityId, final ContractRequest request) {
		final var pagingParameters = getPagingParameters(request);

		// Get all contracts
		final var contractEntities = contractRepository.findAll(createContractSpecification(municipalityId, request), pagingParameters);

		// Map to response objects
		final var contracts = contractEntities.stream()
			.map(contractEntity -> dtoMapper.toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractEntity.getContractId())))
			.toList();

		// Add to response
		return ContractPaginatedResponse.builder()
			.withMetaData(PagingAndSortingMetaData.create().withPageData(contractEntities))
			.withContracts(contracts)
			.build();
	}

	private Pageable getPagingParameters(final ContractRequest request) {
		return PageRequest.of(request.getPage() - 1, request.getLimit());
	}

	public void updateContract(final String municipalityId, final String contractId, final Contract contract) {
		final var oldContractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());

		// Create a new entity and save it
		final var newContractEntity = entityMapper.createNewContractEntity(municipalityId, oldContractEntity, contract);
		contractRepository.save(newContractEntity);
	}

	@Transactional(readOnly = true)
	public Diff diffContract(final String municipalityId, final String contractId, final Integer oldVersion, final Integer newVersion) {
		final var availableVersions = contractRepository.findByMunicipalityIdAndContractId(municipalityId, contractId, VERSION_ASC)
			.stream()
			.map(ContractVersionProjection::getVersion)
			.toList();

		if (availableVersions.size() < 2) {
			throw Problem.valueOf(BAD_REQUEST, "Unable to diff since a single version of the contract with contractId %s within municipality %s".formatted(contractId, municipalityId));
		}

		final var actualNewVersion = ofNullable(newVersion).orElse(availableVersions.getLast());
		final var actualOldVersion = ofNullable(oldVersion).orElse(actualNewVersion - 1);

		final var newContract = getContract(municipalityId, contractId, actualNewVersion);
		final var oldContract = getContract(municipalityId, contractId, actualOldVersion);
		final var changes = differ.diff(oldContract, newContract, List.of("$.id", "$.version"));

		return new Diff(actualOldVersion, actualNewVersion, changes, availableVersions);
	}

	public void deleteContract(final String municipalityId, final String contractId) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}

		attachmentRepository.deleteAllByContractId(contractId);
		contractRepository.deleteAllByMunicipalityIdAndContractId(municipalityId, contractId);
	}
}
