package se.sundsvall.contract.service;

import static java.util.Optional.ofNullable;
import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractPaginatedResponse;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Diff;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.service.diff.Differ;
import se.sundsvall.contract.service.mapper.DtoMapper;
import se.sundsvall.contract.service.mapper.EntityMapper;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

@Service
@Transactional
public class ContractService {

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s is not present within municipality %s.";
	private static final String CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND = "Contract with contractId %s, version %d is not present within municipality %s.";

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
		var entity = entityMapper.toContractEntity(municipalityId, contract);

		return contractRepository.save(entity).getContractId();
	}

	@Transactional(readOnly = true)
	public Contract getContract(final String municipalityId, final String contractId, final Integer version) {
		Optional<ContractEntity> contractEntity;

		if (version == null) {
			contractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId);
		} else {
			contractEntity = contractRepository.findByMunicipalityIdAndContractIdAndVersion(municipalityId, contractId, version);
		}

		return contractEntity
			.map(entity -> dtoMapper.toContractDto(entity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractId)))
			.orElseThrow(() -> Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(version != null ?
					CONTRACT_ID_MUNICIPALITY_ID_VERSION_NOT_FOUND.formatted(contractId, version, municipalityId) :
					CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());
	}

	@Transactional(readOnly = true)
	public ContractPaginatedResponse getContracts(final String municipalityId, final ContractRequest request) {
		var pagingParameters = getPagingParameters(request);

		//Get all contracts
		var contractEntities = contractRepository.findAll(createContractSpecification(municipalityId, request), pagingParameters);

		//Map to response objects
		var contracts = contractEntities.stream()
			.map(contractEntity -> dtoMapper.toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractEntity.getContractId())))
			.toList();

		//Add to response
		return ContractPaginatedResponse.builder()
			.withMetaData(PagingAndSortingMetaData.create().withPageData(contractEntities))
			.withContracts(contracts)
			.build();
	}

	private Pageable getPagingParameters(final ContractRequest request) {
		return PageRequest.of(request.getPage() - 1, request.getLimit());
	}

	public void updateContract(final String municipalityId, final String contractId, final Contract contract) {
		var oldContractEntity = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());

		//Create a new entity and save it
		var newContractEntity = entityMapper.createNewContractEntity(municipalityId, oldContractEntity, contract);
		contractRepository.save(newContractEntity);
	}

	@Transactional(readOnly = true)
	public Diff diffContract(final String municipalityId, final String contractId, final Integer oldVersion, final Integer newVersion) {
		var availableVersions = contractRepository.findAllContractVersionsByMunicipalityIdAndContractId(municipalityId, contractId);

		if (availableVersions.size() < 2) {
			throw Problem.valueOf(Status.BAD_REQUEST, "Unable to diff since a single version of the contract with contractId %s within municipality %s".formatted(municipalityId, contractId));
		}

		var actualNewVersion = ofNullable(newVersion).orElse(availableVersions.getLast());
		var actualOldVersion = ofNullable(oldVersion).orElse(actualNewVersion - 1);

		var newContract = getContract(municipalityId, contractId, actualNewVersion);
		var oldContract = getContract(municipalityId, contractId, actualOldVersion);
		var changes = differ.diff(oldContract, newContract, List.of("$.id", "$.version"));

		return new Diff(actualOldVersion, actualNewVersion, changes, availableVersions);
	}

	public void deleteContract(final String municipalityId, final String contractId) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}

		attachmentRepository.deleteAllByContractId(contractId);
		contractRepository.deleteAllByMunicipalityIdAndContractId(municipalityId, contractId);
	}
}
