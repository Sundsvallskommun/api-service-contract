package se.sundsvall.contract.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.sundsvall.contract.integration.db.model.OutboxEntity;

@CircuitBreaker(name = "outboxRepository")
public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {

	int UNHEALTHY_THRESHOLD = 5;

	@Query("SELECT o FROM OutboxEntity o ORDER BY o.createdAt ASC")
	List<OutboxEntity> findUnsent();

	@Query("SELECT o FROM OutboxEntity o WHERE o.retries >= " + UNHEALTHY_THRESHOLD)
	List<OutboxEntity> findUnhealthy();
}
