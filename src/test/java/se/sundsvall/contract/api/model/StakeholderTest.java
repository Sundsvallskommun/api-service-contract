package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

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
		final var oneOf = Stakeholder.class.getDeclaredField("type")
			.getAnnotation(OneOf.class);

		Arrays.stream(oneOf.value())
			.forEach(value -> assertThat(oneOf.value()).contains(value));
	}

	@Test
	void testStakeholder_roles_hasCorrectDescription() throws NoSuchFieldException {
		final var schema = Stakeholder.class.getDeclaredField("roles")
			.getAnnotation(ArraySchema.class).schema();

		Arrays.stream(StakeholderRole.values())
			.forEach(value -> assertThat(schema.description()).contains(value.name()));
	}

	@Test
	void testBuilderMethods() {

		final var type = StakeholderType.COMPANY;
		final var roles = List.of(StakeholderRole.POWER_OF_ATTORNEY_ROLE);
		final var organizationName = "Sundsvalls kommun";
		final var organizationNumber = "212000-2411";
		final var firstName = "Test";
		final var lastName = "Testorsson";
		final var partyId = "40f14de9-815d-44a5-a34d-b1d38b628e07";
		final var phoneNumber = "0701231212";
		final var emailAddress = "test.testorsson@test.se";
		final var address = Address.builder().build();
		final var parameters = List.of(Parameter.builder().build());

		final var stakeholder = Stakeholder.builder()
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
			.withParameters(parameters)
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
		assertThat(stakeholder.getParameters()).isEqualTo(parameters);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Stakeholder.builder().build()).hasAllNullFieldsOrProperties();
	}

}
