package se.sundsvall.contract.integration.db.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class PropertyDesignationEmbeddableTest {

	@Test
	void testBean() {
		assertThat(PropertyDesignationEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var name = "name";
		var district = "district";
		var propertyDesignation = PropertyDesignationEmbeddable.builder()
			.withName(name)
			.withDistrict(district)
			.build();

		Assertions.assertThat(propertyDesignation).isNotNull().hasNoNullFieldsOrProperties();
		Assertions.assertThat(propertyDesignation.getName()).isEqualTo(name);
		Assertions.assertThat(propertyDesignation.getDistrict()).isEqualTo(district);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		Assertions.assertThat(PropertyDesignationEmbeddable.builder().build()).hasAllNullFieldsOrProperties();
	}
}
