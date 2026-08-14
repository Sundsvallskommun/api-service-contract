package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.api.validation.ValidMimeType;
import se.sundsvall.contract.model.enums.AttachmentCategory;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial attachment metadata payload used for PATCH. Only the fields present in the payload are "
	+ "applied to the existing attachment. The binary content cannot be patched.")
public class PatchAttachmentMetadata {

	private AttachmentCategory category;

	@Size(max = 255)
	@Schema(description = "The attachment filename", examples = "LeaseContract12.pdf")
	private String filename;

	@Size(max = 255)
	@ValidMimeType
	@Schema(
		description = "The attachment mime-type, on the form 'type/subtype'. Parameters (e.g. ';charset=utf-8') are not accepted.",
		examples = "application/pdf")
	private String mimeType;

	@Size(max = 255)
	@Schema(description = "Notes on the attachment", examples = "The contract was a little wrinkled when scanned")
	private String note;
}
