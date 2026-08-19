package se.sundsvall.contract.api.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidMimeTypeConstraintValidatorTest {

	private final ValidMimeTypeConstraintValidator validator = new ValidMimeTypeConstraintValidator();

	@ParameterizedTest
	@ValueSource(strings = {
		"application/pdf", "image/jpeg", "text/csv"
	})
	void validValues(final String value) {
		assertThat(validator.isValid(value, null)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"not-a-mime-type", "application/pdf\r\nX-Injected: yes", "*/*", "text/plain;charset=utf-8"
	})
	void invalidValues(final String value) {
		assertThat(validator.isValid(value, null)).isFalse();
	}

	/**
	 * A missing value is @NotBlank's business where one is required, and means "not provided" on a patch payload.
	 */
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"", "   "
	})
	void missingValuesAreLeftToNotBlank(final String value) {
		assertThat(validator.isValid(value, null)).isTrue();
	}
}
