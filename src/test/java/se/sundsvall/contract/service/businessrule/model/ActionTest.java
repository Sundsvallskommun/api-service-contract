package se.sundsvall.contract.service.businessrule.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.service.businessrule.model.Action.CREATE;
import static se.sundsvall.contract.service.businessrule.model.Action.DELETE;
import static se.sundsvall.contract.service.businessrule.model.Action.UPDATE;

import org.junit.jupiter.api.Test;

class ActionTest {

	@Test
	void enums() {
		assertThat(Action.values()).containsExactlyInAnyOrder(CREATE, UPDATE, DELETE);
	}

	@Test
	void enumValues() {
		assertThat(CREATE).hasToString("CREATE");
		assertThat(UPDATE).hasToString("UPDATE");
		assertThat(DELETE).hasToString("DELETE");
	}
}
