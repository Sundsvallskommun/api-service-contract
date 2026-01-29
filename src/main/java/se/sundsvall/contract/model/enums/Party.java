package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Party", enumAsRef = true)
public enum Party {
	LESSOR,
	LESSEE,
	ALL
}
