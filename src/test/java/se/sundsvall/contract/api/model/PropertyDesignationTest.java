package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PropertyDesignationTest {

	@Test
	void testBean() {
		assertThat(PropertyDesignation.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var name = "name";
		final var district = "district";

		final var propertyDesignation = PropertyDesignation.builder()
			.withName(name)
			.withDistrict(district)
			.build();

		Assertions.assertThat(propertyDesignation).isNotNull().hasNoNullFieldsOrProperties();
		Assertions.assertThat(propertyDesignation.getName()).isEqualTo(name);
		Assertions.assertThat(propertyDesignation.getDistrict()).isEqualTo(district);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		Assertions.assertThat(PropertyDesignation.builder().build()).hasAllNullFieldsOrProperties();
	}
}
