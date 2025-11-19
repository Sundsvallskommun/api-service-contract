package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Duration")
public class Duration {

	@Schema(description = "The lease duration value", example = "9")
	private Integer leaseDuration;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.TimeUnit}
	 */
	@Schema(description = "The unit of the duration value", example = "MONTHS")
	@OneOf(value = {
		"DAYS", "MONTHS", "YEARS"
	})
	private String unit;
}
