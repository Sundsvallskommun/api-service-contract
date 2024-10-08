package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.model.enums.AddressType.POSTAL_ADDRESS;

import org.junit.jupiter.api.Test;

class AddressEntityTest {

	@Test
	void testBean() {
		assertThat(AddressEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var type = POSTAL_ADDRESS;
		final var streetAddress = "Testvägen 18";
		final var postalCode = "123 45";
		final var town = "Sundsvall";
		final var country = "Sverige";
		final var attention = "Test Testorsson";
		final var careOf = "c/o Test Testorsson";

		final var address = AddressEntity.builder()
			.withType(type)
			.withStreetAddress(streetAddress)
			.withPostalCode(postalCode)
			.withTown(town)
			.withCountry(country)
			.withAttention(attention)
			.withCareOf(careOf)
			.build();

		assertThat(address).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(address.getType()).isEqualTo(type);
		assertThat(address.getStreetAddress()).isEqualTo(streetAddress);
		assertThat(address.getPostalCode()).isEqualTo(postalCode);
		assertThat(address.getTown()).isEqualTo(town);
		assertThat(address.getCountry()).isEqualTo(country);
		assertThat(address.getAttention()).isEqualTo(attention);
		assertThat(address.getCareOf()).isEqualTo(careOf);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AddressEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
