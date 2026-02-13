package se.sundsvall.contract.service.businessrule.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BillableAgreementRuleConfigurationTest.TestConfiguration.class)
@ActiveProfiles("junit")
class BillableAgreementRuleConfigurationTest {

	@EnableConfigurationProperties(BillableAgreementRuleConfiguration.class)
	static class TestConfiguration {
	}

	@Autowired
	private BillableAgreementRuleConfiguration configuration;

	@Test
	void testProperties() {
		assertThat(configuration.managedMunicipalityIds()).containsExactlyInAnyOrder(
			"1234",
			"5678");
	}
}
