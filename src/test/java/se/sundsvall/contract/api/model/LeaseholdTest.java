package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.LeaseholdType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class LeaseholdTest {

	@Test
	void testBean() {
		assertThat(Leasehold.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testLeaseHold_type_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = Leasehold.class.getDeclaredField("purpose")
			.getAnnotation(OneOf.class);

		Arrays.stream(oneOf.value())
			.forEach(value -> assertThat(oneOf.value()).contains(value));
	}

	@Test
	void testBuilderMethods() {

		var type = LeaseholdType.APARTMENT;

		var description = "description";

		var leasehold = Leasehold.builder()
			.withAdditionalInformation(Arrays.asList("info", "info2"))
			.withPurpose(type.name())
			.withDescription(description)
			.build();

		assertThat(leasehold).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(leasehold.getPurpose()).isEqualTo(type.name());
		assertThat(leasehold.getDescription()).isEqualTo(description);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Leasehold.builder().build()).hasAllNullFieldsOrProperties();
	}

}
