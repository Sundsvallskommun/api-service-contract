package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractCreatedEvent(
	String id,
	String municipalityId)
	implements
	BillingEvent {

	@Override
	public String eventType() {
		return "CONTRACT_CREATED";
	}
}
