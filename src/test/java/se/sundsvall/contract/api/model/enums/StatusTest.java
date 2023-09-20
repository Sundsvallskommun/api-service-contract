package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.api.model.enums.Status.TERMINATED;

import org.junit.jupiter.api.Test;

class StatusTest {

	@Test
	void enums() {
		assertThat(Status.values()).containsExactlyInAnyOrder(ACTIVE, TERMINATED);
	}

	@Test
	void enumValues() {
		assertThat(ACTIVE).hasToString("ACTIVE");
		assertThat(TERMINATED).hasToString("TERMINATED");
	}

	@Test
	void enumTextValues() {
		assertThat(ACTIVE.getText()).isEqualTo("Aktiv");
		assertThat(TERMINATED.getText()).isEqualTo("Avslutad");
	}

}
