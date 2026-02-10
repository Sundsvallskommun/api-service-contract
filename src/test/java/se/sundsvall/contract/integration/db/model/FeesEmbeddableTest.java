package se.sundsvall.contract.integration.db.model;

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

class FeesEmbeddableTest {

	@Test
	void testBean() {
		assertThat(FeesEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var currency = "SEK";
		var yearly = BigDecimal.valueOf(4350);
		var monthly = BigDecimal.valueOf(375);
		var total = BigDecimal.valueOf(52200);
		var totalAsText = "FEMTITVÅTUSENTVÅHUNDRAKRONOR";
		var indexType = "KPI 80";
		var indexYear = 2023;
		var indexNumber = 2;
		var indexationRate = BigDecimal.valueOf(0.5);
		var additionalInformation = List.of("info1", "info2");

		var fees = FeesEmbeddable.builder()
			.withCurrency(currency)
			.withYearly(yearly)
			.withMonthly(monthly)
			.withTotal(total)
			.withTotalAsText(totalAsText)
			.withIndexType(indexType)
			.withIndexYear(indexYear)
			.withIndexNumber(indexNumber)
			.withIndexationRate(indexationRate)
			.withAdditionalInformation(additionalInformation)
			.build();

		assertThat(fees).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(fees.getCurrency()).isEqualTo(currency);
		assertThat(fees.getYearly()).isEqualTo(yearly);
		assertThat(fees.getMonthly()).isEqualTo(monthly);
		assertThat(fees.getTotal()).isEqualTo(total);
		assertThat(fees.getTotalAsText()).isEqualTo(totalAsText);
		assertThat(fees.getIndexType()).isEqualTo(indexType);
		assertThat(fees.getIndexYear()).isEqualTo(indexYear);
		assertThat(fees.getIndexNumber()).isEqualTo(indexNumber);
		assertThat(fees.getIndexationRate()).isEqualTo(indexationRate);
		assertThat(fees.getAdditionalInformation()).isEqualTo(additionalInformation);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeesEmbeddable.builder().build()).hasAllNullFieldsOrProperties();
	}
}
