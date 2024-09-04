package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.StakeholderRole.BUYER;
import static se.sundsvall.contract.model.enums.StakeholderRole.CONTACT_PERSON;
import static se.sundsvall.contract.model.enums.StakeholderRole.GRANTOR;
import static se.sundsvall.contract.model.enums.StakeholderRole.LAND_RIGHT_OWNER;
import static se.sundsvall.contract.model.enums.StakeholderRole.LEASEHOLDER;
import static se.sundsvall.contract.model.enums.StakeholderRole.POWER_OF_ATTORNEY_CHECK;
import static se.sundsvall.contract.model.enums.StakeholderRole.POWER_OF_ATTORNEY_ROLE;
import static se.sundsvall.contract.model.enums.StakeholderRole.PROPERTY_OWNER;
import static se.sundsvall.contract.model.enums.StakeholderRole.SELLER;
import static se.sundsvall.contract.model.enums.StakeholderRole.SIGNATORY;
import static se.sundsvall.contract.model.enums.StakeholderRole.values;

import org.junit.jupiter.api.Test;

class StakeholderRoleTest {

	@Test
	void enums() {
		assertThat(values()).containsExactlyInAnyOrder(
			BUYER,
			CONTACT_PERSON,
			GRANTOR,
			LAND_RIGHT_OWNER,
			LEASEHOLDER,
			PROPERTY_OWNER,
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
		assertThat(LAND_RIGHT_OWNER).hasToString("LAND_RIGHT_OWNER");
		assertThat(LEASEHOLDER).hasToString("LEASEHOLDER");
		assertThat(PROPERTY_OWNER).hasToString("PROPERTY_OWNER");
		assertThat(POWER_OF_ATTORNEY_CHECK).hasToString("POWER_OF_ATTORNEY_CHECK");
		assertThat(POWER_OF_ATTORNEY_ROLE).hasToString("POWER_OF_ATTORNEY_ROLE");
		assertThat(SELLER).hasToString("SELLER");
		assertThat(SIGNATORY).hasToString("SIGNATORY");
	}
}
