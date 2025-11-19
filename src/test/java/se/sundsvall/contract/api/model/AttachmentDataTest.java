package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

import org.junit.jupiter.api.Test;

class AttachmentDataTest {

	@Test
	void testBean() {
		assertThat(AttachmentData.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var content = "base64Content";
		final var attachmentData = AttachmentData.builder()
			.withContent(content)
			.build();

		assertThat(attachmentData.getContent()).isNotNull();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentData.builder().build()).hasAllNullFieldsOrProperties();
	}
}
