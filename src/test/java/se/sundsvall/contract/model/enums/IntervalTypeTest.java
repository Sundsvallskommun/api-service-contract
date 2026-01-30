package se.sundsvall.contract.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.IntervalType.HALF_YEARLY;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;

import org.junit.jupiter.api.Test;

class IntervalTypeTest {

	@Test
	void enums() {
		assertThat(IntervalType.values()).containsExactlyInAnyOrder(YEARLY, HALF_YEARLY, QUARTERLY, MONTHLY);
	}

	@Test
	void enumValues() {
		assertThat(YEARLY).hasToString("YEARLY");
		assertThat(HALF_YEARLY).hasToString("HALF_YEARLY");
		assertThat(QUARTERLY).hasToString("QUARTERLY");
		assertThat(MONTHLY).hasToString("MONTHLY");
	}
}
