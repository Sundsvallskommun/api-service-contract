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

	@Schema(example = "OTHER")
	private StakeholderType type;

	@ArraySchema(schema = @Schema(description = "Lista med roller."))
	private List<StakeholderRole> roles;

	@Schema(example = "Sundsvalls kommun")
	private String organizationName;

	@Schema(example = "212000-2411")
	private String organizationNumber;

	@Schema(example = "Test")
	private String firstName;

	@Schema(example = "Testorsson")
	private String lastName;

	@Schema(example = "40f14de9-815d-44a5-a34d-b1d38b628e07")
	private String personId;

	// Lyfta ut detta i en egen klass? t.ex. ContactInfo
	@Schema(example = "0701231212")
	private String phoneNumber;

	@Schema(example = "test.testorsson@test.se")
	private String emailAddress;

	private Address address;

}
