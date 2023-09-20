package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IntervalTypeTest {

	@Test
	void enums() {
		assertThat(IntervalType.values()).containsExactlyInAnyOrder(IntervalType.YEARLY, IntervalType.QUARTERLY, IntervalType.MONTHLY);
	}

	@Test
	void enumValues() {
		assertThat(IntervalType.YEARLY).hasToString("YEARLY");
		assertThat(IntervalType.QUARTERLY).hasToString("QUARTERLY");
		assertThat(IntervalType.MONTHLY).hasToString("MONTHLY");
	}

	@Test
	void enumTextValues() {
		assertThat(IntervalType.YEARLY.getType()).isEqualTo("årligen");
		assertThat(IntervalType.QUARTERLY.getType()).isEqualTo("kvartalsvis");
		assertThat(IntervalType.MONTHLY.getType()).isEqualTo("månadsvis");
	}

}
