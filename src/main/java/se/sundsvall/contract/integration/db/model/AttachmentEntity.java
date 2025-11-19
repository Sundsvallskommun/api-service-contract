package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.integration.db.model.converter.enums.AttachmentCategoryConverter;
import se.sundsvall.contract.model.enums.AttachmentCategory;

@Entity
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Table(name = "attachment")
public class AttachmentEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "contract_id", length = 10, nullable = false)
	private String contractId;

	@Column(name = "municipality_id", length = 4)
	private String municipalityId;

	@Column(name = "category")
	@Convert(converter = AttachmentCategoryConverter.class)
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
		return Objects.equals(id, that.id) && Objects.equals(contractId, that.contractId) && Objects.equals(municipalityId, that.municipalityId) && category == that.category && Objects.equals(filename, that.filename) && Objects.equals(mimeType,
			that.mimeType) && Objects.equals(note, that.note) && Objects.deepEquals(content, that.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, contractId, municipalityId, category, filename, mimeType, note, Arrays.hashCode(content));
	}
}
