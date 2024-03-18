package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Objects;

import se.sundsvall.contract.model.enums.AttachmentCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private AttachmentCategory category;

	@Column(name = "name")
	private String name;

	@Column(name = "extension")
	private String extension;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "note")
	private String note;

	@Column(name = "file")
	private String file;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AttachmentEntity that)) return false;
		return Objects.equals(id, that.id) && category == that.category && Objects.equals(name, that.name) && Objects.equals(extension, that.extension) && Objects.equals(mimeType, that.mimeType) && Objects.equals(note, that.note) && Objects.equals(file, that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, category, name, extension, mimeType, note, file);
	}
}
