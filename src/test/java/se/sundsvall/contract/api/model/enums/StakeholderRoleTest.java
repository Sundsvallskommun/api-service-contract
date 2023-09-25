package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.ARRENDATOR;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.FIRMATECKNARE;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.FULLMAKTSROLL;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.KONTAKTPERSON;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.MARKAGARE;
import static se.sundsvall.contract.api.model.enums.StakeholderRole.values;

import org.junit.jupiter.api.Test;

class StakeholderRoleTest {

	@Test
	void enums() {
		assertThat(values()).containsExactlyInAnyOrder(FULLMAKTSROLL, ARRENDATOR, FIRMATECKNARE, KONTAKTPERSON, MARKAGARE);
	}

	@Test
	void enumValues() {
		assertThat(FULLMAKTSROLL).hasToString("FULLMAKTSROLL");
		assertThat(ARRENDATOR).hasToString("ARRENDATOR");
		assertThat(FIRMATECKNARE).hasToString("FIRMATECKNARE");
		assertThat(KONTAKTPERSON).hasToString("KONTAKTPERSON");
		assertThat(MARKAGARE).hasToString("MARKAGARE");
	}

	@Test
	void enumTextValues() {
		assertThat(FULLMAKTSROLL.getText()).isEqualTo("Fullmaktsroll");
		assertThat(ARRENDATOR.getText()).isEqualTo("Arrendator");
		assertThat(FIRMATECKNARE.getText()).isEqualTo("Firmatecknare");
		assertThat(KONTAKTPERSON.getText()).isEqualTo("Kontaktperson");
		assertThat(MARKAGARE.getText()).isEqualTo("Markägare");
	}

}
