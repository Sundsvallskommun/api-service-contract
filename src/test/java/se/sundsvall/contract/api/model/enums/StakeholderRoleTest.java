package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.BUYER;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.CONTACT_PERSON;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.GRANTOR;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.LAND_OWNER;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.LEASE_HOLDER;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.POWER_OF_ATTORNEY_CHECK;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.POWER_OF_ATTORNEY_ROLE;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.SELLER;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.SIGNATORY;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.values;

import org.junit.jupiter.api.Test;

class StakeholderRoleTest {

	@Test
	void enums() {
		assertThat(values()).containsExactlyInAnyOrder(
			BUYER,
			CONTACT_PERSON,
			GRANTOR,
			LAND_OWNER,
			LEASE_HOLDER,
			POWER_OF_ATTORNEY_CHECK,
			POWER_OF_ATTORNEY_ROLE,
			SELLER,
			SIGNATORY);
	}

	@Test
	void enumValues() {
		assertThat(BUYER).hasToString("BUYER");
		assertThat(CONTACT_PERSON).hasToString("CONTACT_PERSON");
		assertThat(GRANTOR).hasToString("GRANTOR");
		assertThat(LAND_OWNER).hasToString("LAND_OWNER");
		assertThat(LEASE_HOLDER).hasToString("LEASE_HOLDER");
		assertThat(POWER_OF_ATTORNEY_CHECK).hasToString("POWER_OF_ATTORNEY_CHECK");
		assertThat(POWER_OF_ATTORNEY_ROLE).hasToString("POWER_OF_ATTORNEY_ROLE");
		assertThat(SELLER).hasToString("SELLER");
		assertThat(SIGNATORY).hasToString("SIGNATORY");
	}
}
