package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.projection.ContractVersionProjection;

/**
 * Repository for {@link ContractEntity} entities.
 */
@CircuitBreaker(name = "contractRepository")
public interface ContractRepository extends JpaRepository<ContractEntity, Long>, JpaSpecificationExecutor<ContractEntity> {

	/**
	 * Finds a contract matching the given municipality, contract id and version.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  version        the contract version
	 * @return                an optional containing the matching entity, or empty if not found
	 */
	Optional<ContractEntity> findByMunicipalityIdAndContractIdAndVersion(String municipalityId, String contractId, Integer version);

	/**
	 * Finds the latest version of a contract matching the given municipality and contract id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                an optional containing the latest version entity, or empty if not found
	 */
	Optional<ContractEntity> findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(String municipalityId, String contractId);

	/**
	 * Checks whether a contract exists for the given municipality and contract id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                true if a matching contract exists, false otherwise
	 */
	boolean existsByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Deletes all contract versions matching the given municipality and contract id.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 */
	void deleteAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Finds all contract version projections matching the given municipality and contract id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  sort           the sort order
	 * @return                list of matching contract version projections
	 */
	List<ContractVersionProjection> findByMunicipalityIdAndContractId(String municipalityId, String contractId, Sort sort);
}
