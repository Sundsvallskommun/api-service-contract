package se.sundsvall.contract.service;

import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;
import static se.sundsvall.contract.service.ContractMapper.toEntity;
import static se.sundsvall.contract.service.ContractMapper.updateEntity;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.ContractRepository;

@Service
@Transactional
public class ContractService {

	private final ContractRepository contractRepository;

	public ContractService(final ContractRepository contractRepository) {
		this.contractRepository = contractRepository;
	}

	public String createContract(final String municipalityId, final Contract contract) {
		var entity = toEntity(municipalityId, contract);
		return contractRepository.save(entity).getContractId();
	}

	@Transactional(readOnly = true)
	public Contract getContract(final String municipalityId, final String contractId) {
		return contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.map(ContractMapper::toDto)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<Contract> getContracts(final String municipalityId, final ContractRequest request) {
		return contractRepository.findAll(createContractSpecification(municipalityId, request)).stream()
			.map(ContractMapper::toDto)
			.toList();
	}

	public void updateContract(final String municipalityId, final String contractId, final Contract contract) {
		var latest = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.map(oldEntity -> updateEntity(oldEntity, contract))
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));

		contractRepository.save(latest);
	}

	public void deleteContract(String municipalityId, String contractId) {
		if(contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			contractRepository.deleteAllByMunicipalityIdAndContractId(municipalityId, contractId);
		} else {
			throw Problem.valueOf(Status.NOT_FOUND);
		}
	}
}
