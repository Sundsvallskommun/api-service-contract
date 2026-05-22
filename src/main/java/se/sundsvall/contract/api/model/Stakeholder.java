package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Stakeholder {

	@Schema(examples = "ASSOCIATION", description = "Type of stakeholder")
	private StakeholderType type;

	@ArraySchema(schema = @Schema(examples = "BUYER"))
	private List<StakeholderRole> roles;

	@Size(max = 255)
	@Schema(description = "Name of the organization", examples = "Sundsvalls kommun")
	private String organizationName;

	// Intentionally not using @ValidOrganizationNumber: dept44's validator only
	// accepts the 10-digit hyphenless form, while this service has historically
	// accepted the hyphenated form (e.g. "212000-2411") in both API and stored
	// data. Adding strict validation here would be a breaking change.
	@Size(max = 255)
	@Schema(description = "Swedish organization number", examples = "212000-2411")
	private String organizationNumber;

	@Size(max = 255)
	@Schema(description = "Stakeholders first name", examples = "Test")
	private String firstName;

	@Size(max = 255)
	@Schema(description = "Stakeholders last name", examples = "Testorsson")
	private String lastName;

	@ValidUuid(nullable = true)
	@Schema(description = "PartyId", examples = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	private String partyId;

	@Size(max = 255)
	@Schema(description = "Phone number for stakeholder", examples = "0701231212")
	private String phoneNumber;

	@Size(max = 255)
	@Schema(description = "Email adress for stakeholder", examples = "test.testorsson@test.se")
	private String emailAddress;

	@Valid
	@Schema(description = "Address for stakeholder")
	private Address address;

	@Schema(description = "Parameters for the stakeholder")
	private List<@Valid Parameter> parameters;
}
