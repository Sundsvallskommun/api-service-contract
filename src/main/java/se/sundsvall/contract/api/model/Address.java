package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.AddressType;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Address {

	@Schema(example = "POSTAL_ADDRESS", description = "Address type")
	private AddressType type;

	@Schema(example = "Testvägen 18")
	private String streetAddress;

	@Schema(example = "c/o Test Testorsson")
	private String careOf;

	@Schema(example = "123 45")
	private String postalCode;

	@Schema(example = "Sundsvall")
	private String town;

	@Schema(example = "Sverige")
	private String country;

	@Schema(example = "Test Testorsson")
	private String attention;
}
