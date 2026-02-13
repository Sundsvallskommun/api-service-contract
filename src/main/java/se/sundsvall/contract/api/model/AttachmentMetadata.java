package se.sundsvall.contract.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
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
@Schema(description = "Attachment metadata", accessMode = READ_WRITE)
public class AttachmentMetadata {

	@Schema(description = "The attachment id", examples = "1234", accessMode = READ_ONLY)
	private Long id;

	private AttachmentCategory category;

	@NotBlank
	@Schema(description = "The attachment filename", examples = "LeaseContract12.pdf", requiredMode = REQUIRED)
	private String filename;

	@NotBlank
	@Schema(description = "The attachment mime-type", examples = "application/pdf", requiredMode = REQUIRED)
	private String mimeType;

	@Schema(description = "Notes on the attachment", examples = "The contract was a little wrinkled when scanned")
	private String note;

	@Schema(description = "Date when the attachment was created", examples = "2023-01-01T00:00:00.000Z", accessMode = READ_ONLY)
	private OffsetDateTime created;
}
