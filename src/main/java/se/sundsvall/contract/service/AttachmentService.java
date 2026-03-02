package se.sundsvall.contract.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.service.mapper.DtoMapper;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.contract.service.mapper.DtoMapper.toAttachmentMetaDataDto;
import static se.sundsvall.contract.service.mapper.EntityMapper.toAttachmentEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.updateAttachmentEntity;

/**
 * Service for managing contract attachments.
 */
@Service
@Transactional
public class AttachmentService {

	private final ContractRepository contractRepository;
	private final AttachmentRepository attachmentRepository;

	private static final String CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId '%s' is not present within municipality '%s'.";
	private static final String CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND = "Contract with contractId '%s' and attachmentId '%s' is not present within municipality '%s'.";

	public AttachmentService(
		final ContractRepository contractRepository,
		final AttachmentRepository attachmentRepository) {

		this.contractRepository = contractRepository;
		this.attachmentRepository = attachmentRepository;
	}

	/**
	 * Creates a new attachment for a contract.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  attachment     the attachment data
	 * @return                the generated attachment id
	 */
	public Long createAttachment(final String municipalityId, final String contractId, final Attachment attachment) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}
		return attachmentRepository.save(toAttachmentEntity(municipalityId, contractId, attachment)).getId();
	}

	/**
	 * Retrieves an attachment by its id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  attachmentId   the attachment id
	 * @return                the attachment
	 */
	@Transactional(readOnly = true)
	public Attachment getAttachment(final String municipalityId, final String contractId, final Long attachmentId) {
		return attachmentRepository.findByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)
			.map(DtoMapper::toAttachmentDto)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());
	}

	/**
	 * Updates an existing attachment.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  attachmentId   the attachment id
	 * @param  attachment     the updated attachment data
	 * @return                the updated attachment metadata
	 */
	public AttachmentMetadata updateAttachment(final String municipalityId, final String contractId, final Long attachmentId, final Attachment attachment) {
		final var result = attachmentRepository.findByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());

		final var updatedEntity = updateAttachmentEntity(result, attachment);

		return toAttachmentMetaDataDto(attachmentRepository.save(updatedEntity));
	}

	/**
	 * Deletes an attachment.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param attachmentId   the attachment id
	 */
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
