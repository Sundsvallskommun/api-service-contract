package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.UsufructType.FISHING;
import static se.sundsvall.contract.model.enums.UsufructType.HUNTING;
import static se.sundsvall.contract.model.enums.UsufructType.MAINTENANCE;
import static se.sundsvall.contract.model.enums.UsufructType.OTHER;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.UsufructType;

class UsufructTypeTest {

	@Test
	void enums() {
		assertThat(UsufructType.values()).containsExactlyInAnyOrder(HUNTING, FISHING,
			MAINTENANCE, OTHER);
	}

	@Test
	void enumValues() {

		assertThat(HUNTING).hasToString("HUNTING");
		assertThat(FISHING).hasToString("FISHING");
		assertThat(MAINTENANCE).hasToString("MAINTENANCE");
		assertThat(OTHER).hasToString("OTHER");

	}
}
