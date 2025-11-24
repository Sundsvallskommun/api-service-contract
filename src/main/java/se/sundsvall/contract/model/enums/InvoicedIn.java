package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Invoiced in", enumAsRef = true)
public enum InvoicedIn {
	ADVANCE,
	ARREARS
}
