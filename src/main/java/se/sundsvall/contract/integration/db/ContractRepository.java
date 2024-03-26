package se.sundsvall.contract.integration.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import se.sundsvall.contract.integration.db.model.ContractEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "contractRepository")
public interface ContractRepository extends JpaRepository<ContractEntity, Long>, JpaSpecificationExecutor<ContractEntity> {

	Optional<ContractEntity> findByMunicipalityIdAndContractId(String municipalityId, String contractId);
	Optional<ContractEntity> findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(String municipalityId, String contractId);
	boolean existsByMunicipalityIdAndContractId(String municipalityId, String contractId);
	void deleteAllByMunicipalityIdAndContractId(String municipalityId, String contractId);
}
