package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

// ByggR - Handlingstyper
@Schema(description = "Attachment category", enumAsRef = true)
public enum AttachmentCategory {
	CONTRACT,
	OTHER
}
