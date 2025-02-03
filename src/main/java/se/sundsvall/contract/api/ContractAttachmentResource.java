package se.sundsvall.contract.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.service.AttachmentService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

@RestController
@Validated
@Tag(name = "Contract attachments", description = "Contract attachment resources")
@RequestMapping(path = "/contracts/{municipalityId}/{contractId}/attachments")
@ApiResponse(
	responseCode = "400",
	description = "Bad Request",
	content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	})))
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
		summary = "Get a contracts attachment(s) and its metadata",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok",
				useReturnTypeSchema = true),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@GetMapping(path = "/{attachmentId}", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> getAttachmentById(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable("contractId") final String contractId,
		@Parameter(name = "attachmentId", description = "Attachment id") @PathVariable("attachmentId") final Long attachmentId) {
		return ok(attachmentService.getAttachment(municipalityId, contractId, attachmentId));
	}

	@Operation(
		summary = "Create a contract attachment",
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "Successful operation",
				headers = @Header(
					name = LOCATION,
					description = "Location of the uploaded attachment.",
					schema = @Schema(type = "string")),
				useReturnTypeSchema = true),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createAttachment(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable("contractId") final String contractId,
		@RequestBody @Valid final Attachment attachment) {
		final var id = attachmentService.createAttachment(municipalityId, contractId, attachment);

		return created(fromPath("/contracts/{municipalityId}/{contractId}/attachments/{attachmentId}").buildAndExpand(municipalityId, contractId, id).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(
		summary = "Update a contract attachment",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Successful operation",
				useReturnTypeSchema = true),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@PutMapping(path = "/{attachmentId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ResponseEntity<AttachmentMetaData> updateAttachment(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable("contractId") final String contractId,
		@Parameter(name = "attachmentId", description = "Attachment id") @PathVariable("attachmentId") final Long attachmentId,
		@Valid @RequestBody final Attachment attachment) {
		final var result = attachmentService.updateAttachment(municipalityId, contractId, attachmentId, attachment);

		return ok(result);
	}

	@Operation(
		summary = "Delete a contract attachment",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "No content")
		})
	@DeleteMapping(path = "/{attachmentId}")
	ResponseEntity<Void> deleteAttachment(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(name = "contractId", description = "Contract id") @PathVariable("contractId") final String contractId,
		@Parameter(name = "attachmentId", description = "Attachment id") @PathVariable("attachmentId") final Long attachmentId) {

		attachmentService.deleteAttachment(municipalityId, contractId, attachmentId);

		return noContent().build();
	}
}
