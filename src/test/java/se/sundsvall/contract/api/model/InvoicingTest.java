package se.sundsvall.contract.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
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

		final var invoicing = Invoicing.builder()
			.withInvoiceInterval(invoiceInterval)
			.withInvoicedIn(invoicedIn)
			.build();

		assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval);
		assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn);
	}
}
