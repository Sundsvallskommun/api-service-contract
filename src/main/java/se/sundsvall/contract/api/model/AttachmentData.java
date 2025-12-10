package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.dept44.common.validators.annotation.ValidBase64;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Attachment content")
public class AttachmentData {

	@ValidBase64
	@Schema(description = "BASE64-encoded attachment file content", examples = "QkFTRTY0LWVuY29kZWQgZGF0YQ==", format = "base64")
	private String content;
}
