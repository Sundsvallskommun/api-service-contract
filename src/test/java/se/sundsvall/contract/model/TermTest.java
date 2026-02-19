package se.sundsvall.contract.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class TermTest {

	@Test
	void testBean() {
		assertThat(Term.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var termName = "some term";
		var termDescription = "some term description";

		var term = Term.builder()
			.withName(termName)
			.withDescription(termDescription)
			.build();

		assertThat(term).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(term.getName()).isEqualTo(termName);
		assertThat(term.getDescription()).isEqualTo(termDescription);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Term.builder().build()).hasAllNullFieldsOrProperties();
	}
}
