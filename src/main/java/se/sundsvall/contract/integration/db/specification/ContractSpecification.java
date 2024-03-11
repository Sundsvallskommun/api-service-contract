package se.sundsvall.contract.integration.db.specification;

import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.END;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.EXTERNAL_REFERENCE_ID;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.LAND_LEASE_TYPE;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.PROPERTY_DESIGNATIONS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.model.ContractEntity;

public final class ContractSpecification {

	private ContractSpecification() { }

	public static Specification<ContractEntity> createContractSpecification(final String municipalityId, final ContractRequest request) {
		return ((root, query, criteriaBuilder) -> {
			final List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.equal(root.get(MUNICIPALITY_ID), municipalityId));

			if (StringUtils.isNotBlank(request.landLeaseType())) {
				predicates.add(criteriaBuilder.equal(root.get(LAND_LEASE_TYPE), request.landLeaseType()));
			}

			if (StringUtils.isNotBlank(request.end())) {
				predicates.add(criteriaBuilder.equal(root.get(END), LocalDate.parse(request.end())));
			}

			if (StringUtils.isNotBlank(request.externalReferenceId())) {
				predicates.add(criteriaBuilder.equal(root.get(EXTERNAL_REFERENCE_ID), request.externalReferenceId()));
			}

			if(!CollectionUtils.isEmpty(request.propertyDesignations())) {

				final List<Predicate> propertyPredicate = new ArrayList<>();

				for(String property : request.propertyDesignations()) {
					propertyPredicate.add(criteriaBuilder.isMember(property, root.get(PROPERTY_DESIGNATIONS)));
				}

				predicates.add(criteriaBuilder.or(propertyPredicate.toArray(new Predicate[0])));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		});
	}

}
