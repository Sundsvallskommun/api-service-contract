package se.sundsvall.contract.api.model;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.dept44.common.validators.annotation.ValidOrganizationNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.models.api.paging.AbstractParameterPagingBase;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder(setterPrefix = "with")
@Schema(description = "Contract request")
public class ContractRequest extends AbstractParameterPagingBase {

	@Schema(description = "Contract id", examples = "2024-12345")
	private String contractId;

	@Schema(description = "PartyId", examples = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	@ValidUuid(nullable = true)
	private String partyId;

	@Schema(description = "Organization number", examples = "771122-1234")
	@ValidOrganizationNumber(nullable = true)
	private String organizationNumber;

	@ArraySchema(schema = @Schema(description = "Property designations"))
	private List<String> propertyDesignations;

	@Schema(description = "External referenceId", examples = "123")
	private String externalReferenceId;

	@DateTimeFormat(iso = DATE)
	@Schema(description = "End date (format: yyyy-MM-dd)", examples = "2023-01-01")
	private LocalDate end;

	@Schema(description = "Lease type", examples = "LEASEHOLD")
	private LeaseType leaseType;

	@Schema(description = "Specific term to search for", examples = "term", types = {
		"string", "null"
	})
	@Size(min = 2, message = "Term must be at least 2 characters long if provided")
	private String term;
}
