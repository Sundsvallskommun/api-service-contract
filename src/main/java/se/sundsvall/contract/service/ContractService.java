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
		final var entity = toEntity(contract);
		final var result = contractRepository.save(entity);
		return result.getId();
	}

	public Contract getContract(final Long id) {
		final var result = contractRepository.findById(id).orElseThrow();
		return ContractMapper.toDto(result);
	}

	public List<Contract> getContracts(final ContractRequest request) {

		final var result = contractRepository.findAll(createContractSpecification(request));
		return result.stream().map(ContractMapper::toDto).toList();
	}

	public void updateContract(final Long id, final Contract contract) {

		final var result = contractRepository.findById(id).orElseThrow();
		final var updatedEntity = updateEntity(result, contract);
		contractRepository.save(updatedEntity);
	}

}
