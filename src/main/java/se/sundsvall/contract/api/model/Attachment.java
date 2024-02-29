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

	/**
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.AttachmentCategory}
	 * */
	@Schema(description = "AttachmentCategory, possible values: CONTRACT | OTHER", example = "CONTRACT")
	@OneOf({"CONTRACT", "OTHER"})
	private String category;

	@Schema(description = "Name of the attachment", example = "LeaseContract12")
	private String name;

	@Schema(description = "file extension", example = ".pdf")
	private String extension;

	@Schema(description = "mimeType", example = "application/pdf")
	private String mimeType;

	@Schema(description = "Notes about the attachment", example = "The contract was a little wrinkled when scanned")
	private String note;

	@ValidBase64(nullable = true)
	@Schema(type = "string", format = "base64", description = "Base64-encoded file")
	private String file;

}
