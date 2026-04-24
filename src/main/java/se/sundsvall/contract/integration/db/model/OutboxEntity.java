package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;

@Entity
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "outbox",
	indexes = @Index(name = "idx_outbox_retries", columnList = "retries"))
public class OutboxEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "contract_id", length = 10, nullable = false)
	private String contractId;

	@Column(name = "event_type", length = 64, nullable = false)
	private String eventType;

	@Column(name = "payload", columnDefinition = "json", nullable = false)
	private String payload;

	@Builder.Default
	@Column(name = "retries", nullable = false)
	private int retries = 0;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "last_error", length = 512)
	private String lastError;

	@PrePersist
	public void prePersist() {
		createdAt = now();
	}
}
