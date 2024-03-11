package se.sundsvall.contract.integration.db.specification;

import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.END;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.EXTERNAL_REFERENCE_ID;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.LAND_LEASE_TYPE;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.PROPERTY_DESIGNATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.model.ContractEntity;

public final class ContractSpecification {

	private ContractSpecification() { }

	public static Specification<ContractEntity> createContractSpecification(final String municipalityId, final ContractRequest request) {
		return ((root, query, criteriaBuilder) -> {
			final List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.equal(root.get(MUNICIPALITY_ID), municipalityId));

			if (request.landLeaseType() != null) {
				predicates.add(criteriaBuilder.equal(root.get(LAND_LEASE_TYPE), request.landLeaseType()));
			}

			if (request.end() != null) {
				predicates.add(criteriaBuilder.equal(root.get(END), LocalDate.parse(request.end())));
			}

			if (request.externalReferenceId() != null) {
				predicates.add(criteriaBuilder.equal(root.get(EXTERNAL_REFERENCE_ID), request.externalReferenceId()));
			}

			if (request.propertyDesignation() != null) {
				predicates.add(criteriaBuilder.equal(root.get(PROPERTY_DESIGNATION), request.propertyDesignation()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		});
	}

}
