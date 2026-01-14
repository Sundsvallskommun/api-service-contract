package se.sundsvall.contract.service.businessrule;

import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.service.businessrule.model.BusinessruleException;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;

public interface BusinessruleInterface {

	/**
	 * Method for evaluating if the provided contract entity should apply the business rule or not
	 *
	 * @param  contractEntity the entity to evaluate
	 * @return                true if contract entity should apply business rule, false otherwise
	 */
	boolean appliesTo(ContractEntity contractEntity);

	/**
	 * /**
	 * Method for applying business rule to the provided contract entity
	 *
	 * @param  businessruleParameters parameter object with data needed to applying the business rule
	 * @throws BusinessruleException  will be thrown on error when applying rule to contract
	 */
	void apply(BusinessruleParameters businessruleParameters) throws BusinessruleException;
}
