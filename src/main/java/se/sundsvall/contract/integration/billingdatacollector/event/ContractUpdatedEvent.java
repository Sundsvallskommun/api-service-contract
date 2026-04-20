package se.sundsvall.contract.integration.billingdatacollector.event;

import java.time.LocalDate;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Status;

public record ContractUpdatedEvent(
	String contractId,
	String municipalityId,
	ContractType type,
	Status status,
	LocalDate startDate,
	LocalDate endDate,
	LocalDate currentPeriodStartDate,
	LocalDate currentPeriodEndDate,
	InvoicedIn invoicedIn,
	IntervalType invoiceInterval,
	LeaseType leaseType)
	implements
	BillingEvent {

	@Override
	public String eventType() {
		return "CONTRACT_UPDATED";
	}
}
