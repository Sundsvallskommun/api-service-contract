package se.sundsvall.contract.service;

import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractPaginatedResponse;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

@Service
@Transactional
public class ContractService {

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final ContractMapper contractMapper;

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s is not present within municipality %s.";


	public ContractService(final ContractRepository contractRepository,
			final AttachmentRepository attachmentRepository, final ContractMapper contractMapper) {
		this.contractRepository = contractRepository;
        this.attachmentRepository = attachmentRepository;
		this.contractMapper = contractMapper;
    }

	public String createContract(final String municipalityId, final Contract contract) {
		var entity = contractMapper.toContractEntity(municipalityId, contract);

		return contractRepository.save(entity).getContractId();
	}

	@Transactional(readOnly = true)
	public Contract getContract(final String municipalityId, final String contractId) {
		return contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.map(contractEntity -> contractMapper.toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractId)))
			.orElseThrow(() -> Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build());
	}

	@Transactional(readOnly = true)
	public ContractPaginatedResponse getContracts(final String municipalityId, final ContractRequest request) {
		var pagingParameters = getPagingParameters(request);

		//Get all contracts
		var contractEntities = contractRepository.findAll(createContractSpecification(municipalityId, request), pagingParameters);

		//Map to response objects
		var contracts = contractEntities.stream()
			.map(contractEntity -> contractMapper.toContractDto(contractEntity, attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractEntity.getContractId())))
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
		var newContractEntity = contractMapper.createNewContractEntity(municipalityId, oldContractEntity, contract);
		contractRepository.save(newContractEntity);

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
