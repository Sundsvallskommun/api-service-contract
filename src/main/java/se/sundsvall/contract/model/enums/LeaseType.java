package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lease type", enumAsRef = true)
public enum LeaseType {
	LAND_LEASE_PUBLIC,
	LAND_LEASE_RESIDENTIAL,
	SITE_LEASE_COMMERCIAL,
	USUFRUCT_MOORING,
	USUFRUCT_HUNTING,
	USUFRUCT_FARMING,
	USUFRUCT_MISC,
	OBJECT_LEASE,
	LAND_LEASE_MISC,
	LEASEHOLD,
	OTHER_FEE
}
