package se.sundsvall.contract.service;

import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;
import static se.sundsvall.contract.service.ContractMapper.toEntity;
import static se.sundsvall.contract.service.ContractMapper.updateEntity;

import java.util.List;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.ContractRepository;

@Service
public class ContractService {

	private final ContractRepository contractRepository;

	public ContractService(final ContractRepository contractRepository) {
		this.contractRepository = contractRepository;
	}

	public String createContract(final String municipalityId, final Contract contract) {
		return contractRepository.save(toEntity(municipalityId, contract)).getId();
	}

	public Contract getContract(final String municipalityId, final String id) {
		return contractRepository.findByMunicipalityIdAndId(municipalityId, id)
			.map(ContractMapper::toDto)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));
	}

	public List<Contract> getContracts(final String municipalityId, final ContractRequest request) {
		return contractRepository.findAll(createContractSpecification(municipalityId, request)).stream()
			.map(ContractMapper::toDto)
			.toList();
	}

	public void updateContract(final String municipalityId, final String id, final Contract contract) {
		final var result = contractRepository.findByMunicipalityIdAndId(municipalityId, id)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));

		final var updatedEntity = updateEntity(result, contract);
		contractRepository.save(updatedEntity);
	}
}
