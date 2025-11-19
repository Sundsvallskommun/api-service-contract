package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "TimeUnit", enumAsRef = true)
public enum TimeUnit {
	DAYS,
	MONTHS,
	YEARS
}
