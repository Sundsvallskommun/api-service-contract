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
		var attachment = Attachment.builder()
			.withAttachmentData(AttachmentData.builder().build())
			.withMetaData(AttachmentMetaData.builder().build())
			.build();

		assertThat(attachment.getAttachmentData()).isNotNull();
		assertThat(attachment.getMetaData()).isNotNull();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Attachment.builder().build()).hasAllNullFieldsOrProperties();
	}
}
