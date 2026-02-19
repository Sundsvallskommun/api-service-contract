package se.sundsvall.contract.api.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.Party;

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

class NoticeTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

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
	void testBuilderMethods() {

		final var terms = List.of(NoticeTerm.builder().build());
		final var noticeDate = LocalDate.now().plusMonths(3);
		final var noticeGivenBy = Party.LESSOR;

		final var object = Notice.builder()
			.withTerms(terms)
			.withNoticeDate(noticeDate)
			.withNoticeGivenBy(noticeGivenBy)
			.build();

		assertThat(object).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(object.getTerms()).isEqualTo(terms);
		assertThat(object.getNoticeDate()).isEqualTo(noticeDate);
		assertThat(object.getNoticeGivenBy()).isEqualTo(noticeGivenBy);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Notice.builder().build()).hasAllNullFieldsOrProperties();
	}
}
