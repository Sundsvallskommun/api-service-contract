package se.sundsvall.contract.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;

@Service
@Transactional
public class AttachmentService {

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final ContractMapper contractMapper;

	public AttachmentService(ContractRepository contractRepository, AttachmentRepository attachmentRepository, ContractMapper contractMapper) {
		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
		this.contractMapper = contractMapper;
	}

	public Long createAttachment(final String municipalityId, final String contractId, final Attachment attachment) {
		if(!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.valueOf(Status.NOT_FOUND);
		}
		return attachmentRepository.save(contractMapper.toAttachmentEntity(contractId, attachment)).getId();
	}

	@Transactional(readOnly = true)
	public Attachment getAttachment(final String contractId, final Long attachmentId) {
		return attachmentRepository.findByContractIdAndId(contractId, attachmentId)
			.map(contractMapper::toAttachmentDto)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));
	}

	public AttachmentMetaData updateAttachment(final Long attachmentId, final Attachment attachment) {
		var result = attachmentRepository.findById(attachmentId)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));

		var updatedEntity = contractMapper.updateAttachmentEntity(result, attachment);

		return contractMapper.toAttachmentMetaDataDto(attachmentRepository.save(updatedEntity));
	}

	public void deleteAttachment(final String contractId, final Long attachmentId) {
		attachmentRepository.deleteByContractIdAndId(contractId, attachmentId);
	}
}
