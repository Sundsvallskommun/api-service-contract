package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Invoicing details")
public class Invoicing {

	@Schema(description = "How often the lease is invoiced", example = "QUARTERLY")
	private IntervalType invoiceInterval;

	private InvoicedIn invoicedIn;
}
