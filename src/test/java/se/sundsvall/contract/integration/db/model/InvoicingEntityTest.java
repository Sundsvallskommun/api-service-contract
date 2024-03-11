package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.api.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.api.model.enums.InvoicedIn.ARREARS;

import org.junit.jupiter.api.Test;

class InvoicingEntityTest {

	@Test
	void testBean() {
		assertThat(InvoicingEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var invoiceInterval = MONTHLY;
		final var invoicedIn = ARREARS;

		final var invoicing = InvoicingEntity.builder()
			.withInvoiceInterval(invoiceInterval)
			.withInvoicedIn(invoicedIn)
			.build();

		assertThat(invoicing).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval);
		assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(InvoicingEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
