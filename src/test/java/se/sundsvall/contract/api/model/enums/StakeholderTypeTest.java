package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.StakeholderType.ASSOCIATION;
import static se.sundsvall.contract.model.enums.StakeholderType.COMPANY;
import static se.sundsvall.contract.model.enums.StakeholderType.MUNICIPALITY;
import static se.sundsvall.contract.model.enums.StakeholderType.OTHER;
import static se.sundsvall.contract.model.enums.StakeholderType.PERSON;
import static se.sundsvall.contract.model.enums.StakeholderType.REGION;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.StakeholderType;

class StakeholderTypeTest {

	@Test
	void enums() {
		assertThat(StakeholderType.values()).containsExactlyInAnyOrder(ASSOCIATION, COMPANY, PERSON, MUNICIPALITY, REGION, OTHER);
	}

	@Test
	void enumValues() {
		assertThat(ASSOCIATION).hasToString("ASSOCIATION");
		assertThat(COMPANY).hasToString("COMPANY");
		assertThat(PERSON).hasToString("PERSON");
		assertThat(MUNICIPALITY).hasToString("MUNICIPALITY");
		assertThat(REGION).hasToString("REGION");
		assertThat(OTHER).hasToString("OTHER");
	}
}
