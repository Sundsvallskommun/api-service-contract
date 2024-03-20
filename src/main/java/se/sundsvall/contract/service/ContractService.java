package se.sundsvall.contract.service;

import static se.sundsvall.contract.integration.db.specification.ContractSpecifications.createContractSpecification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;

@Service
@Transactional
public class ContractService {

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final ContractMapper contractMapper;

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
			.map(contractEntity -> contractMapper.toContractDto(contractEntity, attachmentRepository.findAllByContractId(contractId)))
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<Contract> getContracts(final String municipalityId, final ContractRequest request) {
		return contractRepository.findAll(createContractSpecification(municipalityId, request)).stream()
			.map(contractEntity -> contractMapper.toContractDto(contractEntity, attachmentRepository.findAllByContractId(contractEntity.getContractId())))
			.toList();
	}

	public void updateContract(final String municipalityId, final String contractId, final Contract contract) {
		var latest = contractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(municipalityId, contractId)
			.map(oldEntity -> contractMapper.updateContractEntity(oldEntity, contract))
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));

		contractRepository.save(latest);
	}

	public void deleteContract(final String municipalityId, final String contractId) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.valueOf(Status.NOT_FOUND);
		}

		contractRepository.deleteAllByMunicipalityIdAndContractId(municipalityId, contractId);
	}

	public Long createAttachment(final String contractId, final Attachment attachment) {
		return attachmentRepository.save(contractMapper.toAttachmentEntity(contractId, attachment)).getId();
	}

	@Transactional(readOnly = true)
	public Attachment getAttachment(final Long id) {
		return attachmentRepository.findById(id)
			.map(contractMapper::toAttachmentDto)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));
	}

	public Attachment updateAttachment(final Long attachmentId, final Attachment attachment) {
		var result = attachmentRepository.findById(attachmentId)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));

		var updatedEntity = contractMapper.updateAttachmentEntity(result, attachment);

		return contractMapper.toAttachmentDto(attachmentRepository.save(updatedEntity));
	}

	public void deleteAttachment(final Long attachmentId) {
		attachmentRepository.deleteById(attachmentId);
	}
}
