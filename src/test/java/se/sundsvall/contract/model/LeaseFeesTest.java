package se.sundsvall.contract.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class LeaseFeesTest {

	@Test
	void testBean() {
		assertThat(LeaseFees.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var currency = "USD";
		final var yearly = BigDecimal.valueOf(12.50);
		final var monthly = BigDecimal.valueOf(1.50);
		final var total = BigDecimal.valueOf(25);
		final var totalAsText = "twenty five";
		final var indexYear = 2022;
		final var indexNumber = 5;
		final var additionalInformation = List.of("aaa", "bbb");

		final var leaseFees = LeaseFees.builder()
			.withCurrency(currency)
			.withYearly(yearly)
			.withMonthly(monthly)
			.withTotal(total)
			.withTotalAsText(totalAsText)
			.withIndexYear(indexYear)
			.withIndexNumber(indexNumber)
			.withAdditionalInformation(additionalInformation)
			.build();

		assertThat(leaseFees).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(leaseFees.getCurrency()).isEqualTo(currency);
		assertThat(leaseFees.getYearly()).isEqualTo(yearly);
		assertThat(leaseFees.getMonthly()).isEqualTo(monthly);
		assertThat(leaseFees.getTotal()).isEqualTo(total);
		assertThat(leaseFees.getTotalAsText()).isEqualTo(totalAsText);
		assertThat(leaseFees.getIndexYear()).isEqualTo(indexYear);
		assertThat(leaseFees.getIndexNumber()).isEqualTo(indexNumber);
		assertThat(leaseFees.getAdditionalInformation()).containsExactlyElementsOf(additionalInformation);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(LeaseFees.builder().build()).hasAllNullFieldsOrProperties();
	}
}