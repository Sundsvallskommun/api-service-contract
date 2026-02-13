package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Contract period")
public class Period {

	@Schema(description = "Period start date", examples = "2021-07-01")
	private LocalDate startDate;

	@Schema(description = "Period end date", examples = "2030-06-30")
	private LocalDate endDate;
}
