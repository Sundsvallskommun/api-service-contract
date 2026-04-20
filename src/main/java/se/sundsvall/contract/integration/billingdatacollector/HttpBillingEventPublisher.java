package se.sundsvall.contract.integration.billingdatacollector;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;

@Primary
@Component
public class HttpBillingEventPublisher implements BillingEventPublisher {

	private final BillingDataCollectorClient client;

	public HttpBillingEventPublisher(final BillingDataCollectorClient client) {
		this.client = client;
	}

	@Override
	public void publish(final BillingEvent event) {
		switch (event) {
			case ContractCreatedEvent e -> client.contractCreated(e.municipalityId(), e);
			case ContractUpdatedEvent e -> client.contractUpdated(e.municipalityId(), e);
			case ContractDeletedEvent e -> client.contractDeleted(e.municipalityId(), e);
			case ContractTerminatedEvent e -> client.contractTerminated(e.municipalityId(), e);
		}
	}
}
