package se.sundsvall.contract.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.service.AttachmentService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RestController
@Validated
@Tag(name = "Contract attachments", description = "Contract attachment resources")
@RequestMapping(path = "/{municipalityId}/contracts/{contractId}/attachments")
@ApiResponse(
	responseCode = "400",
	description = "Bad Request",
	content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
@ApiResponse(
	responseCode = "404",
	description = "Not Found",
	content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "500",
	description = "Internal Server Error",
	content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "502",
	description = "Bad Gateway",
	content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class ContractAttachmentResource {

	private final AttachmentService attachmentService;

	ContractAttachmentResource(final AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@Operation(
		summary = "Get the metadata of a contract's attachments",
		description = "Fetches the metadata of every attachment belonging to the contract. The binary content is fetched separately, one attachment at a time.",
		responses = @ApiResponse(
			responseCode = "200",
			description = "Ok",
			useReturnTypeSchema = true))
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	ResponseEntity<List<AttachmentMetadata>> getAttachments(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable final String contractId) {

		return ok(attachmentService.getAttachments(municipalityId, contractId));
	}

	@Operation(
		summary = "Get a streamed contract attachment",
		description = "Fetches the binary content of the attachment that matches the provided id in a streamed manner.",
		responses = @ApiResponse(
			responseCode = "200",
			description = "Ok",
			useReturnTypeSchema = true))
	@GetMapping(path = "/{attachmentId}", produces = ALL_VALUE)
	void getAttachmentById(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable final String contractId,
		@Parameter(name = "attachmentId", description = "Attachment id") @PathVariable final Long attachmentId,
		final HttpServletResponse response) {

		attachmentService.streamAttachment(municipalityId, contractId, attachmentId, response);
	}

	@Operation(
		summary = "Create a contract attachment",
		description = "The metadata is supplied as a JSON part named 'attachment' and the binary content as a file part named 'file'.",
		responses = @ApiResponse(
			responseCode = "201",
			description = "Successful operation",
			headers = @Header(
				name = LOCATION,
				description = "Location of the uploaded attachment.",
				schema = @Schema(type = "string")),
			useReturnTypeSchema = true))
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = ALL_VALUE)
	ResponseEntity<Void> createAttachment(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable final String contractId,
		@Valid @RequestPart("attachment") final AttachmentMetadata attachment,
		@NotNull @RequestPart("file") final MultipartFile file) {

		if (file.isEmpty()) {
			throw Problem.valueOf(BAD_REQUEST, "The 'file' part must not be empty");
		}

		final var id = attachmentService.createAttachment(municipalityId, contractId, attachment, file);
		return created(fromPath("/{municipalityId}/contracts/{contractId}/attachments/{attachmentId}").buildAndExpand(municipalityId, contractId, id).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(
		summary = "Update the metadata of a contract attachment",
		description = "The binary content cannot be updated; replace it by deleting and recreating the attachment.",
		responses = @ApiResponse(
			responseCode = "204",
			description = "No content",
			useReturnTypeSchema = true))
	@PatchMapping(path = "/{attachmentId}", consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	ResponseEntity<Void> patchAttachment(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable final String contractId,
		@Parameter(name = "attachmentId", description = "Attachment id") @PathVariable final Long attachmentId,
		@RequestBody final AttachmentMetadata attachment) {

		attachmentService.updateAttachment(municipalityId, contractId, attachmentId, attachment);
		return noContent()
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(
		summary = "Delete a contract attachment",
		responses = @ApiResponse(
			responseCode = "204",
			description = "No content"))
	@DeleteMapping(path = "/{attachmentId}", produces = ALL_VALUE)
	ResponseEntity<Void> deleteAttachment(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable final String contractId,
		@Parameter(name = "attachmentId", description = "Attachment id") @PathVariable final Long attachmentId) {

		attachmentService.deleteAttachment(municipalityId, contractId, attachmentId);
		return noContent()
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}
}
