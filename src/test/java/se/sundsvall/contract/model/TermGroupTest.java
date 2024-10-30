package se.sundsvall.contract.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class TermGroupTest {

	@Test
	void testBean() {
		assertThat(TermGroup.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var header = "term header";
		var terms = List.of(
			Term.builder().build(),
			Term.builder().build());

		var termGroup = TermGroup.builder()
			.withHeader(header)
			.withTerms(terms)
			.build();

		assertThat(termGroup).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(termGroup.getHeader()).isEqualTo(header);
		assertThat(termGroup.getTerms()).isEqualTo(terms);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TermGroup.builder().build()).hasAllNullFieldsOrProperties();
	}
}
