package se.sundsvall.contract.api.model;

import se.sundsvall.dept44.common.validators.annotation.OneOf;

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
public class Address {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.AddressType}
	 */
	@Schema(example = "POSTAL_ADDRESS", description = "Address type")
	@OneOf(value = {"POSTAL_ADDRESS", "BILLING_ADDRESS", "VISITING_ADDRESS"}, nullable = true)
	private String type;

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
