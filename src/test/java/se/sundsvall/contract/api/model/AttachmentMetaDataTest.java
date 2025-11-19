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
import se.sundsvall.contract.model.enums.AttachmentCategory;

class AttachmentMetaDataTest {

	@Test
	void testBean() {
		assertThat(AttachmentMetaData.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var filename = "filename";
		var category = AttachmentCategory.CONTRACT;
		var mimeType = "mimeType";
		var note = "note";

		var attachment = AttachmentMetaData.builder()
			.withFilename(filename)
			.withCategory(category)
			.withMimeType(mimeType)
			.withNote(note)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getNote()).isEqualTo(note);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentMetaData.builder().build()).hasAllNullFieldsOrProperties();
	}
}
