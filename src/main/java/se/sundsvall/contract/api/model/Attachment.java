package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
@Schema(description = "Attachment")
public class Attachment {

	@Valid
	@Schema(description = "Attachment data, i.e. the file", requiredMode = Schema.RequiredMode.REQUIRED)
	private AttachmentData attachmentData;

	@Valid
	@Schema(description = "Attachment metadata", requiredMode = Schema.RequiredMode.REQUIRED)
	private AttachmentMetaData metaData;
}
