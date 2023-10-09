package se.sundsvall.contract.integration.db.model;


import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import se.sundsvall.contract.api.model.enums.AttachmentCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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

}
