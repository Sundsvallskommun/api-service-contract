package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Address type", enumAsRef = true)
public enum AddressType {
	POSTAL_ADDRESS,
	BILLING_ADDRESS,
	VISITING_ADDRESS
}
