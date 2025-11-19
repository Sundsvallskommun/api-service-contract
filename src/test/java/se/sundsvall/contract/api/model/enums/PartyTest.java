package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.Party.LESSEE;
import static se.sundsvall.contract.model.enums.Party.LESSOR;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.Party;

class PartyTest {

	@Test
	void enums() {
		assertThat(Party.values()).containsExactlyInAnyOrder(LESSEE, LESSOR);
	}

	@Test
	void enumValues() {
		assertThat(LESSEE).hasToString("LESSEE");
		assertThat(LESSOR).hasToString("LESSOR");
	}
}
