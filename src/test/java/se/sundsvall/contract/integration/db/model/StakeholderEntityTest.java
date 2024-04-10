package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;

class StakeholderEntityTest {

	@Test
	void testBean() {
		assertThat(StakeholderEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("roles"),
			hasValidBeanEqualsExcluding("roles"),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var id = 1L;
		var type = StakeholderType.COMPANY;
		var roles = List.of(StakeholderRole.POWER_OF_ATTORNEY_ROLE);
		var organizationName = "Sundsvalls kommun";
		var organizationNumber = "212000-2411";
		var firstName = "Test";
		var lastName = "Testorsson";
		var partyId = "40f14de9-815d-44a5-a34d-b1d38b628e07";
		var phoneNumber = "0701231212";
		var emailAddress = "test.testorsson@test.se";
		var address = AddressEntity.builder().build();

		var stakeholder = StakeholderEntity.builder()
			.withId(id)
			.withType(type)
			.withRoles(roles)
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
		assertThat(stakeholder.getId()).isEqualTo(id);
		assertThat(stakeholder.getType()).isEqualTo(type);
		assertThat(stakeholder.getRoles()).isEqualTo(roles);
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
		assertThat(StakeholderEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
