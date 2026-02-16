package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class AttachmentDataTest {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

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

	@Test
	void testInvalidBase64ContentCausesViolation() {
		final var attachmentData = AttachmentData.builder()
			.withContent("this is not valid base64!!!")
			.build();

		final var violations = VALIDATOR.validate(attachmentData);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(v -> assertThat(v.getPropertyPath()).hasToString("content"));
	}

	@Test
	void testValidBase64ContentHasNoViolations() {
		final var attachmentData = AttachmentData.builder()
			.withContent(Base64.getEncoder().encodeToString("valid content".getBytes()))
			.build();

		final var violations = VALIDATOR.validate(attachmentData);

		assertThat(violations).isEmpty();
	}

	@Test
	void testNullBase64ContentCausesViolation() {
		final var attachmentData = AttachmentData.builder()
			.withContent(null)
			.build();

		final var violations = VALIDATOR.validate(attachmentData);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(v -> assertThat(v.getPropertyPath()).hasToString("content"));
	}
}
