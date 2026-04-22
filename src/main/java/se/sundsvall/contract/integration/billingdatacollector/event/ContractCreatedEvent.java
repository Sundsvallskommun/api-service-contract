package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractCreatedEvent(String id, String municipalityId, String eventType)
	implements
	BillingEvent {

	public static ContractCreatedEvent of(final String id, final String municipalityId) {
		return new ContractCreatedEvent(id, municipalityId, "CONTRACT_CREATED");
	}
}
