package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.LeaseholdType.AGRICULTURE;
import static se.sundsvall.contract.model.enums.LeaseholdType.APARTMENT;
import static se.sundsvall.contract.model.enums.LeaseholdType.BOATING_PLACE;
import static se.sundsvall.contract.model.enums.LeaseholdType.BUILDING;
import static se.sundsvall.contract.model.enums.LeaseholdType.DEPOT;
import static se.sundsvall.contract.model.enums.LeaseholdType.DWELLING;
import static se.sundsvall.contract.model.enums.LeaseholdType.LAND_COMPLEMENT;
import static se.sundsvall.contract.model.enums.LeaseholdType.LINEUP;
import static se.sundsvall.contract.model.enums.LeaseholdType.OTHER;
import static se.sundsvall.contract.model.enums.LeaseholdType.PARKING;
import static se.sundsvall.contract.model.enums.LeaseholdType.RECYCLING_STATION;
import static se.sundsvall.contract.model.enums.LeaseholdType.ROAD;
import static se.sundsvall.contract.model.enums.LeaseholdType.SIGNBOARD;
import static se.sundsvall.contract.model.enums.LeaseholdType.SNOW_DUMP;
import static se.sundsvall.contract.model.enums.LeaseholdType.SPORTS_PURPOSE;
import static se.sundsvall.contract.model.enums.LeaseholdType.SURFACE_HEAT;
import static se.sundsvall.contract.model.enums.LeaseholdType.TRAIL;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.LeaseholdType;

class LeaseholdTypeTest {

	@Test
	void enums() {
		assertThat(LeaseholdType.values()).containsExactlyInAnyOrder(AGRICULTURE, APARTMENT, BOATING_PLACE, BUILDING, DEPOT, DWELLING, LAND_COMPLEMENT,
			LINEUP, OTHER, PARKING, RECYCLING_STATION, ROAD, SIGNBOARD, SNOW_DUMP, SPORTS_PURPOSE, SURFACE_HEAT, TRAIL);
	}

	@Test
	void enumValues() {
		assertThat(AGRICULTURE).hasToString("AGRICULTURE");
		assertThat(APARTMENT).hasToString("APARTMENT");
		assertThat(BOATING_PLACE).hasToString("BOATING_PLACE");
		assertThat(BUILDING).hasToString("BUILDING");
		assertThat(DEPOT).hasToString("DEPOT");
		assertThat(DWELLING).hasToString("DWELLING");
		assertThat(LAND_COMPLEMENT).hasToString("LAND_COMPLEMENT");
		assertThat(LINEUP).hasToString("LINEUP");
		assertThat(OTHER).hasToString("OTHER");
		assertThat(PARKING).hasToString("PARKING");
		assertThat(RECYCLING_STATION).hasToString("RECYCLING_STATION");
		assertThat(ROAD).hasToString("ROAD");
		assertThat(SIGNBOARD).hasToString("SIGNBOARD");
		assertThat(SNOW_DUMP).hasToString("SNOW_DUMP");
		assertThat(SPORTS_PURPOSE).hasToString("SPORTS_PURPOSE");
		assertThat(SURFACE_HEAT).hasToString("SURFACE_HEAT");
		assertThat(TRAIL).hasToString("TRAIL");
	}
}
