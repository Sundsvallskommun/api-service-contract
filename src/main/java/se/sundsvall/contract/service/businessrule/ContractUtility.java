package se.sundsvall.contract.service.businessrule;

import static java.util.Optional.ofNullable;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;

import java.util.Objects;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;

/**
 * Utility class with helper methods for evaluating {@link ContractEntity} properties.
 */
public final class ContractUtility {
	private ContractUtility() {
		// Prevent instantiation
	}

	/**
	 * Method evaluates if contract is billable or not. A contract is interpreted as billable if it has status ACTIVE and
	 * has a value other than null for the invoice interval attribute (residing in the invoicing object).
	 *
	 * @param  contractEntity contract to evaluate
	 * @return                true if contract is evaluated as billable, false otherwise
	 */
	public static boolean isBillable(ContractEntity contractEntity) {
		return Objects.equals(ACTIVE, contractEntity.getStatus()) &&
			ofNullable(contractEntity.getInvoicing())
				.map(InvoicingEmbeddable::getInvoiceInterval)
				.isPresent();
	}
}
