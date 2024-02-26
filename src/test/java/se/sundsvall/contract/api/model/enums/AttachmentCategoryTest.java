package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttachmentCategoryTest {

	@Test
	void enums() {
		assertThat(AttachmentCategory.values()).containsExactlyInAnyOrder(AttachmentCategory.CONTRACT, AttachmentCategory.OTHER);
	}

	@Test
	void enumValues() {
		assertThat(AttachmentCategory.CONTRACT).hasToString("CONTRACT");
		assertThat(AttachmentCategory.OTHER).hasToString("OTHER");
	}
}
