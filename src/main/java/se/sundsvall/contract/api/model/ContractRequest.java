package se.sundsvall.contract.api.model;

import java.util.List;

import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record ContractRequest(
	@Schema(description = "Identifier for private person", example = "40f14de9-815d-44a5-a34d-b1d38b628e07") String personId,
	@Schema(description = "Organization number", example = "771122-1234") String organizationNumber,
	@ArraySchema(schema = @Schema(description = "Property designations")) List<String> propertyDesignations,
	@Schema(description = "External referenceId", example = "123") String externalReferenceId,
	@Schema(description = "End date (format: yyyy-MM-dd)", example = "2023-01-01") @Pattern(regexp = "(\\d{4})-(\\d{2})-(\\d{2})", message = "Expected date format is yyyy-MM-dd.") String end,
	//LandleaseType is backed by LandLeaseType enum
	@OneOf({"LEASEHOLD", "USUFRUCT", "SITELEASEHOLD"})
	@Schema(description = "Lease type", example = "LEASEHOLD") @NotEmpty(message = "landLeaseType must not be empty") String landLeaseType) {

}
