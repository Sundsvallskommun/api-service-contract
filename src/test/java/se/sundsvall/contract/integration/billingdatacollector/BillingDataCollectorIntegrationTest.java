package se.sundsvall.contract.integration.billingdatacollector;

import static generated.se.sundsvall.billingdatacollector.ScheduledBilling.SourceEnum.CONTRACT;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BillingDataCollectorIntegrationTest {
	private static final String MUNICIPALITY_ID = "municipalityId";
	private static final String CONTRACT_ID = "contractId";

	@Mock
	private BillingDataCollectorClient clientMock;

	@Mock
	private ScheduledBilling scheduledBillingRequestMock;

	@Mock
	private ScheduledBilling scheduledBillingAnswerMock;

	@InjectMocks
	private BillingDataCollectorIntegration integration;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(
			clientMock,
			scheduledBillingAnswerMock,
			scheduledBillingRequestMock);
	}

	@Test
	void addBillingCycleForContractWithExistingCycle() {
		// Arrange
		final var id = UUID.randomUUID().toString();
		when(scheduledBillingAnswerMock.getId()).thenReturn(id);
		when(clientMock.getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID)).thenReturn(Optional.of(scheduledBillingAnswerMock));

		// Act
		integration.addBillingCycle(MUNICIPALITY_ID, CONTRACT_ID, scheduledBillingRequestMock);

		// Assert and verify
		verify(scheduledBillingAnswerMock).getId();
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
		verify(clientMock).updateScheduledBilling(MUNICIPALITY_ID, id, scheduledBillingRequestMock);
	}

	@Test
	void addBillingCycleForContractWithNonExistingCycle() {
		// Act
		integration.addBillingCycle(MUNICIPALITY_ID, CONTRACT_ID, scheduledBillingRequestMock);

		// Assert and verify
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
		verify(clientMock).createScheduledBilling(MUNICIPALITY_ID, scheduledBillingRequestMock);
	}

	@Test
	void removeBillingCycleForContractWithExistingCycle() {
		// Arrange
		final var id = UUID.randomUUID().toString();
		when(scheduledBillingAnswerMock.getId()).thenReturn(id);
		when(clientMock.getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID)).thenReturn(Optional.of(scheduledBillingAnswerMock));

		// Act
		integration.removeBillingCycle(MUNICIPALITY_ID, CONTRACT_ID);

		// Assert and verify
		verify(scheduledBillingAnswerMock).getId();
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
		verify(clientMock).deleteScheduledBilling(MUNICIPALITY_ID, id);
	}

	@Test
	void removeBillingCycleForContractWitNonExistingCycle() {
		// Act
		integration.removeBillingCycle(MUNICIPALITY_ID, CONTRACT_ID);

		// Assert and verify
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
	}
}
