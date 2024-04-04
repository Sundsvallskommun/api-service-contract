package se.sundsvall.contract.integration.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.contract.integration.db.model.AttachmentEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "attachmentRepository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

    List<AttachmentEntity> findAllByContractId(String contractId);
    void deleteAllByContractId(String contractId);
	Optional<AttachmentEntity> findByContractIdAndId(String contractId, Long id);
	void deleteByContractIdAndId(String contractId, Long id);
}
