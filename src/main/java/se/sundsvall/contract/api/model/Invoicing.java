package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;

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

	@NotNull
	@Schema(description = "Whether the contract is to be billed or not", examples = "true")
	private Boolean billingEnabled;

	@Schema(description = "The month in which yearly billing takes place, June (6) or December (12). Mandatory when invoiceInterval is YEARLY", examples = "12")
	private Integer yearlyBillingMonth;

	@AssertTrue(message = "If 'invoiceInterval' is YEARLY, 'yearlyBillingMonth' must be provided!")
	boolean hasYearlyBillingMonthWhenYearly() {
		return invoiceInterval != YEARLY || yearlyBillingMonth != null;
	}
}
