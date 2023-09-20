package se.sundsvall.contract.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.util.List;

import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;

import se.sundsvall.contract.api.model.ContractHolder;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.service.ContractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "Contracts", description = "Contracts")
@RequestMapping(path = "/contracts",
	produces = APPLICATION_PROBLEM_JSON_VALUE
)
@ApiResponse(
	responseCode = "400",
	description = "Bad Request",
	content = @Content(schema = @Schema(implementation = Problem.class))
)
@ApiResponse(
	responseCode = "500",
	description = "Internal Server Error",
	content = @Content(schema = @Schema(implementation = Problem.class))
)
@ApiResponse(
	responseCode = "502",
	description = "Bad Gateway",
	content = @Content(schema = @Schema(implementation = Problem.class))
)
public class ContractResource {

	private final ContractService service;

	public ContractResource(final ContractService service) {this.service = service;}

	@Operation(
		summary = "Create contract",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "No content"
			)
		}
	)
	@PostMapping(produces = ALL_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> postLandLeaseContract(final UriComponentsBuilder uriComponentsBuilder,
		@RequestBody @Valid final LandLeaseContract contract) {

		return ResponseEntity
			.created(uriComponentsBuilder.build(service.createContract(contract)))
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@Operation(
		summary = "Get a list of contracts",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok",
				useReturnTypeSchema = true
			)

		}
	)
	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LandLeaseContract>> getContracts(@ParameterObject final ContractRequest request) {

		final List<LandLeaseContract> landLeaseContractList = service.getContracts(request);
		return ResponseEntity.ok(landLeaseContractList);
	}


	@Operation(
		summary = "Get a contract",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Ok",
				useReturnTypeSchema = true
			)

		}
	)
	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<LandLeaseContract> getContractById(@Parameter(description = "Kontraktets id") @PathVariable("id") final Long id) {

		return ResponseEntity.ok(service.getContract(id));
	}

	@Operation(
		summary = "Update contract",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "No content"
			)
		}
	)
	@PatchMapping(path = "/{id}", produces = ALL_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> patchContract(@PathVariable("id") final Long id, @Valid final ContractHolder contractHolder) {

		service.updateContract(id, contractHolder);
		return ResponseEntity.noContent()
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

}
