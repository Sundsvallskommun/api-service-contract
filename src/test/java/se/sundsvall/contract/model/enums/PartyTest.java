package se.sundsvall.contract.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.Party.ALL;
import static se.sundsvall.contract.model.enums.Party.LESSEE;
import static se.sundsvall.contract.model.enums.Party.LESSOR;

import org.junit.jupiter.api.Test;

class PartyTest {

	@Test
	void enums() {
		assertThat(Party.values()).containsExactlyInAnyOrder(LESSEE, LESSOR, ALL);
	}

	@Test
	void enumValues() {
		assertThat(LESSEE).hasToString("LESSEE");
		assertThat(LESSOR).hasToString("LESSOR");
		assertThat(ALL).hasToString("ALL");
	}
}
