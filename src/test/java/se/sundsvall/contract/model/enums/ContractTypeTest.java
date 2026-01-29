package se.sundsvall.contract.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.ContractType.LAND_LEASE_PUBLIC;
import static se.sundsvall.contract.model.enums.ContractType.LEASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.ContractType.SHORT_TERM_LEASE_AGREEMENT;

import org.junit.jupiter.api.Test;

class ContractTypeTest {

	@Test
	void enums() {
		assertThat(ContractType.values()).containsExactlyInAnyOrder(LEASE_AGREEMENT, PURCHASE_AGREEMENT, LAND_LEASE_PUBLIC, SHORT_TERM_LEASE_AGREEMENT);
	}

	@Test
	void enumValues() {
		assertThat(LEASE_AGREEMENT).hasToString("LEASE_AGREEMENT");
		assertThat(PURCHASE_AGREEMENT).hasToString("PURCHASE_AGREEMENT");
		assertThat(LAND_LEASE_PUBLIC).hasToString("LAND_LEASE_PUBLIC");
		assertThat(SHORT_TERM_LEASE_AGREEMENT).hasToString("SHORT_TERM_LEASE_AGREEMENT");
	}
}
