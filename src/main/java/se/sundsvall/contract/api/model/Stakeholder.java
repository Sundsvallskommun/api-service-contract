package se.sundsvall.contract.api.model;

import java.util.List;

import se.sundsvall.contract.api.model.enums.StakeholderRole;
import se.sundsvall.contract.api.model.enums.StakeholderType;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
public class Stakeholder {

	@Schema(example = "ASSOCIATION")
	private StakeholderType type;

	@ArraySchema(schema = @Schema(description = "List of roles"))
	private List<StakeholderRole> roles;

	@Schema(description = "Name of the organization", example = "Sundsvalls kommun")
	private String organizationName;

	@Schema(description = "Swedish organization number", example = "212000-2411")
	private String organizationNumber;

	@Schema(description = "Stakeholders first name", example = "Test")
	private String firstName;

	@Schema(description = "Stakeholders last name", example = "Testorsson")
	private String lastName;

	@Schema(description = "", example = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	private String personId;

	@Schema(description = "Phone number for stakeholder", example = "0701231212")
	private String phoneNumber;

	@Schema(description = "Email adress for stakeholder", example = "test.testorsson@test.se")
	private String emailAddress;

	@Schema(description = "Address for stakeholder")
	private Address address;

}
