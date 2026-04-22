package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractTerminatedEvent(
	String id,
	String municipalityId)
	implements
	BillingEvent {

	@Override
	public String eventType() {
		return "CONTRACT_TERMINATED";
	}
}
