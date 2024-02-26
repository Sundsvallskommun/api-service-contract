package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.AddressType.BILLING_ADDRESS;
import static se.sundsvall.contract.api.model.enums.AddressType.POSTAL_ADDRESS;
import static se.sundsvall.contract.api.model.enums.AddressType.VISITING_ADDRESS;

import org.junit.jupiter.api.Test;

class AddressTypeTest {

	@Test
	void enums() {
		assertThat(AddressType.values()).containsExactlyInAnyOrder(POSTAL_ADDRESS, BILLING_ADDRESS, VISITING_ADDRESS);
	}

	@Test
	void enumValues() {
		assertThat(POSTAL_ADDRESS).hasToString("POSTAL_ADDRESS");
		assertThat(BILLING_ADDRESS).hasToString("BILLING_ADDRESS");
		assertThat(VISITING_ADDRESS).hasToString("VISITING_ADDRESS");
	}

	/*@Test
	void enumTextValues() {
		assertThat(POSTAL_ADDRESS.getType()).isEqualTo("Postadress");
		assertThat(BILLING_ADDRESS.getType()).isEqualTo("Fakturaadress");
		assertThat(VISITING_ADDRESS.getType()).isEqualTo("Besöksadress");
	}*/
}
