package se.sundsvall.contract.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;

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
