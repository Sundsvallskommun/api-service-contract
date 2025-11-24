package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.LeaseType.LAND_LEASE_MISC;
import static se.sundsvall.contract.model.enums.LeaseType.LAND_LEASE_PUBLIC;
import static se.sundsvall.contract.model.enums.LeaseType.LAND_LEASE_RESIDENTIAL;
import static se.sundsvall.contract.model.enums.LeaseType.LEASEHOLD;
import static se.sundsvall.contract.model.enums.LeaseType.OBJECT_LEASE;
import static se.sundsvall.contract.model.enums.LeaseType.OTHER_FEE;
import static se.sundsvall.contract.model.enums.LeaseType.SITE_LEASE_COMMERCIAL;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_FARMING;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_HUNTING;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_MISC;
import static se.sundsvall.contract.model.enums.LeaseType.USUFRUCT_MOORING;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.LeaseType;

class LeaseTypeTest {

	@Test
	void enums() {
		assertThat(LeaseType.values()).containsExactlyInAnyOrder(
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
			OTHER_FEE);
	}

	@Test
	void enumValues() {
		assertThat(LAND_LEASE_PUBLIC).hasToString("LAND_LEASE_PUBLIC");
		assertThat(LAND_LEASE_RESIDENTIAL).hasToString("LAND_LEASE_RESIDENTIAL");
		assertThat(SITE_LEASE_COMMERCIAL).hasToString("SITE_LEASE_COMMERCIAL");
		assertThat(USUFRUCT_MOORING).hasToString("USUFRUCT_MOORING");
		assertThat(USUFRUCT_HUNTING).hasToString("USUFRUCT_HUNTING");
		assertThat(USUFRUCT_FARMING).hasToString("USUFRUCT_FARMING");
		assertThat(USUFRUCT_MISC).hasToString("USUFRUCT_MISC");
		assertThat(OBJECT_LEASE).hasToString("OBJECT_LEASE");
		assertThat(LAND_LEASE_MISC).hasToString("LAND_LEASE_MISC");
		assertThat(LEASEHOLD).hasToString("LEASEHOLD");
		assertThat(OTHER_FEE).hasToString("OTHER_FEE");
	}
}
