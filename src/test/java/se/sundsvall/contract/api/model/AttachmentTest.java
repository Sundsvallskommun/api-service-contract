package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.enums.AttachmentCategory;

class AttachmentTest {
	
	@Test
	void testBean() {
		assertThat(Attachment.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var name = "name";
		final var category = AttachmentCategory.KONTRAKT;
		final var mimeType = "mimeType";
		final var file = "file";
		final var note = "note";
		final var extension = "extension";

		final var attachment = Attachment.builder()
			.withName(name)
			.withCategory(category)
			.withMimeType(mimeType)
			.withFile(file)
			.withNote(note)
			.withExtension(extension)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachment.getName()).isEqualTo(name);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getFile()).isEqualTo(file);
		assertThat(attachment.getNote()).isEqualTo(note);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Attachment.builder().build()).hasAllNullFieldsOrProperties();
	}

}
