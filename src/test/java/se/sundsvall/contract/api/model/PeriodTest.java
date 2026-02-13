package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PeriodTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(Period.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var startDate = LocalDate.of(2021, 7, 1);
		final var endDate = LocalDate.of(2030, 6, 30);

		final var object = Period.builder()
			.withStartDate(startDate)
			.withEndDate(endDate)
			.build();

		assertThat(object).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(object.getStartDate()).isEqualTo(startDate);
		assertThat(object.getEndDate()).isEqualTo(endDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Period.builder().build()).hasAllNullFieldsOrProperties();
	}
}
