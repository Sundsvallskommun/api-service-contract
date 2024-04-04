package se.sundsvall.contract.api.model;

import se.sundsvall.dept44.common.validators.annotation.ValidBase64;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
@Schema(description = "Attachment content")
public class AttachmentData {

	@ValidBase64
	@Schema(description = "BASE64-encoded attachment file content", example = "<BASE64-encoded data>", format = "base64")
	private String content;
}
