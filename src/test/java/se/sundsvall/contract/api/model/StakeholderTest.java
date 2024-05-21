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
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.ArraySchema;

class StakeholderTest {

	@Test
	void testBean() {
		assertThat(Stakeholder.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testStakeholder_type_hasCorrectOneOfValues() throws NoSuchFieldException {
		var oneOf = Stakeholder.class.getDeclaredField("type")
			.getAnnotation(OneOf.class);

		Arrays.stream(oneOf.value())
			.forEach(value -> assertThat(oneOf.value()).contains(value));
	}

	@Test
	void testStakeholder_roles_hasCorrectDescription() throws NoSuchFieldException {
		var schema = Stakeholder.class.getDeclaredField("roles")
			.getAnnotation(ArraySchema.class).schema();

		Arrays.stream(StakeholderRole.values())
			.forEach(value -> assertThat(schema.description()).contains(value.name()));
	}

	@Test
	void testBuilderMethods() {

		var type = StakeholderType.COMPANY;
		var roles = List.of(StakeholderRole.POWER_OF_ATTORNEY_ROLE);
		var organizationName = "Sundsvalls kommun";
		var organizationNumber = "212000-2411";
		var firstName = "Test";
		var lastName = "Testorsson";
		var partyId = "40f14de9-815d-44a5-a34d-b1d38b628e07";
		var phoneNumber = "0701231212";
		var emailAddress = "test.testorsson@test.se";
		var address = Address.builder().build();

		var stakeholder = Stakeholder.builder()
			.withType(type.name())
			.withRoles(roles.stream().map(StakeholderRole::name).toList())
			.withOrganizationName(organizationName)
			.withOrganizationNumber(organizationNumber)
			.withFirstName(firstName)
			.withLastName(lastName)
			.withPartyId(partyId)
			.withPhoneNumber(phoneNumber)
			.withEmailAddress(emailAddress)
			.withAddress(address)
			.build();

		assertThat(stakeholder).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(stakeholder.getType()).isEqualTo(type.name());
		assertThat(stakeholder.getRoles()).isEqualTo(roles.stream().map(StakeholderRole::name).toList());
		assertThat(stakeholder.getOrganizationName()).isEqualTo(organizationName);
		assertThat(stakeholder.getOrganizationNumber()).isEqualTo(organizationNumber);
		assertThat(stakeholder.getFirstName()).isEqualTo(firstName);
		assertThat(stakeholder.getLastName()).isEqualTo(lastName);
		assertThat(stakeholder.getPartyId()).isEqualTo(partyId);
		assertThat(stakeholder.getPhoneNumber()).isEqualTo(phoneNumber);
		assertThat(stakeholder.getEmailAddress()).isEqualTo(emailAddress);
		assertThat(stakeholder.getAddress()).isEqualTo(address);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Stakeholder.builder().build()).hasAllNullFieldsOrProperties();
	}

}
