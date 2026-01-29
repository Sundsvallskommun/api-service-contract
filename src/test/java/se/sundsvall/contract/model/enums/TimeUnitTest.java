package se.sundsvall.contract.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.TimeUnit.DAYS;
import static se.sundsvall.contract.model.enums.TimeUnit.MONTHS;
import static se.sundsvall.contract.model.enums.TimeUnit.YEARS;

import org.junit.jupiter.api.Test;

class TimeUnitTest {

	@Test
	void enums() {
		assertThat(TimeUnit.values()).containsExactlyInAnyOrder(DAYS, MONTHS, YEARS);
	}

	@Test
	void enumValues() {
		assertThat(DAYS).hasToString("DAYS");
		assertThat(MONTHS).hasToString("MONTHS");
		assertThat(YEARS).hasToString("YEARS");
	}
}
