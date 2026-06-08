package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Partial fee details for PATCH. Each field follows JSON Merge Patch semantics: omitted leaves the sub-field unchanged,
 * null clears it, a value updates it. The fee index trio consistency is validated on the merged contract.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Partial fee details for PATCH")
public class PatchFees {

	@Schema(description = "The currency of the lease fees", examples = "SEK")
	@Builder.Default
	private JsonNullable<String> currency = JsonNullable.undefined();

	@Schema(description = "Yearly fee", examples = "1000.5")
	@Builder.Default
	private JsonNullable<BigDecimal> yearly = JsonNullable.undefined();

	@Schema(description = "Monthly fee", examples = "100.5")
	@Builder.Default
	private JsonNullable<BigDecimal> monthly = JsonNullable.undefined();

	@Schema(description = "Total fee", examples = "1200.5")
	@Builder.Default
	private JsonNullable<BigDecimal> total = JsonNullable.undefined();

	@Schema(description = "Total fee as text", examples = "One thousand two hundred")
	@Builder.Default
	private JsonNullable<String> totalAsText = JsonNullable.undefined();

	@Schema(description = "Index type", example = "KPI 80")
	@Builder.Default
	private JsonNullable<String> indexType = JsonNullable.undefined();

	@Schema(description = "Index year", examples = "2021")
	@Builder.Default
	private JsonNullable<Integer> indexYear = JsonNullable.undefined();

	@Schema(description = "Index number", examples = "1.00")
	@Builder.Default
	private JsonNullable<BigDecimal> indexNumber = JsonNullable.undefined();

	@DecimalMin(value = "0.0")
	@DecimalMax(value = "1.0")
	@Schema(description = "Specifies what proportion of the consumer price index should be used for invoicing.", examples = "0.5")
	@Builder.Default
	private JsonNullable<BigDecimal> indexationRate = JsonNullable.undefined();

	@Schema(description = "Additional information. Each entry must be non-blank and between 1 and 30 characters (used as invoice row descriptions).")
	@Builder.Default
	private JsonNullable<List<@NotBlank @Size(min = 1, max = 30) String>> additionalInformation = JsonNullable.undefined();
}
