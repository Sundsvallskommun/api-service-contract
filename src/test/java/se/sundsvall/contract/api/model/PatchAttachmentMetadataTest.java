package se.sundsvall.contract.api.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.contract.model.enums.AttachmentCategory;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PatchAttachmentMetadataTest {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void testBean() {
		assertThat(PatchAttachmentMetadata.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var category = AttachmentCategory.OTHER;
		final var filename = "file.pdf";
		final var mimeType = "application/pdf";
		final var note = "aNote";

		final var patch = PatchAttachmentMetadata.builder()
			.withCategory(category)
			.withFilename(filename)
			.withMimeType(mimeType)
			.withNote(note)
			.build();

		assertThat(patch).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(patch.getCategory()).isEqualTo(category);
		assertThat(patch.getFilename()).isEqualTo(filename);
		assertThat(patch.getMimeType()).isEqualTo(mimeType);
		assertThat(patch.getNote()).isEqualTo(note);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PatchAttachmentMetadata.builder().build()).hasAllNullFieldsOrProperties();
	}

	/**
	 * A patch leaves out what it does not change, so an empty payload is a valid one - unlike on
	 * {@link AttachmentMetadata}, where the same fields are required.
	 */
	@Test
	void testEmptyPatchHasNoViolations() {
		assertThat(VALIDATOR.validate(PatchAttachmentMetadata.builder().build())).isEmpty();
	}

	/**
	 * Blank counts as "not provided" here, and is dropped by the mapper rather than rejected.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"", "   "
	})
	void testBlankMimeTypeIsTreatedAsNotProvided(final String mimeType) {
		final var patch = PatchAttachmentMetadata.builder()
			.withMimeType(mimeType)
			.build();

		assertThat(VALIDATOR.validate(patch)).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"not-a-mime-type",
		"application/pdf\r\nX-Injected: yes",
		"application/pdf ",
		"text/plain;charset=utf-8",
		"*/*"
	})
	void testMimeTypeMustBeAWellFormedMimeType(final String invalidValue) {
		final var patch = PatchAttachmentMetadata.builder()
			.withMimeType(invalidValue)
			.build();

		assertThat(VALIDATOR.validate(patch))
			.singleElement()
			.satisfies(violation -> {
				assertThat(violation.getPropertyPath()).hasToString("mimeType");
				assertThat(violation.getMessage()).isEqualTo("must be a valid mime type on the form 'type/subtype', without parameters");
			});
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"filename", "mimeType", "note"
	})
	void testStringFieldsAreCappedAtTheColumnLength(final String field) {
		final var tooLong = "x".repeat(256);
		final var patch = PatchAttachmentMetadata.builder()
			.withFilename("filename".equals(field) ? tooLong : null)
			// A too-long mime type must still be a well-formed one, or the @ValidMimeType violation would carry the test
			.withMimeType("mimeType".equals(field) ? "application/" + "x".repeat(244) : null)
			.withNote("note".equals(field) ? tooLong : null)
			.build();

		assertThat(VALIDATOR.validate(patch))
			.singleElement()
			.satisfies(violation -> {
				assertThat(violation.getPropertyPath()).hasToString(field);
				assertThat(violation.getMessage()).isEqualTo("size must be between 0 and 255");
			});
	}
}
