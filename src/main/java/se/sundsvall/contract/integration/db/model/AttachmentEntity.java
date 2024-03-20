package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import se.sundsvall.contract.model.enums.AttachmentCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
@Table(name = "attachment")
public class AttachmentEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "contract_id", length = 10, nullable = false)
	private String contractId;

	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private AttachmentCategory category;

	@Column(name = "filename")
	private String filename;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "note")
	private String note;

	@Lob
	@Column(name = "content", columnDefinition = "LONGBLOB")
	private byte[] content;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if (!(o instanceof AttachmentEntity that)) {
            return false;
        }
		return Objects.equals(id, that.id) && Objects.equals(contractId, that.contractId) && category == that.category && Objects.equals(filename, that.filename) && Objects.equals(mimeType, that.mimeType) && Objects.equals(note, that.note) && Arrays.equals(content, that.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, contractId, category, filename, mimeType, note, Arrays.hashCode(content));
	}
}
