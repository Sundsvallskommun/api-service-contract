package se.sundsvall.contract.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.LeaseType.LAND_LEASE_MISC;
import static se.sundsvall.contract.model.enums.LeaseType.LAND_LEASE_RESIDENTIAL;
import static se.sundsvall.contract.model.enums.LeaseType.OTHER_FEE;
import static se.sundsvall.contract.model.enums.LeaseType.SITE_LEASE_COMMERCIAL;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_FARMING;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_HUNTING;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_MISC;

class LeaseTypeTest {

	@Test
	void enums() {
		assertThat(LeaseType.values()).containsExactlyInAnyOrder(
			LAND_LEASE_RESIDENTIAL,
			SITE_LEASE_COMMERCIAL,
			USUFRUCT_HUNTING,
			USUFRUCT_FARMING,
			USUFRUCT_MISC,
			LAND_LEASE_MISC,
			OTHER_FEE);
	}

	@Test
	void enumValues() {
		assertThat(LAND_LEASE_RESIDENTIAL).hasToString("LAND_LEASE_RESIDENTIAL");
		assertThat(SITE_LEASE_COMMERCIAL).hasToString("SITE_LEASE_COMMERCIAL");
		assertThat(USUFRUCT_HUNTING).hasToString("USUFRUCT_HUNTING");
		assertThat(USUFRUCT_FARMING).hasToString("USUFRUCT_FARMING");
		assertThat(USUFRUCT_MISC).hasToString("USUFRUCT_MISC");
		assertThat(LAND_LEASE_MISC).hasToString("LAND_LEASE_MISC");
		assertThat(OTHER_FEE).hasToString("OTHER_FEE");
	}
}
