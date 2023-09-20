package se.sundsvall.contract.api.model.enums;

import lombok.Getter;

// ByggR - Handlingstyper
@Getter
public enum AttachmentCategory {

	KONTRAKT("Kontrakt"), OTHER("Övrigt");

	private final String fileCategory;

	AttachmentCategory(final String fileCategory) {
		this.fileCategory = fileCategory;
	}

}
