package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Attachment")
public class Attachment {

	@Valid
	@NotNull
	@Schema(description = "Attachment data, i.e. the file", requiredMode = REQUIRED)
	private AttachmentData attachmentData;

	@Valid
	@NotNull
	@Schema(description = "Attachment metadata", requiredMode = REQUIRED)
	private AttachmentMetadata metadata;
}
