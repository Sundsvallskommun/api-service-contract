package se.sundsvall.contract.api.model;

import se.sundsvall.contract.api.model.enums.AddressType;

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
public class Address {

	@Schema(example = "POSTAL_ADDRESS")
	private AddressType type;

	@Schema(example = "Testvägen 18")
	private String streetAddress;

	@Schema(example = "123 45")
	private String postalCode;

	@Schema(example = "Sundsvall")
	private String town;

	@Schema(example = "Sverige")
	private String country;

	@Schema(example = "Test Testorsson")
	private String attention;

}
