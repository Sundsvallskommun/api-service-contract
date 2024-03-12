package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.api.model.enums.InvoicedIn.ARREARS;

import org.junit.jupiter.api.Test;

class InvoicedInTest {

	@Test
	void enums() {
		assertThat(InvoicedIn.values()).containsExactlyInAnyOrder(ADVANCE, ARREARS);
	}

	@Test
	void enumValues() {
		assertThat(ADVANCE).hasToString("ADVANCE");
		assertThat(ARREARS).hasToString("ARREARS");
	}
}
