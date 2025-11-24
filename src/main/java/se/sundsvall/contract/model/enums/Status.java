package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status", enumAsRef = true)
public enum Status {
	ACTIVE,
	DRAFT,
	TERMINATED
}
