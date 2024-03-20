package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

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
	void testAddress_category_hasValidOneOfValues() throws NoSuchFieldException {
		var oneOf = Attachment.class.getDeclaredField("category")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(AttachmentCategory.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}

	@Test
	void testBuilderMethods() {
		var filename = "filename";
		var category = AttachmentCategory.CONTRACT;
		var mimeType = "mimeType";
		var file = "file";
		var note = "note";

		var attachment = Attachment.builder()
			.withFilename(filename)
			.withCategory(category.name())
			.withMimeType(mimeType)
			.withContent(file)
			.withNote(note)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category.name());
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getContent()).isEqualTo(file);
		assertThat(attachment.getNote()).isEqualTo(note);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Attachment.builder().build()).hasAllNullFieldsOrProperties();
	}

}
