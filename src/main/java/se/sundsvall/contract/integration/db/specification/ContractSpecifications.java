package se.sundsvall.contract.integration.db.specification;

import static se.sundsvall.contract.integration.db.model.ContractEntity_.CONTRACT_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.VERSION;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.contract.integration.db.model.ContractEntity;

public final class ContractSpecifications {

	private ContractSpecifications() {}

	public static Specification<ContractEntity> withOnlyLatestVersion() {
		return (root, query, cb) -> {
			final var subQuery = query.subquery(Integer.class);
			final var subRoot = subQuery.from(ContractEntity.class);
			subQuery.select(cb.max(subRoot.get(VERSION))).where(cb.equal(root.get(CONTRACT_ID), subRoot.get(CONTRACT_ID)));

			return cb.equal(root.get(VERSION), subQuery);
		};
	}

	public static Specification<ContractEntity> withMunicipalityId(final String municipalityId) {
		return (root, query, cb) -> cb.equal(root.get(MUNICIPALITY_ID), municipalityId);
	}
}
