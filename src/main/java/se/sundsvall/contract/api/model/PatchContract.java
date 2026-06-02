package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geojson.FeatureCollection;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Status;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial contract payload used for PATCH. Only the fields present in the payload are applied to the existing contract.")
public class PatchContract {

	@Schema(description = "A description of the contract", examples = "A simple description of the contract")
	private String description;

	@Schema(description = "External referenceId", examples = "123")
	private String externalReferenceId;

	@Schema(description = "Type of lease")
	private LeaseType leaseType;

	@Schema(description = "Object identity (from Lantmäteriet)", examples = "909a6a80-d1a4-90ec-e040-ed8f66444c3f")
	private String objectIdentity;

	@Schema(description = "Contract status")
	private Status status;

	@Schema(description = "Contract type")
	private ContractType type;

	@Schema(description = "Type of leasehold")
	private Leasehold leasehold;

	@ArraySchema(schema = @Schema(description = "Additional terms for the contract"))
	private List<TermGroup> additionalTerms;

	@Schema(description = "Extra parameters")
	private List<ExtraParameterGroup> extraParameters;

	@ArraySchema(schema = @Schema(description = "Index terms for the contract"))
	private List<TermGroup> indexTerms;

	@ArraySchema(schema = @Schema(description = "Property designations"))
	private List<PropertyDesignation> propertyDesignations;

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	private List<Stakeholder> stakeholders;

	@Schema(description = "Lease extension")
	private Extension extension;

	@Valid
	@Schema(description = "Fee details")
	private Fees fees;

	@Schema(description = "Invoicing details")
	private Invoicing invoicing;

	@Schema(description = "Start date of the contract", examples = "2020-01-01", format = "date")
	private LocalDate startDate;

	@Schema(description = "End date of the contract. Set when the contract is terminated", examples = "2022-12-31", format = "date")
	private LocalDate endDate;

	@Schema(description = "Notice details")
	private Notice notice;

	@Schema(description = "Current contract period")
	private Period currentPeriod;

	@Schema(description = "Leased area (m2)", examples = "150")
	private Integer area;

	@Schema(description = "Whether the contract is signed by a witness")
	private Boolean signedByWitness;

	@Schema(description = "Part(s) of property covered by the lease. Described by GeoJSON using polygon(s)")
	private FeatureCollection areaData;
}
