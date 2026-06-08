package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import se.sundsvall.contract.model.enums.TimeUnit;

/**
 * Partial lease extension details for PATCH (JSON Merge Patch semantics): omitted leaves the sub-field unchanged, null
 * clears it, a value updates it. That leaseExtension and unit are present when autoExtend is true is validated on the
 * merged contract.
 */
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial lease extension details for PATCH")
public class PatchExtension {

	@Schema(description = "Marker for whether an agreement should be extended automatically or not", examples = "true")
	@Builder.Default
	private JsonNullable<Boolean> autoExtend = JsonNullable.undefined();

	@Min(0)
	@Schema(description = "The lease extension value", examples = "2")
	@Builder.Default
	private JsonNullable<Integer> leaseExtension = JsonNullable.undefined();

	@Schema(description = "The unit of the extension value", examples = "MONTHS")
	@Builder.Default
	private JsonNullable<TimeUnit> unit = JsonNullable.undefined();
}
