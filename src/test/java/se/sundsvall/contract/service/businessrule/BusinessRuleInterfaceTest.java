package se.sundsvall.contract.service.businessrule;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.service.businessrule.impl.BillableAgreementRule;
import se.sundsvall.contract.service.businessrule.impl.PurchaseAgreementRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = MOCK)
class BusinessRuleInterfaceTest {

	@Autowired
	private List<BusinessruleInterface> contactTypeRules;

	@Test
	void verifyAllRulesLoaded() {
		assertThat(contactTypeRules)
			.hasSize(2)
			.hasAtLeastOneElementOfType(PurchaseAgreementRule.class)
			.hasAtLeastOneElementOfType(BillableAgreementRule.class);
	}

}
