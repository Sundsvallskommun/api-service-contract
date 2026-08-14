package se.sundsvall.contract.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a concrete mime type on the form {@code type/subtype}, without parameters.
 *
 * <p>
 * Null and blank are considered valid - combine with {@code @NotBlank} where a value is required. On a patch payload,
 * where a missing value simply means "not provided", the annotation stands on its own.
 */
@Documented
@Target({
	FIELD, PARAMETER, ANNOTATION_TYPE
})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidMimeTypeConstraintValidator.class)
public @interface ValidMimeType {

	String message() default "must be a valid mime type on the form 'type/subtype', without parameters";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
