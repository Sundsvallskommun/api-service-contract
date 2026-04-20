package se.sundsvall.contract.integration.billingdatacollector.event;

import java.time.LocalDate;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

public record ContractTerminatedEvent(
	String contractId,
	String municipalityId,
	LocalDate endDate,
	InvoicedIn invoicedIn,
	IntervalType invoiceInterval)
	implements
	BillingEvent {

	@Override
	public String eventType() {
		return "CONTRACT_TERMINATED";
	}
}
