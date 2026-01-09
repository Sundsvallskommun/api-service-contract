package se.sundsvall.contract.service.businessrule.impl;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.service.businessrule.ContractTypeRuleInterface;

@Service
public class PurchaseAgreementRule implements ContractTypeRuleInterface {
	private final Logger logger;

	public PurchaseAgreementRule() {
		logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public boolean appliesTo(ContractEntity contractEntity) {
		return Objects.equals(PURCHASE_AGREEMENT, contractEntity.getType());
	}

	@Override
	public boolean apply(ContractEntity contractEntity) {
		try {
			logger.info("Applying purchase agreement business rules for contract number {}", contractEntity.getContractId());

			// Reset duration and extension to null as these attributes are not applicable for purchase agreements
			contractEntity.setLeaseDuration(null);
			contractEntity.setLeaseDurationUnit(null);
			contractEntity.setLeaseExtension(null);
			contractEntity.setLeaseExtensionUnit(null);
			contractEntity.setAutoExtend(null);

			return true; // Indicates that the business rule have been applied

		} catch (final Exception e) {
			// Log and throw runnable problem
			logger.error("An exception occurred when applying purchase agreement business rules for contract number {}", contractEntity.getContractId(), e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "An exception occurred when applying purchase agreement business rules for contract number %s".formatted(contractEntity.getContractId()));
		}
	}

}
