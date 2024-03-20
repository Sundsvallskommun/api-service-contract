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

import java.util.List;

import jakarta.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.service.ContractService;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "Contracts", description = "Contract resources")
@RequestMapping(path = "/contracts/{municipalityId}")
@ApiResponse(
	responseCode = "400",
	description = "Bad Request",
	content = @Content(schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
@ApiResponse(
	responseCode = "500",
	description = "Internal Server Error",
	content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "502",
	description = "Bad Gateway",
	content = @Content(schema = @Schema(implementation = Problem.class)))
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
		}
	)
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_PROBLEM_JSON_VALUE)
	ResponseEntity<Void> createContract(
			@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
			@RequestBody @Valid final Contract contract) {
		final var id = service.createContract(municipalityId, contract);

		return created(fromPath("/contracts/{municipalityId}/{id}").buildAndExpand(municipalityId, id).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(
		summary = "Get a list of contracts",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok",
				useReturnTypeSchema = true),
			@ApiResponse(
				responseCode = "404",
				description = "Not Found",
				content = @Content(schema = @Schema(implementation = Problem.class)))
		}
	)
	@GetMapping(produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	ResponseEntity<List<Contract>> getContracts(
			@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
			@ParameterObject final ContractRequest request) {
		var landLeaseContractList = service.getContracts(municipalityId, request);

		return ok(landLeaseContractList);
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
	@GetMapping(path = "/{contractId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	ResponseEntity<Contract> getContractById(
			@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
			@Parameter(description = "Contract id") @PathVariable("contractId") final String contractId) {
		return ok(service.getContract(municipalityId, contractId));
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
				content = @Content(schema = @Schema(implementation = Problem.class)))
		})
	@PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	ResponseEntity<Void> updateContract(
			@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
			@Parameter(description = "Contract id") @PathVariable("id") final String id,
			@RequestBody @Valid final Contract contract) {
		service.updateContract(municipalityId, id, contract);

		return ok().build();
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
				content = @Content(schema = @Schema(implementation = Problem.class)))
		})
	@DeleteMapping(path = "/{id}", produces = APPLICATION_PROBLEM_JSON_VALUE)
	ResponseEntity<Void> deleteContract(
			@Parameter(name = "municipalityId", description = "Municipality id") @ValidMunicipalityId @PathVariable("municipalityId") final String municipalityId,
			@Parameter(description = "Contract id") @PathVariable("id") final String id) {
		service.deleteContract(municipalityId, id);

		return noContent().build();
	}
}
