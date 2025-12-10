package se.sundsvall.contract.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.AttachmentCategory;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Attachment metadata", accessMode = Schema.AccessMode.READ_WRITE)
public class AttachmentMetadata {

	@Schema(description = "The attachment id", examples = "1234", accessMode = READ_ONLY)
	private Long id;

	private AttachmentCategory category;

	@Schema(description = "The attachment filename", examples = "LeaseContract12.pdf")
	private String filename;

	@Schema(description = "The attachment mime-type", examples = "application/pdf")
	private String mimeType;

	@Schema(description = "Notes on the attachment", examples = "The contract was a little wrinkled when scanned")
	private String note;
}
