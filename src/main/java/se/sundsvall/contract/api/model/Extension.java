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
@Schema(description = "Extension")
public class Extension {

	@Schema(description = "Marker for whether an agreement should be extended automatically or not", example = "true", defaultValue = "true")
	private Boolean autoExtend;

	@Schema(description = "The lease extension value", example = "2")
	private Integer leaseExtension;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.TimeUnit}
	 */
	@Schema(description = "The unit of the extension value", example = "MONTHS")
	@OneOf(value = {
		"DAYS", "MONTHS", "YEARS"
	})
	private String unit;
}
