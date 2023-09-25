package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.enums.AddressType;

class AddressTest {

	@Test
	void testBean() {
		assertThat(Address.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var type = AddressType.POSTAL_ADDRESS;
		final var streetAddress = "Testvägen 18";
		final var postalCode = "123 45";
		final var town = "Sundsvall";
		final var country = "Sverige";
		final var attention = "Test Testorsson";

		final var address = Address.builder()
			.withType(type)
			.withStreetAddress(streetAddress)
			.withPostalCode(postalCode)
			.withTown(town)
			.withCountry(country)
			.withAttention(attention)
			.build();

		assertThat(address).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(address.getType()).isEqualTo(type);
		assertThat(address.getStreetAddress()).isEqualTo(streetAddress);
		assertThat(address.getPostalCode()).isEqualTo(postalCode);
		assertThat(address.getTown()).isEqualTo(town);
		assertThat(address.getCountry()).isEqualTo(country);
		assertThat(address.getAttention()).isEqualTo(attention);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Address.builder().build()).hasAllNullFieldsOrProperties();
	}


}
