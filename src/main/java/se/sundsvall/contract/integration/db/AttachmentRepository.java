package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;

/**
 * Repository for {@link AttachmentEntity} entities.
 */
@CircuitBreaker(name = "attachmentRepository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

	/**
	 * Finds all attachments matching the given municipality and contract.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @return                list of matching attachment entities
	 */
	List<AttachmentEntity> findAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Finds an attachment matching the given municipality, contract and attachment id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  id             the attachment id
	 * @return                an optional containing the matching entity, or empty if not found
	 */
	Optional<AttachmentEntity> findByMunicipalityIdAndContractIdAndId(String municipalityId, String contractId, Long id);

	/**
	 * Checks whether an attachment exists for the given municipality, contract and attachment id.
	 *
	 * @param  municipalityId the municipality id
	 * @param  contractId     the contract id
	 * @param  id             the attachment id
	 * @return                true if a matching attachment exists, false otherwise
	 */
	boolean existsByMunicipalityIdAndContractIdAndId(String municipalityId, String contractId, Long id);

	/**
	 * Deletes all attachments matching the given municipality and contract.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 */
	void deleteAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	/**
	 * Deletes an attachment matching the given municipality, contract and attachment id.
	 *
	 * @param municipalityId the municipality id
	 * @param contractId     the contract id
	 * @param id             the attachment id
	 */
	void deleteByMunicipalityIdAndContractIdAndId(String municipalityId, String contractId, Long id);
}
