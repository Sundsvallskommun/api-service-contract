package se.sundsvall.contract.api.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.dept44.common.validators.annotation.ValidOrganizationNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

public record ContractRequest(

	@Schema(description = "Contract id", example = "2024-12345")
	String contractId,

	@ValidUuid(nullable = true)
	@Schema(description = "Identifier for private person", example = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	String partyId,

	@ValidOrganizationNumber(nullable = true)
	@Schema(description = "Organization number", example = "771122-1234")
	String organizationNumber,

	@ArraySchema(schema = @Schema(description = "Property designations"))
	List<String> propertyDesignations,

	@Schema(description = "External referenceId", example = "123")
	String externalReferenceId,

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Schema(description = "End date (format: yyyy-MM-dd)", example = "2023-01-01")
	LocalDate end,

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.LandLeaseType}
	 */
	@OneOf(value = {"LEASEHOLD", "USUFRUCT", "SITELEASEHOLD"}, nullable = true)
	@Schema(description = "Lease type", example = "LEASEHOLD")
	String landLeaseType) { }
