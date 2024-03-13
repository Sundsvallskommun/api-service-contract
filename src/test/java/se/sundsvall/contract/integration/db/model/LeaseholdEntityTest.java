package se.sundsvall.contract.integration.db.model;

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

import se.sundsvall.contract.api.model.enums.LeaseholdType;

class LeaseholdEntityTest {

	@Test
	void testBean() {
		assertThat(LeaseholdEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var type = LeaseholdType.APARTMENT;
		final var description = "description";
		final var leasehold = LeaseholdEntity.builder()
			.withPurpose(type)
			.withAdditionalInformation(List.of("info1", "info2"))
			.withDescription(description)
			.build();

		assertThat(leasehold).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(leasehold.getPurpose()).isEqualTo(type);
		assertThat(leasehold.getDescription()).isEqualTo(description);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(LeaseholdEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
