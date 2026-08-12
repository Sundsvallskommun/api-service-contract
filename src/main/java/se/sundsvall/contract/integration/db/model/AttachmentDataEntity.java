package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.sql.Blob;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * The binary content of an attachment, kept in its own table so that reading attachment metadata never touches the
 * blob.
 *
 * <p>
 * Splitting it out is what makes the content genuinely lazy: {@link AttachmentEntity} references this through a lazy
 * {@code @OneToOne}, and association laziness is proxy-based and works without Hibernate bytecode enhancement - unlike
 * a lazy basic attribute, which is silently ignored without it.
 */
@Entity
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Table(name = "attachment_data")
public class AttachmentDataEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	/**
	 * Mandatory: a data row exists only to carry content, so a null file would be a row with no reason to exist - and one
	 * that the streaming endpoint would have to guard against on every read.
	 */
	@ToString.Exclude
	@Lob
	@Column(name = "file", columnDefinition = "LONGBLOB", nullable = false)
	private Blob file;

	/**
	 * The file is deliberately excluded: a {@link Blob} is a locator, not a value, and has no meaningful equality. Two
	 * rows are compared by id only - the content itself is compared through {@code AttachmentEntity.hash}.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AttachmentDataEntity that)) {
			return false;
		}
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
