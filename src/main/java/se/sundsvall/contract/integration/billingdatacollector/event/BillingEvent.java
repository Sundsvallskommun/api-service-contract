package se.sundsvall.contract.integration.billingdatacollector.event;

public sealed interface BillingEvent permits ContractCreatedEvent, ContractUpdatedEvent, ContractDeletedEvent, ContractTerminatedEvent {

	String id();

	String municipalityId();

	String eventType();
}
