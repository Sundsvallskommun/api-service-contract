package se.sundsvall.contract.api.model;

import jakarta.validation.constraints.Pattern;

import se.sundsvall.contract.api.model.enums.LandLeaseType;

import io.swagger.v3.oas.annotations.media.Schema;

public record ContractRequest(
	@Schema(description = "Identifier for private person", example = "40f14de9-815d-44a5-a34d-b1d38b628e07") String personId,
	@Schema(description = "Organization number", example = "771122-1234") String organizationNumber,
	@Schema(description = "Property designation") String propertyDesignation,
	@Schema(description = "External referenceId", example = "123") String externalReferenceId,
	@Schema(description = "End date (format: yyyy-MM-dd)", example = "2023-01-01") @Pattern(regexp = "(\\d{4})-(\\d{2})-(\\d{2})", message = "Expected date format is yyyy-MM-dd.") String end,
	@Schema(description = "Lease type", example = "LEASEHOLD") LandLeaseType landLeaseType) {

}
