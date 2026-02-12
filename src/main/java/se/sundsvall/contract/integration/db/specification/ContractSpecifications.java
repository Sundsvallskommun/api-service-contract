package se.sundsvall.contract.integration.db.specification;

import static se.sundsvall.contract.integration.db.model.ContractEntity_.CONTRACT_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.VERSION;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.contract.integration.db.model.ContractEntity;

/**
 * JPA specification utility for building dynamic {@link ContractEntity} queries.
 */
public final class ContractSpecifications {

	private ContractSpecifications() {}

	/**
	 * Creates a specification that filters contracts to only include the latest version of each contract.
	 *
	 * @return a specification matching only the latest version per contract id
	 */
	public static Specification<ContractEntity> withOnlyLatestVersion() {
		return (root, query, cb) -> {
			final var subQuery = query.subquery(Integer.class);
			final var subRoot = subQuery.from(ContractEntity.class);
			subQuery.select(cb.max(subRoot.get(VERSION))).where(cb.equal(root.get(CONTRACT_ID), subRoot.get(CONTRACT_ID)));

			return cb.equal(root.get(VERSION), subQuery);
		};
	}

	/**
	 * Creates a specification that filters contracts by the given municipality id.
	 *
	 * @param  municipalityId the municipality id to match
	 * @return                a specification matching contracts belonging to the given municipality
	 */
	public static Specification<ContractEntity> withMunicipalityId(final String municipalityId) {
		return (root, query, cb) -> cb.equal(root.get(MUNICIPALITY_ID), municipalityId);
	}
}
