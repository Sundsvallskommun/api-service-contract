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

class FeesTest {

	@Test
	void testBean() {
		assertThat(Fees.class, allOf(
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
		final var indexType = "indexType";
		final var indexationRate = BigDecimal.valueOf(0.5);
		final var indexYear = 2022;
		final var indexNumber = 5;
		final var additionalInformation = List.of("aaa", "bbb");

		final var fees = Fees.builder()
			.withCurrency(currency)
			.withYearly(yearly)
			.withMonthly(monthly)
			.withTotal(total)
			.withTotalAsText(totalAsText)
			.withIndexType(indexType)
			.withIndexationRate(indexationRate)
			.withIndexYear(indexYear)
			.withIndexNumber(indexNumber)
			.withAdditionalInformation(additionalInformation)
			.build();

		assertThat(fees).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(fees.getCurrency()).isEqualTo(currency);
		assertThat(fees.getYearly()).isEqualTo(yearly);
		assertThat(fees.getMonthly()).isEqualTo(monthly);
		assertThat(fees.getTotal()).isEqualTo(total);
		assertThat(fees.getTotalAsText()).isEqualTo(totalAsText);
		assertThat(fees.getIndexType()).isEqualTo(indexType);
		assertThat(fees.getIndexationRate()).isEqualTo(indexationRate);
		assertThat(fees.getIndexYear()).isEqualTo(indexYear);
		assertThat(fees.getIndexNumber()).isEqualTo(indexNumber);
		assertThat(fees.getAdditionalInformation()).containsExactlyElementsOf(additionalInformation);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Fees.builder().build()).hasAllNullFieldsOrProperties();
	}
}
