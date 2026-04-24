package se.sundsvall.contract.integration.billingdatacollector.event;

public record ContractDeletedEvent(String id, String municipalityId, String eventType)
	implements
	BillingEvent {

	public static ContractDeletedEvent of(final String id, final String municipalityId) {
		return new ContractDeletedEvent(id, municipalityId, "DELETED");
	}
}
