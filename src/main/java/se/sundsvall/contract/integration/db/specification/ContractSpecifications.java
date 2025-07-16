package se.sundsvall.contract.integration.db.specification;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.ADDITIONAL_TERMS;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.CONTRACT_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.END;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.EXTERNAL_REFERENCE_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.INDEX_TERMS;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.LAND_LEASE_TYPE;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.MUNICIPALITY_ID;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.PROPERTY_DESIGNATIONS;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.STAKEHOLDERS;
import static se.sundsvall.contract.integration.db.model.ContractEntity_.VERSION;
import static se.sundsvall.contract.integration.db.model.StakeholderEntity_.ORGANIZATION_NUMBER;
import static se.sundsvall.contract.integration.db.model.StakeholderEntity_.PARTY_ID;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.integration.db.model.ContractEntity;

public final class ContractSpecifications {

	private static final Specification<ContractEntity> EMPTY = Specification.allOf(emptyList());
	private static final String JSON_SEARCH = "JSON_SEARCH";
	private static final String LOWER = "LOWER";
	private static final String HEADER_JSON_PATH = "$[*].header";
	private static final String DESCRIPTION_JSON_PATH = "$[*].terms[*].description";
	private static final String TERM_JSON_PATH = "$[*].terms[*].term";
	private static final String LITERAL_ALL = "all";

	private ContractSpecifications() {}

	public static Specification<ContractEntity> createContractSpecification(final String municipalityId, final ContractRequest request) {
		return withOnlyLatestVersion()
			.and(withMunicipalityId(municipalityId))
			.and(withContractId(request.getContractId()))
			.and(withPartyId(request.getPartyId()))
			.and(withOrganizationNumber(request.getOrganizationNumber()))
			.and(withEndDate(request.getEnd()))
			.and(withLandLeaseType(request.getLandLeaseType()))
			.and(withExternalReferenceId(request.getExternalReferenceId()))
			.and(withPropertyDesignations(request.getPropertyDesignations()))
			.and(withTerm(request.getTerm()));
	}

	public static Specification<ContractEntity> withOnlyLatestVersion() {
		return (root, query, cb) -> {
			final var subQuery = query.subquery(Integer.class);
			final var subRoot = subQuery.from(ContractEntity.class);
			subQuery.select(cb.max(subRoot.get(VERSION)))
				.where(cb.equal(root.get(CONTRACT_ID), subRoot.get(CONTRACT_ID)));
			return cb.equal(root.get(VERSION), subQuery);

		};
	}

	private static Specification<ContractEntity> withMunicipalityId(final String municipalityId) {
		return (root, query, cb) -> cb.equal(root.get(MUNICIPALITY_ID), municipalityId);
	}

	private static Specification<ContractEntity> withContractId(final String contractId) {
		if (isBlank(contractId)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.get(CONTRACT_ID), contractId);
	}

	private static Specification<ContractEntity> withPartyId(final String partyId) {
		if (isBlank(partyId)) {
			return EMPTY;
		}

		return (root, query, cb) -> cb.equal(root.join(STAKEHOLDERS).get(PARTY_ID), partyId);
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

	private static Specification<ContractEntity> withTerm(final String term) {
		if (isBlank(term)) {
			return EMPTY;
		}

		return (root, query, cb) -> {
			final var wildcardSearchTerm = "%" + term.toLowerCase() + "%";

			final var searchPaths = List.of(HEADER_JSON_PATH, DESCRIPTION_JSON_PATH, TERM_JSON_PATH);
			final var jsonColumns = List.of(INDEX_TERMS, ADDITIONAL_TERMS);
			final List<Predicate> predicates = new ArrayList<>();

			// Create a predicate for each search path and json column
			for (final String jsonColumn : jsonColumns) {
				for (final String searchPath : searchPaths) {
					final var predicate = cb.isNotNull(
						cb.function(JSON_SEARCH, String.class, cb.function(LOWER, String.class, root.get(jsonColumn)), cb.literal(LITERAL_ALL), cb.literal(wildcardSearchTerm), cb.literal(null), cb.literal(searchPath)));
					predicates.add(predicate);
				}
			}

			return cb.or(predicates.toArray(new Predicate[0]));
		};
	}
}
