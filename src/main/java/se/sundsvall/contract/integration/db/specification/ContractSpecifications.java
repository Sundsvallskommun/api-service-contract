package se.sundsvall.contract.integration.db.specification;

import static java.util.function.Predicate.not;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.STAKEHOLDERS;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.END;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.EXTERNAL_REFERENCE_ID;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.LAND_LEASE_TYPE;
import static se.sundsvall.contract.integration.db.model.LandLeaseContractEntity_.PROPERTY_DESIGNATIONS;
import static se.sundsvall.contract.integration.db.model.StakeholderEntity_.ORGANIZATION_NUMBER;
import static se.sundsvall.contract.integration.db.model.StakeholderEntity_.PERSON_ID;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.model.ContractEntity;

public final class ContractSpecifications {

	private static final Specification<ContractEntity> EMPTY = Specification.where(null);

	private ContractSpecifications() { }

	public static Specification<ContractEntity> createContractSpecification(final String municipalityId, final ContractRequest request) {
		return withMunicipalityId(municipalityId)
			.and(withContractId(request.contractId()))
			.and(withPersonId(request.personId()))
			.and(withOrganizationNumber(request.organizationNumber()))
			.and(withEndDate(request.end()))
			.and(withLandLeaseType(request.landLeaseType()))
			.and(withExternalReferenceId(request.externalReferenceId()))
			.and(withPropertyDesignations(request.propertyDesignations()));
	}

	private static Specification<ContractEntity> withMunicipalityId(final String municipalityId) {
		return (root, query, cb) -> cb.equal(root.get(MUNICIPALITY_ID), municipalityId);
	}

	private static Specification<ContractEntity> withContractId(final String contractId) {
		if (isBlank(contractId)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.get(ID), contractId);
	}

	private static Specification<ContractEntity> withPersonId(final String personId) {
		if (isBlank(personId)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.join(STAKEHOLDERS).get(PERSON_ID), personId);
	}

	private static Specification<ContractEntity> withOrganizationNumber(final String organizationNumber) {
		if (isBlank(organizationNumber)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.join(STAKEHOLDERS).get(ORGANIZATION_NUMBER), organizationNumber);
	}

	private static Specification<ContractEntity> withLandLeaseType(final String landLeaseType) {
		if (isBlank(landLeaseType)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.get(LAND_LEASE_TYPE), landLeaseType);
	}

	private static Specification<ContractEntity> withEndDate(final LocalDate endDate) {
		if (endDate == null) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.get(END), endDate);
	}

	private static Specification<ContractEntity> withExternalReferenceId(final String externalReferenceId) {
		if (isBlank(externalReferenceId)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.get(EXTERNAL_REFERENCE_ID), externalReferenceId);
	}

	private static Specification<ContractEntity> withPropertyDesignations(final List<String> propertyDesignations) {
		if (isEmpty(propertyDesignations)) {
			return EMPTY;
		}

		return (root, query, cb) -> propertyDesignations.stream()
			.filter(not(String::isBlank))
			.map(propertyDesignation -> cb.isMember(propertyDesignation, root.get(PROPERTY_DESIGNATIONS)))
			.reduce(cb::or)
			.orElse(null);
	}
}