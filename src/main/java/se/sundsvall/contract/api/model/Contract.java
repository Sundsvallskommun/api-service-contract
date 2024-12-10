package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
import se.sundsvall.dept44.common.validators.annotation.OneOf;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Contract")
public class Contract {

	@Schema(description = "Version for contract", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private int version;

	@Schema(description = "Contract id", example = "2024-12345", accessMode = Schema.AccessMode.READ_ONLY)
	private String contractId;

	@Schema(description = "A description ", example = "A simple description of the contract")
	private String description;

	@Schema(description = "External referenceId", example = "123")
	private String externalReferenceId;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.LandLeaseType}
	 */
	@Schema(description = "Type of lease", example = "LEASEHOLD")
	@OneOf(value = {
		"LEASEHOLD", "USUFRUCT", "SITELEASEHOLD"
	}, nullable = true)
	private String landLeaseType;

	@Schema(description = "Municipality id for the contract", example = "1984", accessMode = Schema.AccessMode.READ_ONLY)
	private String municipalityId;

	@Schema(description = "Object identity (from Lantmäteriet)", example = "909a6a80-d1a4-90ec-e040-ed8f66444c3f", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String objectIdentity;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.Status}
	 */
	@Schema(description = "Status for contract", example = "ACTIVE")
	@OneOf({
		"ACTIVE", "DRAFT", "TERMINATED"
	})
	private String status;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.ContractType}
	 */
	@Schema(description = "Contract type.", example = "LAND_LEASE")
	@OneOf(value = {
		"APARTMENT_LEASE", "LAND_LEASE", "PURCHASE_AGREEMENT"
	})
	private String type;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.UsufructType}
	 */
	@Schema(description = "Type of right of use", example = "HUNTING")
	@OneOf(value = {
		"HUNTING", "FISHING", "MAINTENANCE", "OTHER"
	}, nullable = true)
	private String usufructType;

	@Schema(description = "Type of leasehold")
	private Leasehold leasehold;

	@ArraySchema(schema = @Schema(description = "Metadata for all attachments", accessMode = Schema.AccessMode.READ_ONLY))
	private List<AttachmentMetaData> attachmentMetaData;

	@ArraySchema(schema = @Schema(description = "Additional terms for the contract"))
	private List<TermGroup> additionalTerms;

	@Schema(description = "Extra parameters")
	private List<ExtraParameterGroup> extraParameters;

	@ArraySchema(schema = @Schema(description = "Index terms for the contract"))
	private List<TermGroup> indexTerms;

	@ArraySchema(schema = @Schema(description = "Property designations", example = "SUNDSVALL NORRMALM 1:1"))
	private List<String> propertyDesignations;

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	private List<Stakeholder> stakeholders;

	@Schema(description = "The duration of the lease in years", example = "9")
	private Integer leaseDuration;

	@Schema(description = "Fees")
	private Fees fees;

	@Schema(description = "Invoicing details")
	private Invoicing invoicing;

	@Schema(description = "Lease period start date", example = "2020-01-01", format = "date")
	private LocalDate start;

	@Schema(description = "Lease period end date", example = "2022-12-31", format = "date")
	private LocalDate end;

	@Schema(description = "Marker for whether an agreement should be extended automatically or not", example = "true", defaultValue = "true")
	private Boolean autoExtend;

	@Schema(description = "Extension period in days", example = "30")
	private Integer leaseExtension;

	@Schema(description = "Termination period in days", example = "30")
	private Integer periodOfNotice;

	@Schema(description = "Leased area (m2)", example = "150")
	private Integer area;

	@Schema(description = "Whether the contract is signed by a witness")
	private boolean signedByWitness;

	@Schema(description = "Part(s) of property covered by the lease. Described by GeoJSON using polygon(s)", example = """
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
