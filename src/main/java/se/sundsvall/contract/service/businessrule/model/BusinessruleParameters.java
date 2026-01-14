package se.sundsvall.contract.service.businessrule.model;

import se.sundsvall.contract.integration.db.model.ContractEntity;

/**
 * Record containing the data that each businessrule needs to perform its duty
 * 
 * @param contractEntity the entity to process
 * @param action         the action that is performed on the entity, can be null
 */
public record BusinessruleParameters(ContractEntity contractEntity, Action action) {
}
