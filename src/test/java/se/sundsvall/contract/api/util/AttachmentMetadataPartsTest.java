package se.sundsvall.contract.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class AttachmentMetadataPartsTest {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	private static final String INVALID_JSON_DETAIL = "The 'attachment' part must be valid JSON";

	@Test
	void parse() {
		// Arrange
		final var json = """
			{
				"filename": "LeaseContract12.pdf",
				"mimeType": "application/pdf",
				"category": "CONTRACT",
				"note": "A note"
			}""";

		// Act
		final var result = AttachmentMetadataParts.parse(OBJECT_MAPPER, json);

		// Assert
		assertThat(result).isNotNull()
			.extracting(
				AttachmentMetadata::getFilename,
				AttachmentMetadata::getMimeType,
				AttachmentMetadata::getCategory,
				AttachmentMetadata::getNote)
			.containsExactly("LeaseContract12.pdf", "application/pdf", AttachmentCategory.CONTRACT, "A note");
	}

	@Test
	void parse_shouldThrowBadRequest_whenJsonIsMalformed() {
		// Arrange
		final var json = "{\"filename\": ";

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> AttachmentMetadataParts.parse(OBJECT_MAPPER, json))
			.satisfies(problem -> {
				assertThat(problem.getStatus()).isEqualTo(BAD_REQUEST);
				assertThat(problem.getDetail()).isEqualTo(INVALID_JSON_DETAIL);
			});
	}

	@Test
	void parse_shouldThrowBadRequest_whenCategoryIsUnknown() {
		// Arrange
		final var json = """
			{
				"filename": "LeaseContract12.pdf",
				"mimeType": "application/pdf",
				"category": "NOT_A_CATEGORY"
			}""";

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> AttachmentMetadataParts.parse(OBJECT_MAPPER, json))
			.satisfies(problem -> {
				assertThat(problem.getStatus()).isEqualTo(BAD_REQUEST);
				assertThat(problem.getDetail()).isEqualTo(INVALID_JSON_DETAIL);
			});
	}

	/**
	 * The parser message must never be echoed back, since it quotes the submitted payload.
	 */
	@Test
	void parse_shouldNotReflectSubmittedPayload_whenJsonIsMalformed() {
		// Arrange
		final var json = "{\"filename\": \"secret-payload-marker\", ";

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> AttachmentMetadataParts.parse(OBJECT_MAPPER, json))
			.satisfies(problem -> {
				assertThat(problem.getDetail()).doesNotContain("secret-payload-marker");
				assertThat(problem.getMessage()).doesNotContain("secret-payload-marker");
			});
	}

	@Test
	void validate() {
		// Arrange
		final var metadata = AttachmentMetadata.builder()
			.withFilename("LeaseContract12.pdf")
			.withMimeType("application/pdf")
			.build();

		// Act & Assert
		assertThatNoException().isThrownBy(() -> AttachmentMetadataParts.validate(VALIDATOR, metadata));
	}

	@Test
	void validate_shouldThrowConstraintViolationException_whenRequiredValuesAreMissing() {
		// Arrange
		final var metadata = AttachmentMetadata.builder()
			.withFilename(" ")
			.build();

		// Act & Assert
		assertThatExceptionOfType(ConstraintViolationException.class)
			.isThrownBy(() -> AttachmentMetadataParts.validate(VALIDATOR, metadata))
			.satisfies(exception -> assertThat(exception.getConstraintViolations())
				.extracting(violation -> violation.getPropertyPath().toString(), violation -> violation.getMessage())
				.containsExactlyInAnyOrder(
					tuple("filename", "must not be blank"),
					tuple("mimeType", "must not be blank")));
	}
}
