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
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Leasehold")
public class Leasehold {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.LeaseholdType}
	 */
	@Schema(example = "OTHER", description = "Type of leasehold")
	@OneOf(value = {
		"AGRICULTURE", "APARTMENT", "BOATING_PLACE", "BUILDING", "DEPOT", "DWELLING",
		"LAND_COMPLEMENT", "LINEUP", "OTHER", "PARKING", "RECYCLING_STATION", "ROAD", "SIGNBOARD",
		"SNOW_DUMP", "SPORTS_PURPOSE", "SURFACE_HEAT", "TRAIL"
	}, nullable = true)
	private String purpose;

	@Schema(description = "description ", example = "A simple description of the leasehold")
	private String description;

	@ArraySchema(schema = @Schema(description = "Additional information", example = "Some additional information"))
	private List<String> additionalInformation;

}
