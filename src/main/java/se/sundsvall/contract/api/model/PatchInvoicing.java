package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

/**
 * Partial invoicing details for PATCH (JSON Merge Patch semantics). Invoicing is a billing pair; that both the interval
 * and invoicedIn are set when invoicing is present is validated on the merged contract.
 */
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial invoicing details for PATCH")
public class PatchInvoicing {

	@Schema(description = "How often the lease is invoiced", examples = "QUARTERLY")
	@Builder.Default
	private JsonNullable<IntervalType> invoiceInterval = JsonNullable.undefined();

	@Builder.Default
	private JsonNullable<InvoicedIn> invoicedIn = JsonNullable.undefined();
}
