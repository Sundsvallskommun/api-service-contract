package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.TimeUnit;

class NoticeTermTest {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void testBean() {
		assertThat(NoticeTerm.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var party = Party.LESSEE;
		final var periodOfNotice = 3;
		final var unit = TimeUnit.DAYS;

		final var object = NoticeTerm.builder()
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
		assertThat(NoticeTerm.builder().build()).hasAllNullFieldsOrProperties();
	}

	@ParameterizedTest
	@ValueSource(ints = {
		0, -1, -100
	})
	void testPeriodOfNoticeMustBePositive(int invalidValue) {
		final var noticeTerm = NoticeTerm.builder()
			.withParty(Party.LESSEE)
			.withPeriodOfNotice(invalidValue)
			.withUnit(TimeUnit.MONTHS)
			.build();

		final var violations = VALIDATOR.validate(noticeTerm);

		assertThat(violations)
			.isNotEmpty()
			.anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("periodOfNotice"));
	}

	@Test
	void testValidNoticeTermHasNoViolations() {
		final var noticeTerm = NoticeTerm.builder()
			.withParty(Party.LESSEE)
			.withPeriodOfNotice(3)
			.withUnit(TimeUnit.MONTHS)
			.build();

		final var violations = VALIDATOR.validate(noticeTerm);

		assertThat(violations).isEmpty();
	}
}
