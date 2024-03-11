package se.sundsvall.contract.api.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	property = "type"
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = LandLeaseContract.class, name = "landLease"),
})
@Data
@SuperBuilder(setterPrefix = "with")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Arrendeavtal")
public abstract class Contract {

	@Schema(description = "Contract id", example = "2024-12345", accessMode = Schema.AccessMode.READ_ONLY)
	private String id;

	@Schema(description = "Version for contract", example = "1")
	private Integer version;

	/**
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.Status}
	 */
	@OneOf({"ACTIVE", "DRAFT", "TERMINATED"})
	@Schema(description = "Status for contract", example = "ACTIVE")
	private String status;

	@Schema(description = "Municipality id for the contract", example = "1984", accessMode = Schema.AccessMode.READ_ONLY)
	private String municipalityId;

	@Schema(description = "Eventual caseId for the contract", example = "100")
	private Long caseId;

	@ArraySchema(schema = @Schema(description = "Index terms for the contract"))
	private List<TermGroup> indexTerms;

	@Schema(description = "A description ", example = "A simple description of the contract")
	private String description;

	@ArraySchema(schema = @Schema(description = "Additional terms for the contract"))
	private List<TermGroup> additionalTerms;

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	private List<Stakeholder> stakeholders;

	@ArraySchema(schema = @Schema(description = "List of attachments"))
	private List<Attachment> attachments;

	@Schema(description = "Whether the contract is signed by a witness")
	private boolean signedByWitness;

	@Schema(description = "Extra parameters", example = "{\"key\": \"value\"}")
	private Map<String, String> extraParameters;
}
