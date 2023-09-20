package se.sundsvall.contract.api.model;


import jakarta.validation.constraints.NotBlank;

import se.sundsvall.contract.api.model.enums.LeaseholdType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Leasehold")
public class Leasehold {

	@NotBlank
	@Schema(example = "OTHER")
	private LeaseholdType type;

	@Schema(description = "description ", example = "A simple description of the leasehold")
	private String description;

}
