package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.Status.DRAFT;
import static se.sundsvall.contract.model.enums.Status.TERMINATED;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.Status;

class StatusTest {

	@Test
	void enums() {
		assertThat(Status.values()).containsExactlyInAnyOrder(ACTIVE, DRAFT, TERMINATED);
	}

	@Test
	void enumValues() {
		assertThat(ACTIVE).hasToString("ACTIVE");
		assertThat(DRAFT).hasToString("DRAFT");
		assertThat(TERMINATED).hasToString("TERMINATED");
	}
}
