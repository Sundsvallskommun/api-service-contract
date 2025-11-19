package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Notice")
public class Notice {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.Party}
	 */
	@Schema(description = "The party type", example = "LESSOR")
	@OneOf(value = {
		"LESSOR", "LESSEE"
	})
	private String party;

	@Schema(description = "The period of notice", example = "3")
	@NotNull
	private Integer periodOfNotice;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.TimeUnit}
	 */
	@Schema(description = "The unit of the periodOfNotice value", example = "MONTHS")
	@OneOf(value = {
		"DAYS", "MONTHS", "YEARS"
	})
	private String unit;
}
