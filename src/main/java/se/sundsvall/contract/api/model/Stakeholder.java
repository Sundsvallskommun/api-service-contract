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
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Stakeholder {

	@Schema(examples = "ASSOCIATION", description = "Type of stakeholder")
	private StakeholderType type;

	@ArraySchema(schema = @Schema(examples = "BUYER"))
	private List<StakeholderRole> roles;

	@Schema(description = "Name of the organization", examples = "Sundsvalls kommun")
	private String organizationName;

	@Schema(description = "Swedish organization number", examples = "212000-2411")
	private String organizationNumber;

	@Schema(description = "Stakeholders first name", examples = "Test")
	private String firstName;

	@Schema(description = "Stakeholders last name", examples = "Testorsson")
	private String lastName;

	@Schema(description = "PartyId", examples = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	private String partyId;

	@Schema(description = "Phone number for stakeholder", examples = "0701231212")
	private String phoneNumber;

	@Schema(description = "Email adress for stakeholder", examples = "test.testorsson@test.se")
	private String emailAddress;

	@Schema(description = "Address for stakeholder")
	private Address address;

	@Schema(description = "Parameters for the stakeholder")
	private List<@Valid Parameter> parameters;
}
