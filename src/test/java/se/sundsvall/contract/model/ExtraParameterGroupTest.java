package se.sundsvall.contract.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.Address;

class ExtraParameterGroupTest {

	@Test
	void testBean() {
		assertThat(ExtraParameterGroup.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var name = "Test";
		var parameters = Map.of("someKey", "someValue");

		var extraParameterGroup = ExtraParameterGroup.builder()
			.withName(name)
			.withParameters(parameters)
			.build();

		assertThat(extraParameterGroup.getName()).isEqualTo(name);
		assertThat(extraParameterGroup.getParameters()).isEqualTo(parameters);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		Assertions.assertThat(Address.builder().build()).hasAllNullFieldsOrProperties();
	}
}
