package se.sundsvall.contract.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.AttachmentDataEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.dept44.problem.Problem;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static se.sundsvall.contract.service.mapper.DtoMapper.toAttachmentMetadataDtos;
import static se.sundsvall.contract.service.mapper.EntityMapper.toAttachmentEntity;
import static se.sundsvall.contract.service.mapper.EntityMapper.updateAttachmentEntity;
import static se.sundsvall.dept44.util.HashUtils.sha256Hex;

/**
 * Service for managing contract attachments.
 *
 * <p>
 * Attachment content is binary and immutable once uploaded - to replace it, the attachment is deleted and recreated.
 * Only the metadata can be patched.
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
	 * @param  metadata       the attachment metadata
	 * @param  file           the uploaded file
	 * @return                the generated attachment id
	 */
	public Long createAttachment(final String municipalityId, final String contractId, final AttachmentMetadata metadata, final MultipartFile file) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}

		// Read once, then derive both the hash and the blob from the same bytes - re-reading a multipart stream is not
		// guaranteed to be supported, and hashing needs a full pass over the content anyway.
		final var content = readContent(file);

		final var entity = toAttachmentEntity(municipalityId, contractId, metadata);
		entity.setHash(sha256Hex(content));
		entity.setAttachmentData(AttachmentDataEntity.builder()
			.withFile(Hibernate.getLobHelper().createBlob(content))
			.build());

		return attachmentRepository.save(entity).getId();
	}

	/**
	 * Retrieves the metadata of all attachments belonging to a contract.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                the attachment metadata
	 */
	@Transactional(readOnly = true)
	public List<AttachmentMetadata> getAttachments(final String municipalityId, final String contractId) {
		if (!contractRepository.existsByMunicipalityIdAndContractId(municipalityId, contractId)) {
			throw Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, municipalityId))
				.build();
		}

		return toAttachmentMetadataDtos(attachmentRepository.findAllByMunicipalityIdAndContractId(municipalityId, contractId));
	}

	/**
	 * Writes an attachment's binary content to the given response as a file download.
	 *
	 * <p>
	 * The attachment is looked up before anything is written, since status and headers cannot be changed once the
	 * response has been committed - a failure midway through would otherwise surface as a truncated 200 rather than as an
	 * error.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param attachmentId   the attachment id
	 * @param response       the response to write the content to
	 */
	@Transactional(readOnly = true)
	public void streamAttachment(final String municipalityId, final String contractId, final Long attachmentId, final HttpServletResponse response) {
		final var attachment = findAttachment(municipalityId, contractId, attachmentId);

		writeContent(response, attachment, attachmentId);
	}

	/**
	 * Updates the metadata of an existing attachment. The binary content is left untouched.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param attachmentId   the attachment id
	 * @param metadata       the metadata to apply
	 */
	public void updateAttachment(final String municipalityId, final String contractId, final Long attachmentId, final AttachmentMetadata metadata) {
		final var attachment = findAttachment(municipalityId, contractId, attachmentId);

		attachmentRepository.save(updateAttachmentEntity(attachment, metadata));
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

	private AttachmentEntity findAttachment(final String municipalityId, final String contractId, final Long attachmentId) {
		return attachmentRepository.findByMunicipalityIdAndContractIdAndId(municipalityId, contractId, attachmentId)
			.orElseThrow(() -> Problem.builder()
				.withStatus(NOT_FOUND)
				.withDetail(CONTRACT_ID_ATTACHMENT_ID_MUNICIPALITY_ID_NOT_FOUND.formatted(contractId, attachmentId, municipalityId))
				.build());
	}

	private byte[] readContent(final MultipartFile file) {
		try {
			return file.getBytes();
		} catch (final IOException e) {
			throw Problem.valueOf(BAD_REQUEST, "%s occurred when reading the uploaded file: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
		}
	}

	/**
	 * The mime type is client-supplied, so the content is always served as a download and browsers are told not to sniff
	 * the type - an uploaded HTML file must never be rendered in the API's origin.
	 *
	 * <p>
	 * This is the only place the lazy content association is read. Note that "streamed" here means the bytes are not
	 * buffered a second time on their way to the response - the JDBC driver materializes a BLOB in the heap when the row
	 * is read, so the whole file is in memory regardless. What the separate content table buys is that metadata reads
	 * never load it at all.
	 */
	private void writeContent(final HttpServletResponse response, final AttachmentEntity attachment, final Long attachmentId) {
		// Both the join column and the content column are NOT NULL, so this cannot happen through the mapping. It is
		// still checked, since dereferencing blindly would surface a hand-inserted or otherwise malformed row as an
		// unhandled NullPointerException rather than as a diagnosable error.
		final var blob = ofNullable(attachment.getAttachmentData())
			.map(AttachmentDataEntity::getFile)
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, "Attachment with id '%s' has no stored content".formatted(attachmentId)));

		response.addHeader(CONTENT_TYPE, isBlank(attachment.getMimeType()) ? APPLICATION_OCTET_STREAM_VALUE : attachment.getMimeType());
		// Built rather than concatenated: the filename is client-supplied, and a quote in it would otherwise break out of
		// the quoted-string, while a non-ASCII one (ordinary in Swedish filenames) would be mangled by the ISO-8859-1
		// encoding headers use. This emits the RFC 6266 pair - an escaped, transliterated 'filename' for old clients and
		// a percent-encoded 'filename*' carrying the real UTF-8 name.
		response.addHeader(CONTENT_DISPOSITION, ContentDisposition.attachment()
			.filename(attachment.getFilename(), UTF_8)
			.build()
			.toString());
		response.addHeader("X-Content-Type-Options", "nosniff");

		try (final var content = blob.getBinaryStream()) {
			response.setContentLengthLong(blob.length());
			content.transferTo(response.getOutputStream());
		} catch (final IOException | SQLException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "%s occurred when copying file with attachment id '%s' to response: %s".formatted(e.getClass().getSimpleName(), attachmentId, e.getMessage()));
		}
	}
}
