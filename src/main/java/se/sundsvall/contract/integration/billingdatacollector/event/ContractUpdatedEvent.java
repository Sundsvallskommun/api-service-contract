package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractUpdatedEvent(String id, String municipalityId, String eventType)
	implements
	BillingEvent {

	public static ContractUpdatedEvent of(final String id, final String municipalityId) {
		return new ContractUpdatedEvent(id, municipalityId, "UPDATED");
	}
}
