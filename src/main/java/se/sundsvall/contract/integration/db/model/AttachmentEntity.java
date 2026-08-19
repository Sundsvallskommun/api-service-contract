package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import se.sundsvall.contract.model.enums.AttachmentCategory;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.OffsetDateTime.now;

@Entity
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Table(name = "attachment",
	indexes = @Index(name = "idx_attachment_municipality_id_contract_id", columnList = "municipality_id, contract_id"),
	uniqueConstraints = @UniqueConstraint(name = "uk_attachment_attachment_data_id", columnNames = "attachment_data_id"))
public class AttachmentEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "contract_id", length = 11, nullable = false)
	private String contractId;

	@Column(name = "municipality_id", length = 4)
	private String municipalityId;

	@Column(name = "category", length = 64)
	private AttachmentCategory category;

	@Column(name = "filename")
	private String filename;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "note")
	private String note;

	@Column(name = "created")
	private OffsetDateTime created;

	/**
	 * The binary content, in its own table behind a lazy association so that it is fetched only when the file is actually
	 * streamed - never when reading metadata. Cascading and orphan removal tie the data row's lifecycle to the
	 * attachment, so deleting an attachment takes its content with it.
	 *
	 * <p>
	 * Mandatory in both directions: an attachment without content is not a meaningful record, and leaving the join column
	 * nullable would make that broken state representable and force every reader to guard against it. Laziness is
	 * unaffected - the foreign key sits on this side, so Hibernate knows whether there is anything to load without
	 * fetching it.
	 */
	@ToString.Exclude
	@OneToOne(cascade = ALL, fetch = LAZY, orphanRemoval = true, optional = false)
	@JoinColumn(
		name = "attachment_data_id",
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_attachment_data_attachment"))
	private AttachmentDataEntity attachmentData;

	/**
	 * Lower-case hex encoded SHA-256 of the content, derived by the service on upload via dept44's {@code HashUtils}. It
	 * is not derived here, since that would mean pulling the whole blob back through the lazy association the split is
	 * meant to avoid.
	 */
	@Column(name = "hash", length = 64)
	private String hash;

	@PrePersist
	public void prePersist() {
		created = now(ZoneId.systemDefault());
	}

	/**
	 * The content association is deliberately excluded: comparing it would initialize the lazy association, which is
	 * exactly what holding the content in a separate table avoids, and a blob has no meaningful value equality anyway.
	 * The hash stands in for it - two attachments with the same hash have the same bytes.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AttachmentEntity that)) {
			return false;
		}
		return Objects.equals(id, that.id) && Objects.equals(contractId, that.contractId) && Objects.equals(municipalityId, that.municipalityId) && category == that.category && Objects.equals(filename, that.filename) && Objects.equals(mimeType,
			that.mimeType) && Objects.equals(note, that.note) && Objects.equals(created, that.created) && Objects.equals(hash, that.hash);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, contractId, municipalityId, category, filename, mimeType, note, created, hash);
	}
}
