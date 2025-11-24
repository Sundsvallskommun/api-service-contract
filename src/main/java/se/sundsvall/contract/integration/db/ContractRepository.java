package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.projection.ContractVersionProjection;

@CircuitBreaker(name = "contractRepository")
public interface ContractRepository extends JpaRepository<ContractEntity, Long>, JpaSpecificationExecutor<ContractEntity> {

	Optional<ContractEntity> findByMunicipalityIdAndContractIdAndVersion(String municipalityId, String contractId, Integer version);

	Optional<ContractEntity> findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(String municipalityId, String contractId);

	boolean existsByMunicipalityIdAndContractId(String municipalityId, String contractId);

	void deleteAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	List<ContractVersionProjection> findByMunicipalityIdAndContractId(String municipalityId, String contractId, Sort sort);
}
