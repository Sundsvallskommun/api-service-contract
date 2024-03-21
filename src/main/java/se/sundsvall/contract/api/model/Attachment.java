package se.sundsvall.contract.api.model;

import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.dept44.common.validators.annotation.ValidBase64;

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

	@ValidBase64(nullable = true)
	@Schema(description = "BASE64-encoded attachment file content", example = "<BASE64-encoded data>", type = "string", format = "base64", accessMode = Schema.AccessMode.WRITE_ONLY)
	private String content;
}
