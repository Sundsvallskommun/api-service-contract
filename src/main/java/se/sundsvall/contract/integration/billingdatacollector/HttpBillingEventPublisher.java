package se.sundsvall.contract.integration.billingdatacollector;

import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;

@Component
public class HttpBillingEventPublisher implements BillingEventPublisher {

	private final BillingDataCollectorClient client;

	public HttpBillingEventPublisher(final BillingDataCollectorClient client) {
		this.client = client;
	}

	@Override
	public void publish(final BillingEvent event) {
		client.sendEvent(event.municipalityId(), BillingSource.CONTRACT, event);
	}
}
