package se.sundsvall.contract.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import se.sundsvall.contract.integration.db.model.ContractEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "contractRepository")
public interface ContractRepository extends JpaRepository<ContractEntity, Long>, JpaSpecificationExecutor<ContractEntity> {

}
