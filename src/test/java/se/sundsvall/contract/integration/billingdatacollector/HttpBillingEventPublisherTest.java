package se.sundsvall.contract.integration.billingdatacollector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class HttpBillingEventPublisherTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String CONTRACT_ID = "CONTRACT-1";

	@Mock
	private BillingDataCollectorClient clientMock;

	@InjectMocks
	private HttpBillingEventPublisher publisher;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(clientMock);
	}

	@Test
	void publishContractCreatedEvent() {
		final var event = ContractCreatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		publisher.publish(event);
		verify(clientMock).sendEvent(MUNICIPALITY_ID, BillingSource.CONTRACTS, event);
	}

	@Test
	void publishContractUpdatedEvent() {
		final var event = ContractUpdatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		publisher.publish(event);
		verify(clientMock).sendEvent(MUNICIPALITY_ID, BillingSource.CONTRACTS, event);
	}

	@Test
	void publishContractDeletedEvent() {
		final var event = ContractDeletedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		publisher.publish(event);
		verify(clientMock).sendEvent(MUNICIPALITY_ID, BillingSource.CONTRACTS, event);
	}

	@Test
	void publishContractTerminatedEvent() {
		final var event = ContractTerminatedEvent.of(CONTRACT_ID, MUNICIPALITY_ID);
		publisher.publish(event);
		verify(clientMock).sendEvent(MUNICIPALITY_ID, BillingSource.CONTRACTS, event);
	}
}
