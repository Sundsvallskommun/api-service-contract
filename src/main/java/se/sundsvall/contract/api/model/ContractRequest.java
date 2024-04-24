package se.sundsvall.contract.api.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.dept44.common.validators.annotation.ValidOrganizationNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingBase;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder(setterPrefix = "with")
@Schema(description = "Contract request")
public class ContractRequest extends AbstractParameterPagingBase {
	
	@Schema(description = "Contract id", example = "2024-12345")
	private String contractId;
	
	@Schema(description = "PartyId", example = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	@ValidUuid(nullable = true)
	private String partyId;
	
	@Schema(description = "Organization number", example = "771122-1234")
	@ValidOrganizationNumber(nullable = true)
	private String organizationNumber;
	
	@ArraySchema(schema = @Schema(description = "Property designations"))
	private List<String> propertyDesignations;
	
	@Schema(description = "External referenceId", example = "123")
	private String externalReferenceId;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Schema(description = "End date (format: yyyy-MM-dd)", example = "2023-01-01")
	private LocalDate end;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.LandLeaseType}
	 */
	@Schema(description = "Lease type", example = "LEASEHOLD")
	@OneOf(value = {"LEASEHOLD", "USUFRUCT", "SITELEASEHOLD"}, nullable = true)
	private String landLeaseType;
}
