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
		var type = POSTAL_ADDRESS;
		var streetAddress = "Testvägen 18";
		var postalCode = "123 45";
		var town = "Sundsvall";
		var country = "Sverige";
		var attention = "Test Testorsson";

		var address = AddressEntity.builder()
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
		assertThat(AddressEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
