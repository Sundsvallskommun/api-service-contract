package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.IntervalType;

class IntervalTypeTest {

	@Test
	void enums() {
		assertThat(IntervalType.values()).containsExactlyInAnyOrder(YEARLY, QUARTERLY, MONTHLY);
	}

	@Test
	void enumValues() {
		assertThat(YEARLY).hasToString("YEARLY");
		assertThat(QUARTERLY).hasToString("QUARTERLY");
		assertThat(MONTHLY).hasToString("MONTHLY");
	}
}
