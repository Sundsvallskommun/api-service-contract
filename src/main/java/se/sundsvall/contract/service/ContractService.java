package se.sundsvall.contract.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractHolder;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;

@Service
public class ContractService {

	public Long createContract(final Contract contract) {
		// to be implemented properly
		return contract.getCaseId();
	}

	public Contract getContract(final Long id) {
		// to be implemented properly
		return LandLeaseContract.builder()
			.withCaseId(id)
			.build();
	}

	public List<Contract> getContracts(final ContractRequest request) {
		// to be implemented properly
		return List.of(LandLeaseContract.builder()
			.withPropertyDesignation(request.propertyDesignation())
			.build());
	}

	public void updateContract(final Long id, final ContractHolder contractHolder) {
		// to be implemented properly
	}

}
