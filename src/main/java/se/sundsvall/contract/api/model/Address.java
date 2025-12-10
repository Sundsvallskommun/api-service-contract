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

	@Schema(examples = "POSTAL_ADDRESS", description = "Address type")
	private AddressType type;

	@Schema(examples = "Testvägen 18")
	private String streetAddress;

	@Schema(examples = "c/o Test Testorsson")
	private String careOf;

	@Schema(examples = "123 45")
	private String postalCode;

	@Schema(examples = "Sundsvall")
	private String town;

	@Schema(examples = "Sverige")
	private String country;

	@Schema(examples = "Test Testorsson")
	private String attention;
}
