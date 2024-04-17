package se.sundsvall.contract.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.EXISTING_PROPERTY,
	property = "type"
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = LandLeaseContract.class, name = "LAND_LEASE")
})
@Data
@EqualsAndHashCode
@SuperBuilder(setterPrefix = "with")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Contract")
public abstract class Contract {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.ContractType}
	 */
	@OneOf(value = {"LAND_LEASE"}, nullable = true)
	@Schema(description = "Contract type. Possible values: LAND_LEASE", example = "LAND_LEASE")
	private String type;

	@Schema(description = "Contract id", example = "2024-12345", accessMode = Schema.AccessMode.READ_ONLY)
	private String contractId;

	@Schema(description = "Version for contract", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private int version;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.Status}
	 */
	@OneOf({"ACTIVE", "DRAFT", "TERMINATED"})
	@Schema(description = "Status for contract", example = "ACTIVE")
	private String status;

	@Schema(description = "Municipality id for the contract", example = "1984", accessMode = Schema.AccessMode.READ_ONLY)
	private String municipalityId;

	@ArraySchema(schema = @Schema(description = "Index terms for the contract"))
	private List<TermGroup> indexTerms;

	@Schema(description = "A description ", example = "A simple description of the contract")
	private String description;

	@ArraySchema(schema = @Schema(description = "Additional terms for the contract"))
	private List<TermGroup> additionalTerms;

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	private List<Stakeholder> stakeholders;

	@ArraySchema(schema = @Schema(description = "Metadata for all attachments", accessMode = Schema.AccessMode.READ_ONLY))
	private List<AttachmentMetaData> attachmentMetaData;

	@Schema(description = "Whether the contract is signed by a witness")
	private boolean signedByWitness;

	@Schema(description = "Extra parameters")
	private List<ExtraParameterGroup> extraParameters;
}
