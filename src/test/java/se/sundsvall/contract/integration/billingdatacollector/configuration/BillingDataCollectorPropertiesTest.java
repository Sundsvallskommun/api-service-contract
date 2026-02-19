package se.sundsvall.contract.integration.billingdatacollector.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.contract.Application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class BillingDataCollectorPropertiesTest {

	@Autowired
	private BillingDataCollectorProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(123);
		assertThat(properties.readTimeout()).isEqualTo(456);
	}
}
