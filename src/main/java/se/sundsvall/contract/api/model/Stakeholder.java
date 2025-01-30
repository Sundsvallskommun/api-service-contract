package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Stakeholder {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.StakeholderType}
	 */
	@Schema(example = "ASSOCIATION", description = "Type of stakeholder, possible values: PERSON | COMPANY | ASSOCIATION")
	@OneOf(value = {
		"PERSON", "COMPANY", "ASSOCIATION"
	}, nullable = true)
	private String type;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.StakeholderRole}
	 */
	@ArraySchema(schema = @Schema(description = "List of roles, possible values: BUYER | CONTACT_PERSON | GRANTOR | LAND_RIGHT_OWNER | LEASEHOLDER | " +
		"PROPERTY_OWNER | POWER_OF_ATTORNEY_CHECK | POWER_OF_ATTORNEY_ROLE | SELLER | SIGNATORY", example = "BUYER"))
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

	@With
	@Schema(description = "Parameters for the stakeholder")
	private List<@Valid Parameter> parameters;
}
