package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.TimeUnit;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class ExtensionTest {

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
	void oneOfValuesShouldMatchTimeUnitEnum() throws Exception {
		// Fetch field
		var field = Duration.class.getDeclaredField("unit");
		var annotation = field.getAnnotation(OneOf.class);
		assertThat(annotation).isNotNull();

		// Fetch annotation values
		var annotationValues = Set.of(annotation.value());

		// Fetch enum values
		var enumValues = Arrays.stream(TimeUnit.values())
			.map(Enum::name)
			.collect(toSet());

		// Assert
		assertThat(enumValues).containsExactlyInAnyOrderElementsOf(annotationValues);
	}

	@Test
	void testBuilderMethods() {

		final var autoExtend = true;
		final var leaseExtension = 3;
		final var unit = TimeUnit.DAYS.toString();

		var object = Extension.builder()
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
}
