package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import se.sundsvall.contract.model.enums.LeaseholdType;

/**
 * Partial leasehold details for PATCH (JSON Merge Patch semantics): omitted leaves the sub-field unchanged, null clears
 * it, a value updates it.
 */
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial leasehold details for PATCH")
public class PatchLeasehold {

	@Schema(description = "Type of leasehold")
	@Builder.Default
	private JsonNullable<LeaseholdType> purpose = JsonNullable.undefined();

	@Schema(description = "Additional description of the leasehold type")
	@Builder.Default
	private JsonNullable<String> description = JsonNullable.undefined();

	@Schema(description = "Additional information")
	@Builder.Default
	private JsonNullable<List<String>> additionalInformation = JsonNullable.undefined();
}
