package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.TimeUnit;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

class NoticeTest {

	@Test
	void testBean() {
		assertThat(Notice.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void oneOfValuesShouldMatchTimeUnitEnum() throws Exception {
		// Fetch field
		var field = Notice.class.getDeclaredField("unit");
		var annotation = field.getAnnotation(OneOf.class);
		assertThat(annotation).isNotNull();

		// Fetch annotation values
		var annotationValues = Set.of(annotation.value());

		// Fetch enum values
		var enumValues = Arrays.stream(TimeUnit.values())
			.map(Enum::name)
			.collect(toSet());

		// Assert
		assertThat(enumValues).containsExactlyInAnyOrderElementsOf(annotationValues);
	}

	@Test
	void oneOfValuesShouldMatchPartyEnum() throws Exception {
		// Fetch field
		var field = Notice.class.getDeclaredField("party");
		var annotation = field.getAnnotation(OneOf.class);
		assertThat(annotation).isNotNull();

		// Fetch annotation values
		var annotationValues = Set.of(annotation.value());

		// Fetch enum values
		var enumValues = Arrays.stream(Party.values())
			.map(Enum::name)
			.collect(toSet());

		// Assert
		assertThat(enumValues).containsExactlyInAnyOrderElementsOf(annotationValues);
	}

	@Test
	void testBuilderMethods() {

		final var party = Party.LESSEE.toString();
		final var periodOfNotice = 3;
		final var unit = TimeUnit.DAYS.toString();

		var object = Notice.builder()
			.withParty(party)
			.withPeriodOfNotice(periodOfNotice)
			.withUnit(unit)
			.build();

		assertThat(object).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(object.getPeriodOfNotice()).isEqualTo(periodOfNotice);
		assertThat(object.getParty()).isEqualTo(party);
		assertThat(object.getUnit()).isEqualTo(unit);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Notice.builder().build()).hasAllNullFieldsOrProperties();
	}
}
