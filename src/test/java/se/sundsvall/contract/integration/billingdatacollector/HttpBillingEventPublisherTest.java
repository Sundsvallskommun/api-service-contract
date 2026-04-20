package se.sundsvall.contract.integration.billingdatacollector;

import java.time.LocalDate;
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
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Status;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class HttpBillingEventPublisherTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String CONTRACT_ID = "CONTRACT-1";
	private static final LocalDate START_DATE = LocalDate.of(2020, 1, 1);
	private static final LocalDate END_DATE = LocalDate.of(2026, 6, 30);

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
		// Arrange
		final var event = new ContractCreatedEvent(CONTRACT_ID, MUNICIPALITY_ID, ContractType.LEASE_AGREEMENT, Status.ACTIVE,
			START_DATE, END_DATE, null, null, InvoicedIn.ADVANCE, IntervalType.QUARTERLY, LeaseType.LAND_LEASE_RESIDENTIAL);

		// Act
		publisher.publish(event);

		// Assert
		verify(clientMock).contractCreated(MUNICIPALITY_ID, event);
	}

	@Test
	void publishContractUpdatedEvent() {
		// Arrange
		final var event = new ContractUpdatedEvent(CONTRACT_ID, MUNICIPALITY_ID, ContractType.LEASE_AGREEMENT, Status.ACTIVE,
			START_DATE, END_DATE, null, null, InvoicedIn.ARREARS, IntervalType.YEARLY, LeaseType.LAND_LEASE_RESIDENTIAL);

		// Act
		publisher.publish(event);

		// Assert
		verify(clientMock).contractUpdated(MUNICIPALITY_ID, event);
	}

	@Test
	void publishContractDeletedEvent() {
		// Arrange
		final var event = new ContractDeletedEvent(CONTRACT_ID, MUNICIPALITY_ID);

		// Act
		publisher.publish(event);

		// Assert
		verify(clientMock).contractDeleted(MUNICIPALITY_ID, event);
	}

	@Test
	void publishContractTerminatedEvent() {
		// Arrange
		final var event = new ContractTerminatedEvent(CONTRACT_ID, MUNICIPALITY_ID, END_DATE, InvoicedIn.ADVANCE, IntervalType.QUARTERLY);

		// Act
		publisher.publish(event);

		// Assert
		verify(clientMock).contractTerminated(MUNICIPALITY_ID, event);
	}
}
