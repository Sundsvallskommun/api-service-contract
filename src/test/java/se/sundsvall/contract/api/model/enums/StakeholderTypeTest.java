package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.StakeholderType.ASSOCIATION;
import static se.sundsvall.contract.model.enums.StakeholderType.MUNICIPALITY;
import static se.sundsvall.contract.model.enums.StakeholderType.ORGANIZATION;
import static se.sundsvall.contract.model.enums.StakeholderType.OTHER;
import static se.sundsvall.contract.model.enums.StakeholderType.PERSON;
import static se.sundsvall.contract.model.enums.StakeholderType.REGION;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.StakeholderType;

class StakeholderTypeTest {

	@Test
	void enums() {
		assertThat(StakeholderType.values()).containsExactlyInAnyOrder(ASSOCIATION, ORGANIZATION, PERSON, MUNICIPALITY, REGION, OTHER);
	}

	@Test
	void enumValues() {
		assertThat(ASSOCIATION).hasToString("ASSOCIATION");
		assertThat(ORGANIZATION).hasToString("ORGANIZATION");
		assertThat(PERSON).hasToString("PERSON");
		assertThat(MUNICIPALITY).hasToString("MUNICIPALITY");
		assertThat(REGION).hasToString("REGION");
		assertThat(OTHER).hasToString("OTHER");
	}
}
