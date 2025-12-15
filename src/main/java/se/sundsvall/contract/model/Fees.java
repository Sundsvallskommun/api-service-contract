package se.sundsvall.contract.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Fees")
public class Fees {

	@Schema(description = "The currency of the lease fees", examples = "SEK")
	private String currency;

	@Schema(description = "Yearly fee", examples = "1000.5")
	private BigDecimal yearly;

	@Schema(description = "Monthly fee", examples = "100.5")
	private BigDecimal monthly;

	@Schema(description = "Total fee", examples = "1200.5")
	private BigDecimal total;

	@Schema(description = "Total fee as text", examples = "One thousand two hundred")
	private String totalAsText;

	@Schema(description = "Index type", example = "KPI 80")
	private String indexType;

	@Schema(description = "Index year", examples = "2021")
	private Integer indexYear;

	@Schema(description = "Index number", examples = "1")
	private Integer indexNumber;

	@Schema(description = "Specifies what proportion of the consumer price index should be used for invoicing.", examples = "0.5")
	@DecimalMin(value = "0.0")
	@DecimalMax(value = "1.0")
	private BigDecimal indexationRate;

	@Schema(description = "Additional information")
	private List<String> additionalInformation;
}
