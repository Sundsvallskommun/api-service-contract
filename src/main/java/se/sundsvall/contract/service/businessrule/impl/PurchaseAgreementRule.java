package se.sundsvall.contract.service.businessrule.impl;

import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.service.businessrule.BusinessruleException;
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
	public void apply(ContractEntity contractEntity) throws BusinessruleException {
		try {
			logger.info("Applying purchase agreement business rules for contract number {}", contractEntity.getContractId());

			// Reset duration and extension to null as these attributes are not applicable for purchase agreements
			contractEntity.setLeaseDuration(null);
			contractEntity.setLeaseDurationUnit(null);
			contractEntity.setLeaseExtension(null);
			contractEntity.setLeaseExtensionUnit(null);
			contractEntity.setAutoExtend(null);

		} catch (final Exception e) {
			// Wrap exception and rethrow as a BusinessruleException
			throw new BusinessruleException("An exception occurred when applying purchase agreement business rules for contract number %s".formatted(contractEntity.getContractId()), e);
		}
	}

}
