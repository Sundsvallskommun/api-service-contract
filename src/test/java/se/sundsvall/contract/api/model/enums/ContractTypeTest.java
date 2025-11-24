package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.ContractType.LEASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.ContractType;

class ContractTypeTest {

	@Test
	void enums() {
		assertThat(ContractType.values()).containsExactlyInAnyOrder(LEASE_AGREEMENT, PURCHASE_AGREEMENT);
	}

	@Test
	void enumValues() {
		assertThat(LEASE_AGREEMENT).hasToString("LEASE_AGREEMENT");
		assertThat(PURCHASE_AGREEMENT).hasToString("PURCHASE_AGREEMENT");
	}
}
