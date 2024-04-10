package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.LandLeaseType.LEASEHOLD;
import static se.sundsvall.contract.model.enums.LandLeaseType.SITELEASEHOLD;
import static se.sundsvall.contract.model.enums.LandLeaseType.USUFRUCT;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.LandLeaseType;

class LandLeaseTypeTest {

	@Test
	void enums() {
		assertThat(LandLeaseType.values()).containsExactlyInAnyOrder(LEASEHOLD, USUFRUCT, SITELEASEHOLD);
	}

	@Test
	void enumValues() {
		assertThat(LEASEHOLD).hasToString("LEASEHOLD");
		assertThat(USUFRUCT).hasToString("USUFRUCT");
		assertThat(SITELEASEHOLD).hasToString("SITELEASEHOLD");

	}
}
