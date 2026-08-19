package se.sundsvall.contract.api.model;

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
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.contract.model.enums.AttachmentCategory;

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
		final var hash = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
		final var created = OffsetDateTime.now();

		final var attachment = AttachmentMetadata.builder()
			.withFilename(filename)
			.withCategory(category)
			.withMimeType(mimeType)
			.withNote(note)
			.withHash(hash)
			.withCreated(created)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getNote()).isEqualTo(note);
		assertThat(attachment.getHash()).isEqualTo(hash);
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

	/**
	 * The mime type ends up in the Content-Type response header when the attachment is fetched, so it is caught here
	 * rather than left for the servlet container to deal with.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"not-a-mime-type",
		"application/pdf\r\nX-Injected: yes",
		"application/pdf ",
		"application/pdf@evil",
		"text/plain;charset=utf-8",
		"*/*"
	})
	void testMimeTypeMustBeAWellFormedMimeType(final String invalidValue) {
		final var metadata = AttachmentMetadata.builder()
			.withFilename("file.pdf")
			.withMimeType(invalidValue)
			.build();

		final var violations = VALIDATOR.validate(metadata);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(violation -> {
				assertThat(violation.getPropertyPath()).hasToString("mimeType");
				assertThat(violation.getMessage()).isEqualTo("must be a valid mime type on the form 'type/subtype', without parameters");
			});
	}

	/**
	 * The columns are varchar(255), so anything longer would pass validation only to fail as a 500 on insert.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"filename", "mimeType", "note"
	})
	void testStringFieldsAreCappedAtTheColumnLength(final String field) {
		final var tooLong = "x".repeat(256);
		final var metadata = AttachmentMetadata.builder()
			.withFilename("filename".equals(field) ? tooLong : "file.pdf")
			// A too-long mime type must still be a well-formed one, or the @ValidMimeType violation would carry the test
			.withMimeType("mimeType".equals(field) ? "application/" + "x".repeat(244) : "application/pdf")
			.withNote("note".equals(field) ? tooLong : null)
			.build();

		final var violations = VALIDATOR.validate(metadata);

		// The required fields also carry a lower bound, the optional one does not
		final var expectedMessage = "note".equals(field) ? "size must be between 0 and 255" : "size must be between 1 and 255";

		assertThat(violations)
			.singleElement()
			.satisfies(violation -> {
				assertThat(violation.getPropertyPath()).hasToString(field);
				assertThat(violation.getMessage()).isEqualTo(expectedMessage);
			});
	}

	private static Stream<Arguments> blankStringArgumentProvider() {
		return Stream.of(
			Arguments.of("Null value", null),
			Arguments.of("Empty string", ""),
			Arguments.of("Whitespace only", "   "));
	}
}
