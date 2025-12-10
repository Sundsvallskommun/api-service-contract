package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.LeaseholdType;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Leasehold")
public class Leasehold {

	private LeaseholdType purpose;

	@Schema(description = "description ", examples = "A simple description of the leasehold")
	private String description;

	@ArraySchema(schema = @Schema(description = "Additional information", examples = "Some additional information"))
	private List<String> additionalInformation;
}
