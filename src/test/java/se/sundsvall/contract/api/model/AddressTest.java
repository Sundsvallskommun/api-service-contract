package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.AddressType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

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
	void testAddress_type_hasValidOneOfValues() throws NoSuchFieldException {
		var oneOf = Address.class.getDeclaredField("type")
			.getAnnotation(OneOf.class)
			.value();

		Arrays.stream(AddressType.values())
			.forEach(value -> assertThat(oneOf).contains(value.name()));
	}

	@Test
	void testBuilderMethods() {
		var type = AddressType.POSTAL_ADDRESS;
		var streetAddress = "Testvägen 18";
		var postalCode = "123 45";
		var town = "Sundsvall";
		var country = "Sverige";
		var attention = "Test Testorsson";

		var address = Address.builder()
			.withType(type.name())
			.withStreetAddress(streetAddress)
			.withPostalCode(postalCode)
			.withTown(town)
			.withCountry(country)
			.withAttention(attention)
			.build();

		assertThat(address).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(address.getType()).isEqualTo(type.name());
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
