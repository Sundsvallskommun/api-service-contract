package se.sundsvall.contract.service;

import static se.sundsvall.contract.integration.db.specification.ContractSpecification.createContractSpecification;
import static se.sundsvall.contract.service.ContractMapper.toEntity;
import static se.sundsvall.contract.service.ContractMapper.updateEntity;

import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.ContractRepository;

@Service
public class ContractService {

	private final ContractRepository contractRepository;

	public ContractService(final ContractRepository contractRepository) {this.contractRepository = contractRepository;}

	public Long createContract(final Contract contract) {

		return contractRepository.save(toEntity(contract)).getId();
	}

	public Contract getContract(final Long id) {
		return contractRepository.findById(id).map(ContractMapper::toDto).orElseThrow();
	}

	public List<Contract> getContracts(final ContractRequest request) {

		return contractRepository.findAll(createContractSpecification(request)).stream()
			.map(ContractMapper::toDto)
			.toList();
	}

	public void updateContract(final Long id, final Contract contract) {
		final var result = contractRepository.findById(id).orElseThrow();
		final var updatedEntity = updateEntity(result, contract);
		contractRepository.save(updatedEntity);
	}

}
