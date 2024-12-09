package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.sundsvall.contract.integration.db.model.ContractEntity;

@CircuitBreaker(name = "contractRepository")
public interface ContractRepository extends JpaRepository<ContractEntity, Long>, JpaSpecificationExecutor<ContractEntity> {

	Optional<ContractEntity> findByMunicipalityIdAndContractIdAndVersion(String municipalityId, String contractId, Integer version);

	Optional<ContractEntity> findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(String municipalityId, String contractId);

	boolean existsByMunicipalityIdAndContractId(String municipalityId, String contractId);

	void deleteAllByMunicipalityIdAndContractId(String municipalityId, String contractId);

	@Query("select c.version from ContractEntity c where c.municipalityId = :municipalityId and c.contractId = :contractId order by c.version asc")
	List<Integer> findAllContractVersionsByMunicipalityIdAndContractId(@Param("municipalityId") String municipalityId, @Param("contractId") String contractId);
}
