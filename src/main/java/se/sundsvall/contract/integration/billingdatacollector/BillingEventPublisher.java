package se.sundsvall.contract.integration.billingdatacollector;

import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;

public interface BillingEventPublisher {

	void publish(BillingEvent event);
}
