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
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractPaginatedResponse;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Diff;
import se.sundsvall.contract.service.ContractService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

@RestController
@Validated
@Tag(name = "Contracts", description = "Contract resources")
@RequestMapping(path = "/contracts/{municipalityId}")
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
class ContractResource {

	private final ContractService service;

	ContractResource(final ContractService service) {
		this.service = service;
	}

	@Operation(
		summary = "Create contract",
		responses = {
			@ApiResponse(
				responseCode = "201",
				description = "Successful operation",
				headers = @Header(
					name = LOCATION,
					description = "Location of the created resource.",
					schema = @Schema(type = "string")),
				useReturnTypeSchema = true)
		})
	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createContract(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@RequestBody @Valid final Contract contract) {

		final var contractId = service.createContract(municipalityId, contract);
		return created(fromPath("/contracts/{municipalityId}/{contractId}").buildAndExpand(municipalityId, contractId).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(
		summary = "Get a list of contracts",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok",
				useReturnTypeSchema = true)
		})
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	ResponseEntity<ContractPaginatedResponse> getContracts(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Valid @ParameterObject final ContractRequest request) {

		return ok(service.getContracts(municipalityId, request));
	}

	@Operation(
		summary = "Get a contract",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok",
				useReturnTypeSchema = true),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(schema = @Schema(implementation = Problem.class)))
		})
	@GetMapping(path = "/{contractId}", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<Contract> getContractById(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(description = "Contract id") @PathVariable("contractId") final String contractId,
		@Parameter(description = "Contract version") @Positive @RequestParam(name = "version", required = false) final Integer version) {

		return ok(service.getContract(municipalityId, contractId, version));
	}

	@Operation(
		summary = "Update a contract",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok"),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@PutMapping(path = "/{contractId}", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateContract(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(description = "Contract id") @PathVariable("contractId") final String contractId,
		@RequestBody @Valid final Contract contract) {

		service.updateContract(municipalityId, contractId, contract);
		return ok().build();
	}

	@Operation(
		summary = "Diff two versions of a contract",
		responses = {
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@PostMapping(path = "/{contractId}/diff", consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE)
	ResponseEntity<Diff> diffContract(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(description = "Contract id") @PathVariable("contractId") final String contractId,
		@Parameter(description = "Old version") @Positive @RequestParam(name = "oldVersion", required = false) final Integer oldVersion,
		@Parameter(description = "New version") @Positive @RequestParam(name = "newVersion", required = false) final Integer newVersion) {

		final var oldVersionNull = oldVersion == null;
		final var newVersionNull = newVersion == null;
		if (oldVersionNull ^ newVersionNull) {
			throw Problem.valueOf(Status.BAD_REQUEST, "Either both or none of 'oldVersion' and 'newVersion' must be set");
		}

		return ok(service.diffContract(municipalityId, contractId, oldVersion, newVersion));
	}

	@Operation(
		summary = "Delete a contract",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "No Content"),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
		})
	@DeleteMapping(path = "/{contractId}")
	ResponseEntity<Void> deleteContract(
		@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
		@Parameter(description = "Contract id") @PathVariable("contractId") final String contractId) {

		service.deleteContract(municipalityId, contractId);
		return noContent().build();
	}
}
