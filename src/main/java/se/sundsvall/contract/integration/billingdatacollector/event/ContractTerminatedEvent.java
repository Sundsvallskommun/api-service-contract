package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractTerminatedEvent(String id, String municipalityId, String eventType)
	implements
	BillingEvent {

	public static ContractTerminatedEvent of(final String id, final String municipalityId) {
		return new ContractTerminatedEvent(id, municipalityId, "CONTRACT_TERMINATED");
	}
}
