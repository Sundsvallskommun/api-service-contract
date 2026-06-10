package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.Status;

/**
 * Repository for {@link ContractEntity} entities.
 */
@CircuitBreaker(name = "contractRepository")
public interface ContractRepository extends JpaRepository<ContractEntity, Long>, JpaSpecificationExecutor<ContractEntity> {

	/**
	 * Finds the contract matching the given municipality and contract id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                an optional containing the matching entity, or empty if not found
	 */
	Optional<ContractEntity> findByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Checks whether a contract exists for the given municipality and contract id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                true if a matching contract exists, false otherwise
	 */
	boolean existsByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Deletes the contract matching the given municipality and contract id.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 */
	void deleteAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Finds all contracts matching the given status where end date is before the given date.
	 * <p>
	 * <b>Intentionally cross-tenant:</b> this query is not scoped by {@code municipalityId}
	 * because it is only invoked by the contract-termination scheduler ({@code ContractTerminationJob}),
	 * which must sweep expired contracts across every municipality on every run. Do not call
	 * this from request-scoped code paths — those must filter by {@code municipalityId}
	 * to preserve tenant isolation.
	 *
	 * @param  status  the contract status to filter on
	 * @param  endDate the date to compare end date against
	 * @return         list of matching contracts across all municipalities
	 */
	List<ContractEntity> findByStatusAndEndDateBefore(Status status, LocalDate endDate);

	/**
	 * Finds all contracts matching the given status where auto-extend is disabled and the current
	 * period end date is on or before the given date.
	 * <p>
	 * <b>Intentionally cross-tenant:</b> only invoked by {@code ContractTerminationJob}.
	 * Do not call from request-scoped code paths.
	 *
	 * @param  status               the contract status to filter on
	 * @param  currentPeriodEndDate the date to compare current period end date against (inclusive)
	 * @return                      list of matching contracts across all municipalities
	 */
	List<ContractEntity> findByStatusAndAutoExtendFalseAndCurrentPeriodEndDateLessThanEqual(Status status, LocalDate currentPeriodEndDate);

	/**
	 * Finds all contracts matching the given status where auto-extend is enabled and the current
	 * period end date is on or before the given date.
	 * <p>
	 * <b>Intentionally cross-tenant:</b> only invoked by {@code ContractAutoExtensionJob}.
	 * Do not call from request-scoped code paths.
	 *
	 * @param  status               the contract status to filter on
	 * @param  currentPeriodEndDate the date to compare current period end date against (inclusive)
	 * @return                      list of matching contracts across all municipalities
	 */
	List<ContractEntity> findByStatusAndAutoExtendTrueAndCurrentPeriodEndDateLessThanEqual(Status status, LocalDate currentPeriodEndDate);
}
