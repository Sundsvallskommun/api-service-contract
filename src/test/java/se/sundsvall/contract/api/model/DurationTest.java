package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.TimeUnit;

class DurationTest {

	@Test
	void testBean() {
		assertThat(Duration.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var leaseDuration = 3;
		final var unit = TimeUnit.DAYS;

		final var object = Duration.builder()
			.withLeaseDuration(leaseDuration)
			.withUnit(unit)
			.build();

		assertThat(object).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(object.getLeaseDuration()).isEqualTo(leaseDuration);
		assertThat(object.getUnit()).isEqualTo(unit);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Duration.builder().build()).hasAllNullFieldsOrProperties();
	}
}
