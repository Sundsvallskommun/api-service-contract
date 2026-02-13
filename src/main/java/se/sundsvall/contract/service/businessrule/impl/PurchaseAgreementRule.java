package se.sundsvall.contract.service.businessrule.impl;

import static java.util.Optional.ofNullable;
import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.model.BusinessruleException;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;

/**
 * Business rule for purchase agreements that clears lease-specific attributes
 * not applicable to this contract type.
 */
@Service
public class PurchaseAgreementRule implements BusinessruleInterface {
	private final Logger logger;

	public PurchaseAgreementRule() {
		logger = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns true if the contract is of type {@code PURCHASE_AGREEMENT}.
	 */
	@Override
	public boolean appliesTo(ContractEntity contractEntity) {
		return Objects.equals(PURCHASE_AGREEMENT, contractEntity.getType());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Resets lease duration and extension attributes to null since they are not applicable for purchase agreements.
	 */
	@Override
	public void apply(BusinessruleParameters parameters) throws BusinessruleException {
		try {
			final var contractEntity = parameters.contractEntity();
			logger.info("Applying purchase agreement business rules for contract number {}", contractEntity.getContractId());

			// Reset duration and extension to null as these attributes are not applicable for purchase agreements
			contractEntity.setLeaseDuration(null);
			contractEntity.setLeaseDurationUnit(null);
			contractEntity.setLeaseExtension(null);
			contractEntity.setLeaseExtensionUnit(null);
			contractEntity.setAutoExtend(null);

		} catch (final Exception e) {
			final var contractId = ofNullable(parameters).map(BusinessruleParameters::contractEntity).map(ContractEntity::getContractId).orElse("[n/a]");
			logger.error("An exception occurred when applying purchase agreement business rules for contract number {}", contractId, e);
			throw new BusinessruleException("An exception occurred when applying purchase agreement business rules for contract number %s".formatted(contractId), e);
		}
	}

}
