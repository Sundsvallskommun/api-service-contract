package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.TimeUnit;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Duration")
public class Duration {

	@Schema(description = "The lease duration value", example = "9")
	@NotNull
	private Integer leaseDuration;

	@Schema(description = "The unit of the duration value", example = "MONTHS")
	@NotNull
	private TimeUnit unit;
}
