package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "InvoicedIn", enumAsRef = true)
public enum InvoicedIn {
	ADVANCE,
	ARREARS
}
