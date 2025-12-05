package se.sundsvall.contract.service;

import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.contract.service.mapper.DtoMapper.toAttachmentMetaDataDto;
import static se.sundsvall.contract.service.mapper.EntityMapper.toAttachmentEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.updateAttachmentEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.service.mapper.DtoMapper;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

@Service
@Transactional
public class AttachmentService {

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s is not present within municipality %s.";
	private static final String CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId %s and attachmentId %s is not present within municipality %s.";

	public AttachmentService(
		final ContractRepository contractRepository,
		final AttachmentRepository attachmentRepository) {

		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
	}

	public Long createAttachment(final String municipalityId, final String contractId, final Attachment attachment) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}
		return attachmentRepository.save(toAttachmentEntity(municipalityId, contractId, attachment)).getId();
	}

	@Transactional(readOnly = true)
	public Attachment getAttachment(final String municipalityId, final String contractId, final Long attachmentId) {
		return attachmentRepository.findByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)
			.map(DtoMapper::toAttachmentDto)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());
	}

	public AttachmentMetadata updateAttachment(@ValidMunicipalityId String municipalityId, String contractId, final Long attachmentId, final Attachment attachment) {
		final var result = attachmentRepository.findById(attachmentId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());

		final var updatedEntity = updateAttachmentEntity(result, attachment);

		return toAttachmentMetaDataDto(attachmentRepository.save(updatedEntity));
	}

	public void deleteAttachment(final String municipalityId, final String contractId, final Long attachmentId) {
		if (!attachmentRepository.existsByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)) {
			throw Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build();
		}

		attachmentRepository.deleteByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId);
	}
}
