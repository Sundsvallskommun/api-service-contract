package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.StakeholderType.ASSOCIATION;
import static se.sundsvall.contract.api.model.enums.StakeholderType.COMPANY;
import static se.sundsvall.contract.api.model.enums.StakeholderType.PERSON;

import org.junit.jupiter.api.Test;

class StakeholderTypeTest {

	@Test
	void enums() {
		assertThat(StakeholderType.values()).containsExactlyInAnyOrder(ASSOCIATION, COMPANY, PERSON);
	}

	@Test
	void enumValues() {
		assertThat(ASSOCIATION).hasToString("ASSOCIATION");
		assertThat(COMPANY).hasToString("COMPANY");
		assertThat(PERSON).hasToString("PERSON");
	}

}
