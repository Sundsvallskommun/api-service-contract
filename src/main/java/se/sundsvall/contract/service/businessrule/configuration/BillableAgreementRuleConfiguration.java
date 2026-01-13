package se.sundsvall.contract.service.businessrule.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("business-rules.billable-agreement-rule")
public record BillableAgreementRuleConfiguration(List<String> managedMunicipalityIds) {
}
