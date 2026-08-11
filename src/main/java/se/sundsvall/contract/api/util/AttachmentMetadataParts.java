package se.sundsvall.contract.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Handling of the JSON metadata part of a multipart attachment upload. Bean validation of a {@code @RequestPart} String
 * is not performed by the framework, so the parsed metadata has to be validated explicitly.
 */
public final class AttachmentMetadataParts {

	private AttachmentMetadataParts() {}

	/**
	 * Parses the 'attachment' metadata part. Without this, a Jackson parse failure would escape as an unhandled exception
	 * and be reported as a server error - unparsable input from a client is a bad request. The parser message is
	 * deliberately not echoed back, to avoid reflecting the submitted payload in the response.
	 *
	 * @param  objectMapper the object mapper to parse with
	 * @param  attachment   the raw JSON metadata part
	 * @return              the parsed metadata
	 */
	public static AttachmentMetadata parse(final ObjectMapper objectMapper, final String attachment) {
		try {
			return objectMapper.readValue(attachment, AttachmentMetadata.class);
		} catch (final JsonProcessingException e) {
			throw Problem.valueOf(BAD_REQUEST, "The 'attachment' part must be valid JSON");
		}
	}

	/**
	 * Validates the parsed metadata and reports any violations the same way the framework does for a validated request
	 * body.
	 *
	 * <p>
	 * The violations are deliberately not ordered here: {@link ConstraintViolationException} copies them into a
	 * {@code HashSet}, so any ordering imposed by this method is discarded before the exception handler ever sees it.
	 *
	 * @param validator the validator to validate with
	 * @param object    the object to validate
	 */
	public static <T> void validate(final Validator validator, final T object) {
		final Set<ConstraintViolation<T>> violations = validator.validate(object);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}
}
