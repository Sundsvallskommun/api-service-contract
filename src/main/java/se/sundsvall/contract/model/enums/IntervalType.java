package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Interval type", enumAsRef = true)
public enum IntervalType {
	YEARLY,
	HALF_YEARLY,
	QUARTERLY,
	MONTHLY
}
