package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

	@NotNull
	@Schema(description = "How often the lease is invoiced", examples = "QUARTERLY")
	private IntervalType invoiceInterval;

	@NotNull
	private InvoicedIn invoicedIn;
}
