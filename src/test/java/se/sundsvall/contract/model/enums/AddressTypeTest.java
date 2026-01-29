package se.sundsvall.contract.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.AddressType.BILLING_ADDRESS;
import static se.sundsvall.contract.model.enums.AddressType.POSTAL_ADDRESS;
import static se.sundsvall.contract.model.enums.AddressType.VISITING_ADDRESS;

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
}
