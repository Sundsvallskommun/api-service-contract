package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "PropertyDesignation")
public class PropertyDesignation {

	@Schema(description = "Name of property designation", example = "SUNDSVALL BALDER 5:1", maxLength = 255)
	@Size(max = 255)
	private String name;

	@Schema(description = "District of property designation", example = "Sundsvall", maxLength = 255)
	@Size(max = 255)
	private String district;
}
