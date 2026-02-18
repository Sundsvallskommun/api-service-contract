package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.contract.model.enums.TimeUnit;

class ExtensionTest {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void testBean() {
		assertThat(Extension.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var autoExtend = true;
		final var leaseExtension = 3;
		final var unit = TimeUnit.DAYS;

		final var object = Extension.builder()
			.withAutoExtend(autoExtend)
			.withLeaseExtension(leaseExtension)
			.withUnit(unit)
			.build();

		assertThat(object).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(object.getLeaseExtension()).isEqualTo(leaseExtension);
		assertThat(object.getUnit()).isEqualTo(unit);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Extension.builder().build()).hasAllNullFieldsOrProperties();
	}

	@Test
	void hasValidExtensionPropertiesReturnsFalse() {

		assertThat(Extension.builder()
			.withAutoExtend(true)
			.withLeaseExtension(null)
			.withUnit(TimeUnit.DAYS)
			.build()
			.hasValidExtensionProperties()).isFalse();

		assertThat(Extension.builder()
			.withAutoExtend(true)
			.withLeaseExtension(3)
			.withUnit(null)
			.build()
			.hasValidExtensionProperties()).isFalse();

		assertThat(Extension.builder()
			.withAutoExtend(true)
			.withLeaseExtension(null)
			.withUnit(null)
			.build()
			.hasValidExtensionProperties()).isFalse();
	}

	@Test
	void hasValidExtensionPropertiesReturnsTrue() {

		assertThat(Extension.builder()
			.withAutoExtend(null)
			.withLeaseExtension(null)
			.withUnit(null)
			.build()
			.hasValidExtensionProperties()).isTrue();

		assertThat(Extension.builder()
			.withAutoExtend(false)
			.withLeaseExtension(null)
			.withUnit(null)
			.build()
			.hasValidExtensionProperties()).isTrue();

		assertThat(Extension.builder()
			.withAutoExtend(false)
			.withLeaseExtension(3)
			.withUnit(null)
			.build()
			.hasValidExtensionProperties()).isTrue();

		assertThat(Extension.builder()
			.withAutoExtend(false)
			.withLeaseExtension(null)
			.withUnit(TimeUnit.DAYS)
			.build()
			.hasValidExtensionProperties()).isTrue();
	}

	@ParameterizedTest
	@ValueSource(ints = {
		-1, -100
	})
	void leaseExtensionMustBeZeroOrPositive(int invalidValue) {
		final var extension = Extension.builder()
			.withAutoExtend(true)
			.withLeaseExtension(invalidValue)
			.withUnit(TimeUnit.DAYS)
			.build();

		final var violations = VALIDATOR.validate(extension);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(v -> assertThat(v.getPropertyPath()).hasToString("leaseExtension"));
	}
}
