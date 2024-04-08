package se.sundsvall.contract.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

@Service
@Transactional
public class AttachmentService {

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;
	private final ContractMapper contractMapper;

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s is not present within municipality %s.";
	private static final String CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s and attachmentId %s is not present within municipality %s.";

	public AttachmentService(ContractRepository contractRepository, AttachmentRepository attachmentRepository, ContractMapper contractMapper) {
		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
		this.contractMapper = contractMapper;
	}

	public Long createAttachment(final String municipalityId, final String contractId, final Attachment attachment) {
		if(!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}
		return attachmentRepository.save(contractMapper.toAttachmentEntity(municipalityId, contractId, attachment)).getId();
	}

	@Transactional(readOnly = true)
	public Attachment getAttachment(final String municipalityId, final String contractId, final Long attachmentId) {
		return attachmentRepository.findByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)
			.map(contractMapper::toAttachmentDto)
			.orElseThrow(() -> Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());
	}

	public AttachmentMetaData updateAttachment(@ValidMunicipalityId String municipalityId, String contractId, final Long attachmentId, final Attachment attachment) {
		var result = attachmentRepository.findById(attachmentId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());

		var updatedEntity = contractMapper.updateAttachmentEntity(result, attachment);

		return contractMapper.toAttachmentMetaDataDto(attachmentRepository.save(updatedEntity));
	}

	public void deleteAttachment(final String municipalityId, final String contractId, final Long attachmentId) {
		if(!attachmentRepository.existsByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)) {
			throw Problem.builder()
				.withStatus(Status.NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build();
		}
		attachmentRepository.deleteByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId);
	}
}
