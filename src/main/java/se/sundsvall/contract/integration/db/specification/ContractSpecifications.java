package se.sundsvall.contract.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.contract.integration.db.model.ContractEntity;

import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;

/**
 * JPA specification utility for building dynamic {@link ContractEntity} queries.
 */
public final class ContractSpecifications {

	private ContractSpecifications() {}

	/**
	 * Creates a specification that filters contracts by the given municipality id.
	 *
	 * @param  municipalityId the municipality id to match
	 * @return                a specification matching contracts belonging to the given municipality
	 */
	public static Specification<ContractEntity> withMunicipalityId(final String municipalityId) {
		return (root, _, cb) -> cb.equal(root.get(MUNICIPALITY_ID), municipalityId);
	}
}
