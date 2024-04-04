package se.sundsvall.contract.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
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
	public AttachmentData getAttachmentData(final Long id) {
		return attachmentRepository.findById(id)
			.map(contractMapper::toAttachmentDataDto)
			.map(dto -> AttachmentData.builder()
				.withContent(dto.getContent())
				.build())
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));
	}

	public AttachmentMetaData updateAttachment(final Long attachmentId, final Attachment attachment) {
		var result = attachmentRepository.findById(attachmentId)
			.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND));

		var updatedEntity = contractMapper.updateAttachmentEntity(result, attachment);

		return contractMapper.toAttachmentMetaDataDto(attachmentRepository.save(updatedEntity));
	}

	public void deleteAttachment(final Long attachmentId) {
		attachmentRepository.deleteById(attachmentId);
	}
}
