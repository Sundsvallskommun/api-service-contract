package se.sundsvall.contract.api.model;

import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Attachment metadata", accessMode = Schema.AccessMode.READ_WRITE)
public class AttachmentMetaData {

	@Schema(description = "The attachment id", example = "1234", accessMode = Schema.AccessMode.READ_ONLY)
	private Long id;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.AttachmentCategory}
	 */
	@Schema(description = "The attachment category. Possible values: CONTRACT | OTHER", example = "CONTRACT")
	@OneOf({"CONTRACT", "OTHER"})
	private String category;

	@Schema(description = "The attachment filename", example = "LeaseContract12.pdf")
	private String filename;

	@Schema(description = "The attachment mime-type", example = "application/pdf")
	private String mimeType;

	@Schema(description = "Notes on the attachment", example = "The contract was a little wrinkled when scanned")
	private String note;
}
