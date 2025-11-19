package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "StakeholderType", enumAsRef = true)
public enum StakeholderType {
	PERSON,
	COMPANY,
	ASSOCIATION,
	MUNICIPALITY,
	REGION,
	OTHER
}
