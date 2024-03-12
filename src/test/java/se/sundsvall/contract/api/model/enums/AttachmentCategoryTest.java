package se.sundsvall.contract.api.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.api.model.enums.AttachmentCategory.CONTRACT;
import static se.sundsvall.contract.api.model.enums.AttachmentCategory.OTHER;

import org.junit.jupiter.api.Test;

class AttachmentCategoryTest {

	@Test
	void enums() {
		assertThat(AttachmentCategory.values()).containsExactlyInAnyOrder(CONTRACT, OTHER);
	}

	@Test
	void enumValues() {
		assertThat(CONTRACT).hasToString("CONTRACT");
		assertThat(OTHER).hasToString("OTHER");
	}
}
