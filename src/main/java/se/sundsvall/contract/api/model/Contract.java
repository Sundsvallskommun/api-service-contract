package se.sundsvall.contract.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Contract")
public class Contract {

	@Schema(description = "Version for contract", examples = "1", accessMode = READ_ONLY)
	private int version;

	@Schema(description = "Contract id", examples = "2024-12345", accessMode = READ_ONLY)
	private String contractId;

	@Schema(description = "A description ", examples = "A simple description of the contract")
	private String description;

	@Schema(description = "External referenceId", examples = "123")
	private String externalReferenceId;

	private LeaseType leaseType;

	@Schema(description = "Municipality id for the contract", examples = "1984", accessMode = READ_ONLY)
	private String municipalityId;

	@Schema(description = "Object identity (from Lantmäteriet)", examples = "909a6a80-d1a4-90ec-e040-ed8f66444c3f", requiredMode = NOT_REQUIRED)
	private String objectIdentity;

	@NotNull
	private Status status;

	@NotNull
	private ContractType type;

	@Schema(description = "Type of leasehold")
	private Leasehold leasehold;

	@ArraySchema(schema = @Schema(description = "Metadata for all attachments", accessMode = READ_ONLY))
	private List<AttachmentMetadata> attachmentMetaData;

	@ArraySchema(schema = @Schema(description = "Additional terms for the contract"))
	private List<TermGroup> additionalTerms;

	@Schema(description = "Extra parameters")
	private List<ExtraParameterGroup> extraParameters;

	@ArraySchema(schema = @Schema(description = "Index terms for the contract"))
	private List<TermGroup> indexTerms;

	@ArraySchema(schema = @Schema(description = "Property designations", examples = "SUNDSVALL NORRMALM 1:1"))
	private List<String> propertyDesignations;

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	private List<Stakeholder> stakeholders;

	@Valid
	private Duration duration;

	@Valid
	private Extension extension;

	@Valid
	private Fees fees;

	@Schema(description = "Invoicing details")
	private Invoicing invoicing;

	@Schema(description = "Lease period start date", examples = "2020-01-01", format = "date")
	private LocalDate start;

	@Schema(description = "Lease period end date", examples = "2022-12-31", format = "date")
	private LocalDate end;

	@ArraySchema(schema = @Schema(description = "Termination periods"))
	private List<@Valid Notice> notices;

	@Schema(description = "Leased area (m2)", examples = "150")
	private Integer area;

	@Schema(description = "Whether the contract is signed by a witness")
	private boolean signedByWitness;

	@Schema(description = "Part(s) of property covered by the lease. Described by GeoJSON using polygon(s)", examples = """
		{
			"features": [
				{
					"geometry": {
						"coordinates": [
							[
								[
									1730072021484375,
									6238137830626575
								],
								[
									17297286987304688,
									6238050291927199
								],
								[
									17297801971435547,
									6237922958346664
								],
								[
									17301406860351562,
									62378194958300895
								],
								[
									17303810119628906,
									62379149998183046
								],
								[
									17303638458251953,
									6238066208244492
								],
								[
									1730072021484375,
									6238137830626575
								]
							]
						],
						"type": "Polygon"
					},
					"properties": {},
					"type": "Feature"
				}
			],
			"type": "FeatureCollection"
		}
		""")
	private FeatureCollection areaData;
}
