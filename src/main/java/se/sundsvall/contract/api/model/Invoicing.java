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
@Schema(description = "Invoicing details")
public class Invoicing {

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.IntervalType}
	 */
	@Schema(description = "How often the lease is invoiced", example = "QUARTERLY")
	@OneOf(value = {
		"YEARLY", "QUARTERLY", "MONTHLY"
	}, nullable = true)
	private String invoiceInterval;

	/*
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.InvoicedIn}
	 */
	@Schema(description = "How the lease is invoiced", example = "ADVANCE")
	@OneOf(value = {
		"ADVANCE", "ARREARS"
	}, nullable = true)
	private String invoicedIn;
}
