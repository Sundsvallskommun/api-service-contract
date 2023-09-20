package se.sundsvall.contract.api.model;

import se.sundsvall.contract.api.model.enums.AttachmentCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
public class Attachment {

	@Schema(example = "KONTRAKT")
	private AttachmentCategory category;

	@Schema(example = "Arrendekontrakt")
	private String name;

	@Schema(example = "pdf")
	private String extension;

	@Schema(example = "application/pdf")
	private String mimeType;

	@Schema(example = "Kontraktet var lite skrynkligt vid inskanningen.")
	private String note;

	@Schema(format = "byte", description = "Base64-encoded file")
	private String file;

}
