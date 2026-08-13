package se.sundsvall.contract.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import se.sundsvall.contract.model.MimeTypes;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ValidMimeTypeConstraintValidator implements ConstraintValidator<ValidMimeType, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		// A missing value is deliberately left to @NotBlank, as the bean-validation built-ins do, so that a missing value
		// and a malformed one produce one violation each rather than two on the same field. Blank counts as missing here:
		// on a patch payload it means "not provided", which is what the mapper's blank-to-null normalization treats it as.
		return isBlank(value) || MimeTypes.isValid(value);
	}
}
