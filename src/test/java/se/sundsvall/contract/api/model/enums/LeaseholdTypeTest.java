package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.LeaseholdType.AGRICULTURE;
import static se.sundsvall.contract.model.enums.LeaseholdType.APARTMENT;
import static se.sundsvall.contract.model.enums.LeaseholdType.BUILDING;
import static se.sundsvall.contract.model.enums.LeaseholdType.DWELLING;
import static se.sundsvall.contract.model.enums.LeaseholdType.OTHER;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.LeaseholdType;

class LeaseholdTypeTest {

	@Test
	void enums() {
		assertThat(LeaseholdType.values()).containsExactlyInAnyOrder(APARTMENT, BUILDING, AGRICULTURE, DWELLING, OTHER);
	}

	@Test
	void enumValues() {
		
		assertThat(APARTMENT).hasToString("APARTMENT");
		assertThat(BUILDING).hasToString("BUILDING");
		assertThat(AGRICULTURE).hasToString("AGRICULTURE");
		assertThat(DWELLING).hasToString("DWELLING");
		assertThat(OTHER).hasToString("OTHER");

	}
}
