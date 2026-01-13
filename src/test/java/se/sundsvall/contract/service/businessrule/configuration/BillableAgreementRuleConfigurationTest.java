package se.sundsvall.contract.service.businessrule.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.contract.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class BillableAgreementRuleConfigurationTest {

	@Autowired
	private BillableAgreementRuleConfiguration configuration;

	@Test
	void testProperties() {
		assertThat(configuration.managedMunicipalityIds()).containsExactlyInAnyOrder(
			"1234",
			"5678");
	}
}
