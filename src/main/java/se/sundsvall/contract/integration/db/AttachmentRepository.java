package se.sundsvall.contract.integration.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.contract.integration.db.model.AttachmentEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "attachmentRepository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

    List<AttachmentEntity> findAllByContractId(String contractId);

    void deleteAllByContractId(String contractId);
}
