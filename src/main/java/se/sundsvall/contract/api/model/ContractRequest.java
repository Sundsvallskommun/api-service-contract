package se.sundsvall.contract.api.model;

import jakarta.validation.constraints.Pattern;

import se.sundsvall.contract.api.model.enums.LandLeaseType;

import io.swagger.v3.oas.annotations.media.Schema;

public record ContractRequest(
	@Schema(description = "Identifierare för privatperson") String personId,
	@Schema(description = "Organisationsnummer") String organizationNumber,
	@Schema(description = "Fastighetsbeteckning") String propertyDesignation,
	@Schema(description = "Referensid") String externalReferenceId,
	@Schema(description = "Slutdatum (format: yyyy-MM-dd)") @Pattern(regexp = "(\\d{4})-(\\d{2})-(\\d{2})", message = "Expected date format is yyyy-MM-dd.") String end,
	@Schema(description = "Arrendetyp") LandLeaseType landLeaseType) {

}
