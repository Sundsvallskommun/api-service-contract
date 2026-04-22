package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractDeletedEvent(
	String id,
	String municipalityId)
	implements
	BillingEvent {

	@Override
	public String eventType() {
		return "CONTRACT_DELETED";
	}
}
