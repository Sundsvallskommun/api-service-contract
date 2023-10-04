package se.sundsvall.contract.service;

import static se.sundsvall.contract.integration.db.specification.ContractSpecification.createContractSpecification;
import static se.sundsvall.contract.service.ContractMapper.toEntity;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;

@Service
public class ContractService {

	private final ContractRepository contractRepository;

	private final ObjectMapper objectMapper;

	public ContractService(final ContractRepository contractRepository, final ObjectMapper objectMapper) {
		this.contractRepository = contractRepository;
		this.objectMapper = objectMapper;

		objectMapper.registerModule(new JavaTimeModule());
	}

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

	public void updateContract(final Long id, final JsonPatch patch) {

		try {
			final var entity = contractRepository.findById(id)
				.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND,
					"Contract with id %s not found".formatted(id)));

			final var patchedEntity = patch.apply(objectMapper.convertValue(entity, JsonNode.class));
			contractRepository.save(objectMapper.treeToValue(patchedEntity, ContractEntity.class));

		} catch (final JsonPatchException | JsonProcessingException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR,
				"Failed to apply patch %s with exception %s".formatted(patch, e.getMessage()));
		}
	}

}
