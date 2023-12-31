package se.sundsvall.contract.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import se.sundsvall.contract.api.model.enums.Status;

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

	@Schema(description = "Version for contract", example = "1")
	private Integer version;

	@Schema(description = "Status for contract", example = "ACTIVE")
	private Status status;

	@Schema(description = "Eventual caseId for the contract", example = "100")
	private Long caseId;

	@Schema(description = "Index terms for the contract", example = "?")
	private String indexTerms;

	@Schema(description = "A description ", example = "A simple description of the contract")
	private String description;

	@Schema(description = "Additional terms for the contract", example = "The fee must be paid in advance")
	private String additionalTerms;

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	private List<Stakeholder> stakeholders;

	@ArraySchema(schema = @Schema(description = "List of attachments"))
	private List<Attachment> attachments;

}
