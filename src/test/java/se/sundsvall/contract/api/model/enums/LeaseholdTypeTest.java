package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.AGRICULTURE;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.APARTMENT;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.BUILDING;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.DWELLING;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.OTHER;

import org.junit.jupiter.api.Test;

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

	@Test
	void enumTestValues() {

		assertThat(APARTMENT.getText()).isEqualTo("Lägenhetsarrende");
		assertThat(BUILDING.getText()).isEqualTo("Anläggningsarrende");
		assertThat(AGRICULTURE.getText()).isEqualTo("Jordbruksarrende");
		assertThat(DWELLING.getText()).isEqualTo("Bostadsarrende");
		assertThat(OTHER.getText()).isEqualTo("Annat");
	}

}
