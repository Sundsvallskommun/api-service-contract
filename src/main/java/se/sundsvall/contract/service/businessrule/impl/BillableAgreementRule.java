package se.sundsvall.contract.service.businessrule.impl;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.contract.integration.billingdatacollector.mapper.BillingDataCollectorMapper.toScheduledBilling;
import static se.sundsvall.contract.service.businessrule.ContractUtility.isBillable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.contract.integration.billingdatacollector.BillingDataCollectorIntegration;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.configuration.BillableAgreementRuleConfiguration;
import se.sundsvall.contract.service.businessrule.model.BusinessruleException;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;

/**
 * Business rule for managing billing cycles in the BillingDataCollector service
 * when contracts are created, updated or deleted.
 */
@Service
public class BillableAgreementRule implements BusinessruleInterface {
	private final Logger logger;
	private final BillableAgreementRuleConfiguration configuration;
	private final BillingDataCollectorIntegration bdcIntegration;

	public BillableAgreementRule(BillableAgreementRuleConfiguration configuration, BillingDataCollectorIntegration bdcIntegration) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.configuration = configuration;
		this.bdcIntegration = bdcIntegration;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns true if the contract belongs to a municipality configured for billing integration.
	 */
	@Override
	public boolean appliesTo(ContractEntity contractEntity) {
		// All contracts within the municipality are considered applicable to the rule if it is activated for the municipality
		return ofNullable(configuration.managedMunicipalityIds()).orElse(emptyList()).contains(contractEntity.getMunicipalityId());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Adds, updates or removes billing cycles depending on the action and whether the contract is billable.
	 */
	@Override
	public void apply(BusinessruleParameters parameters) throws BusinessruleException {
		try {
			final var contractEntity = parameters.contractEntity();
			final var action = parameters.action();

			switch (action) {
				case null -> throw new IllegalArgumentException("Action can not be null");
				case CREATE -> processCreate(contractEntity);
				case UPDATE -> processUpdate(contractEntity);
				case DELETE -> processDelete(contractEntity);
			}

		} catch (final Exception e) {
			// Wrap exception and rethrow as a BusinessruleException
			throw new BusinessruleException("An exception occurred when applying billable agreement business rules for contract number %s"
				.formatted(ofNullable(parameters).map(BusinessruleParameters::contractEntity).map(ContractEntity::getContractId).orElse("[n/a]")), e);
		}
	}

	private void processCreate(ContractEntity contractEntity) {
		if (isBillable(contractEntity)) { // If agreement is billable at the moment of creation, add billing info in BDC
			logger.info("Adding billing information in BillingDataCollector for contract number {}", contractEntity.getContractId());
			bdcIntegration.addBillingCycle(contractEntity.getMunicipalityId(), contractEntity.getContractId(), toScheduledBilling(contractEntity));
		}
	}

	private void processUpdate(ContractEntity contractEntity) {
		if (isBillable(contractEntity)) { // If agreement is billable at the moment of update, add billing info in BDC
			logger.info("Updating BillingDataCollector by adding billing information for existing contract number {}", contractEntity.getContractId());
			bdcIntegration.addBillingCycle(contractEntity.getMunicipalityId(), contractEntity.getContractId(), toScheduledBilling(contractEntity));
		} else { // else remove billing info in BDC
			logger.info("Updating BillingDataCollector by removing billing information for existing contract number {}", contractEntity.getContractId());
			bdcIntegration.removeBillingCycle(contractEntity.getMunicipalityId(), contractEntity.getContractId());
		}
	}

	private void processDelete(ContractEntity contractEntity) {
		logger.info("Removing possible billing information in BillingDataCollector for contract number {}", contractEntity.getContractId());
		bdcIntegration.removeBillingCycle(contractEntity.getMunicipalityId(), contractEntity.getContractId());
	}
}
