package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.api.model.enums.AttachmentCategory.CONTRACT;

import org.junit.jupiter.api.Test;

class AttachmentEntityTest {

	@Test
	void testBean() {
		assertThat(AttachmentEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var id = 1L;
		final var name = "name";
		final var category = CONTRACT;
		final var mimeType = "mimeType";
		final var file = "file";
		final var note = "note";
		final var extension = "extension";

		final var attachment = AttachmentEntity.builder()
			.withId(id)
			.withName(name)
			.withCategory(category)
			.withMimeType(mimeType)
			.withFile(file)
			.withNote(note)
			.withExtension(extension)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachment.getId()).isEqualTo(id);
		assertThat(attachment.getName()).isEqualTo(name);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getFile()).isEqualTo(file);
		assertThat(attachment.getNote()).isEqualTo(note);
		assertThat(attachment.getExtension()).isEqualTo(extension);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
