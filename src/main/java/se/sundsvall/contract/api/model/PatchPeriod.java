package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Partial contract period for PATCH (JSON Merge Patch semantics): omitted leaves the sub-field unchanged, null clears
 * it, a value updates it.
 */
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial contract period for PATCH")
public class PatchPeriod {

	@Schema(description = "Start date of the period", examples = "2020-01-01", format = "date")
	@Builder.Default
	private JsonNullable<LocalDate> startDate = JsonNullable.undefined();

	@Schema(description = "End date of the period", examples = "2020-12-31", format = "date")
	@Builder.Default
	private JsonNullable<LocalDate> endDate = JsonNullable.undefined();
}
