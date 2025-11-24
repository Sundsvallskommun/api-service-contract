package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class ParameterTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ParameterTest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void builder() {

		// Arrange
		final var key = "someKey";
		final var displayName = "someDisplayName";
		final var group = "someGroup";
		final var values = List.of("someValue");

		// Act
		final var result = Parameter.builder()
			.withKey(key)
			.withDisplayName(displayName)
			.withGroup(group)
			.withValues(values)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getDisplayName()).isEqualTo(displayName);
		assertThat(result.getGroup()).isEqualTo(group);
		assertThat(result.getValues()).isEqualTo(values);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Parameter.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Parameter()).hasAllNullFieldsOrProperties();
	}

}
