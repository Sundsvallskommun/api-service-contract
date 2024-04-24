package se.sundsvall.contract.api.model;

import java.util.List;

import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Stakeholder {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.StakeholderType}
	 */
	@Schema(example = "ASSOCIATION", description = "Type of stakeholder, possible values: PERSON | COMPANY | ASSOCIATION")
	@OneOf(value = {"PERSON", "COMPANY", "ASSOCIATION"}, nullable = true)
	private String type;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.StakeholderRole}
	 */
	@ArraySchema(schema = @Schema(description = "List of roles", example = "BUYER"))
	@OneOf(value = {"BUYER", "CONTACT_PERSON", "GRANTOR", "LAND_OWNER", "LEASE_HOLDER", "POWER_OF_ATTORNEY_CHECK",
		"POWER_OF_ATTORNEY_ROLE", "SELLER", "SIGNATORY"}, nullable = true)
	private List<String> roles;

	@Schema(description = "Name of the organization", example = "Sundsvalls kommun")
	private String organizationName;

	@Schema(description = "Swedish organization number", example = "212000-2411")
	private String organizationNumber;

	@Schema(description = "Stakeholders first name", example = "Test")
	private String firstName;

	@Schema(description = "Stakeholders last name", example = "Testorsson")
	private String lastName;

	@Schema(description = "PartyId", example = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	private String partyId;

	@Schema(description = "Phone number for stakeholder", example = "0701231212")
	private String phoneNumber;

	@Schema(description = "Email adress for stakeholder", example = "test.testorsson@test.se")
	private String emailAddress;

	@Schema(description = "Address for stakeholder")
	private Address address;
}
