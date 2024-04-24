package se.sundsvall.contract.model;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
@Schema(description = "Fees")
public class Fees {

    @Schema(description = "The currency of the lease fees", example = "SEK")
    private String currency;

    @Schema(description = "Yearly fee", example = "1000")
    private BigDecimal yearly;

    @Schema(description = "Monthly fee", example = "100")
    private BigDecimal monthly;

    @Schema(description = "Total fee", example = "1200")
    private BigDecimal total;

    @Schema(description = "Total fee as text", example = "One thousand two hundred")
    private String totalAsText;

    @Schema(description = "Index year", example = "2021")
    private Integer indexYear;

    @Schema(description = "Index number", example = "1")
    private Integer indexNumber;

    @Schema(description = "Additional information")
    private List<String> additionalInformation;
}
