package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.contract.model.enums.AttachmentCategory;

class AttachmentMetadataTest {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(AttachmentMetadata.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var filename = "filename";
		final var category = AttachmentCategory.CONTRACT;
		final var mimeType = "mimeType";
		final var note = "note";
		final var created = OffsetDateTime.now();

		final var attachment = AttachmentMetadata.builder()
			.withFilename(filename)
			.withCategory(category)
			.withMimeType(mimeType)
			.withNote(note)
			.withCreated(created)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getNote()).isEqualTo(note);
		assertThat(attachment.getCreated()).isEqualTo(created);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentMetadata.builder().build()).hasAllNullFieldsOrProperties();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("blankStringArgumentProvider")
	void testFilenameMustNotBeBlank(String description, String invalidValue) {
		final var metadata = AttachmentMetadata.builder()
			.withFilename(invalidValue)
			.withMimeType("application/pdf")
			.build();

		final var violations = VALIDATOR.validate(metadata);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(v -> assertThat(v.getPropertyPath()).hasToString("filename"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("blankStringArgumentProvider")
	void testMimeTypeMustNotBeBlank(String description, String invalidValue) {
		final var metadata = AttachmentMetadata.builder()
			.withFilename("file.pdf")
			.withMimeType(invalidValue)
			.build();

		final var violations = VALIDATOR.validate(metadata);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(v -> assertThat(v.getPropertyPath()).hasToString("mimeType"));
	}

	@Test
	void testValidMetadataHasNoViolations() {
		final var metadata = AttachmentMetadata.builder()
			.withFilename("file.pdf")
			.withMimeType("application/pdf")
			.build();

		final var violations = VALIDATOR.validate(metadata);

		assertThat(violations).isEmpty();
	}

	private static Stream<Arguments> blankStringArgumentProvider() {
		return Stream.of(
			Arguments.of("Null value", null),
			Arguments.of("Empty string", ""),
			Arguments.of("Whitespace only", "   "));
	}
}
