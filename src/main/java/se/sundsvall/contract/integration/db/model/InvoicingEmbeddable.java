package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

@Embeddable
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class InvoicingEmbeddable {

	@Column(name = "invoice_interval", length = 64)
	private IntervalType invoiceInterval;

	@Column(name = "invoiced_in", length = 64)
	private InvoicedIn invoicedIn;
}
