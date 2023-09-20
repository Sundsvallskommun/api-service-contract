package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttachmentCategoryTest {

	@Test
	void enums() {
		assertThat(AttachmentCategory.values()).containsExactlyInAnyOrder(AttachmentCategory.KONTRAKT, AttachmentCategory.OTHER);
	}

	@Test
	void enumValues() {
		assertThat(AttachmentCategory.KONTRAKT).hasToString("KONTRAKT");
		assertThat(AttachmentCategory.OTHER).hasToString("OTHER");
	}

	@Test
	void enumTextValues() {
		assertThat(AttachmentCategory.KONTRAKT.getFileCategory()).isEqualTo("Kontrakt");
		assertThat(AttachmentCategory.OTHER.getFileCategory()).isEqualTo("Övrigt");
	}

}
