package se.sundsvall.contract.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.contract.api.model.ContractHolder;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;

@Service
public class ContractService {

	public Long postContract(final LandLeaseContract contract) {
		// to be implemented properly
		return contract.getCaseId();
	}

	public LandLeaseContract getContract(final Long id) {
		// to be implemented properly
		return LandLeaseContract.builder()
			.withCaseId(id)
			.build();
	}

	public List<LandLeaseContract> getContracts(final ContractRequest request) {
		// to be implemented properly
		return List.of(LandLeaseContract.builder()
			.withPropertyDesignation(request.propertyDesignation())
			.build());
	}

	public void patchContract(final Long id, final ContractHolder contractHolder) {
		// to be implemented properly
	}

}
