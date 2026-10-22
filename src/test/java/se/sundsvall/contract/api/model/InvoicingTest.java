package se.sundsvall.contract.api.model;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.contract.model.enums.IntervalType;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;

class InvoicingTest {

	@Test
	void testBean() {
		assertThat(Invoicing.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Invoicing.builder().build()).hasAllNullFieldsOrProperties();
	}

	@Test
	void testBuilderMethods() {
		final var invoiceInterval = MONTHLY;
		final var invoicedIn = ADVANCE;
		final var billingEnabled = true;
		final var yearlyBillingMonth = 6;

		final var invoicing = Invoicing.builder()
			.withInvoiceInterval(invoiceInterval)
			.withInvoicedIn(invoicedIn)
			.withBillingEnabled(billingEnabled)
			.withYearlyBillingMonth(yearlyBillingMonth)
			.build();

		assertThat(invoicing).hasNoNullFieldsOrProperties();
		assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval);
		assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn);
		assertThat(invoicing.getBillingEnabled()).isEqualTo(billingEnabled);
		assertThat(invoicing.getYearlyBillingMonth()).isEqualTo(yearlyBillingMonth);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("yearlyBillingMonthWhenYearlyArgumentProvider")
	void hasYearlyBillingMonthWhenYearly(final String description, final IntervalType invoiceInterval, final Integer yearlyBillingMonth, final boolean expectedResult) {
		final var invoicing = Invoicing.builder()
			.withInvoiceInterval(invoiceInterval)
			.withYearlyBillingMonth(yearlyBillingMonth)
			.build();

		assertThat(invoicing.hasYearlyBillingMonthWhenYearly()).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> yearlyBillingMonthWhenYearlyArgumentProvider() {
		return Stream.of(
			Arguments.of("YEARLY without month", YEARLY, null, false),
			Arguments.of("YEARLY with month", YEARLY, 12, true),
			Arguments.of("Other interval without month", QUARTERLY, null, true),
			Arguments.of("Other interval with month", QUARTERLY, 6, true),
			Arguments.of("No interval without month", null, null, true));
	}
}
