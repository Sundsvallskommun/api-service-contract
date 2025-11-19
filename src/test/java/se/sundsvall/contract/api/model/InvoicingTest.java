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
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;

import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

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
	void oneOfValuesShouldMatchIntervalTypeEnum() throws Exception {
		// Fetch field
		var field = Invoicing.class.getDeclaredField("invoiceInterval");
		var annotation = field.getAnnotation(OneOf.class);
		assertThat(annotation).isNotNull();

		// Fetch annotation values
		var annotationValues = Set.of(annotation.value());

		// Fetch enum values
		var enumValues = Arrays.stream(IntervalType.values())
			.map(Enum::name)
			.collect(toSet());

		// Assert
		assertThat(enumValues).containsExactlyInAnyOrderElementsOf(annotationValues);
	}

	@Test
	void testBuilderMethods() {
		var invoiceInterval = MONTHLY.name();
		var invoicedIn = ADVANCE.name();

		var invoicing = Invoicing.builder()
			.withInvoiceInterval(invoiceInterval)
			.withInvoicedIn(invoicedIn)
			.build();

		assertThat(invoicing.getInvoiceInterval()).isEqualTo(invoiceInterval);
		assertThat(invoicing.getInvoicedIn()).isEqualTo(invoicedIn);
	}

	@Test
	void test_invoiceInterval_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = Invoicing.class.getDeclaredField("invoiceInterval")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(IntervalType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}
}
