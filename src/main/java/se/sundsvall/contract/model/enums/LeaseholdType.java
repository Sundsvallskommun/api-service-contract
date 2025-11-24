package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Leasehold type", enumAsRef = true)
public enum LeaseholdType {
	AGRICULTURE,
	APARTMENT,
	BOATING_PLACE,
	BUILDING,
	DEPOT,
	DWELLING,
	LAND_COMPLEMENT,
	LINEUP,
	OTHER,
	PARKING,
	RECYCLING_STATION,
	ROAD,
	SIGNBOARD,
	SNOW_DUMP,
	SPORTS_PURPOSE,
	SURFACE_HEAT,
	TRAIL
}
