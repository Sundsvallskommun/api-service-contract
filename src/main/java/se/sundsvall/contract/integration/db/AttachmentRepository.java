package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;

@CircuitBreaker(name = "attachmentRepository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

	List<AttachmentEntity> findAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	Optional<AttachmentEntity> findByMunicipalityIdAndContractIdAndId(String municipalityId, String contractId, Long id);

	boolean existsByMunicipalityIdAndContractIdAndId(String municipalityId, String contractId, Long id);

	void deleteAllByContractId(String contractId);

	void deleteByMunicipalityIdAndContractIdAndId(String municipalityId, String contractId, Long id);
}
