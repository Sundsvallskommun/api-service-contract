package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lease type", enumAsRef = true)
public enum LeaseType {
	LAND_LEASE_RESIDENTIAL,
	SITE_LEASE_COMMERCIAL,
	USUFRUCT_HUNTING,
	USUFRUCT_FARMING,
	USUFRUCT_MISC,
	LAND_LEASE_MISC,
	OTHER_FEE
}
