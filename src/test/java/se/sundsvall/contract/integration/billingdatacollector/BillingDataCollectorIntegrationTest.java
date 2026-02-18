package se.sundsvall.contract.integration.billingdatacollector;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static generated.se.sundsvall.billingdatacollector.ScheduledBilling.SourceEnum.CONTRACT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
	void addBillingCycleForContractWithExistingCycleWhenDaysDiffer() {
		// Arrange
		final var id = UUID.randomUUID().toString();
		when(scheduledBillingAnswerMock.getId()).thenReturn(id);
		when(clientMock.getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID)).thenReturn(Optional.of(scheduledBillingAnswerMock));
		when(scheduledBillingAnswerMock.getBillingDaysOfMonth()).thenReturn(Set.of(1));
		when(scheduledBillingRequestMock.getBillingDaysOfMonth()).thenReturn(Set.of(30));

		// Act
		integration.addBillingCycle(MUNICIPALITY_ID, CONTRACT_ID, scheduledBillingRequestMock);

		// Assert and verify
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
		verify(scheduledBillingAnswerMock).getBillingDaysOfMonth();
		verify(scheduledBillingRequestMock).getBillingDaysOfMonth();
		verify(scheduledBillingAnswerMock).getId();
		verify(clientMock).updateScheduledBilling(MUNICIPALITY_ID, id, scheduledBillingRequestMock);
	}

	@Test
	void addBillingCycleForContractWithExistingCycleWhenMonthsDiffer() {
		// Arrange
		final var id = UUID.randomUUID().toString();
		when(scheduledBillingAnswerMock.getId()).thenReturn(id);
		when(clientMock.getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID)).thenReturn(Optional.of(scheduledBillingAnswerMock));
		when(scheduledBillingAnswerMock.getBillingMonths()).thenReturn(Set.of(12));
		when(scheduledBillingRequestMock.getBillingMonths()).thenReturn(Set.of(6));

		// Act
		integration.addBillingCycle(MUNICIPALITY_ID, CONTRACT_ID, scheduledBillingRequestMock);

		// Assert and verify
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
		verify(scheduledBillingAnswerMock).getBillingDaysOfMonth();
		verify(scheduledBillingRequestMock).getBillingDaysOfMonth();
		verify(scheduledBillingAnswerMock).getBillingMonths();
		verify(scheduledBillingRequestMock).getBillingMonths();
		verify(scheduledBillingAnswerMock).getId();
		verify(clientMock).updateScheduledBilling(MUNICIPALITY_ID, id, scheduledBillingRequestMock);
	}

	@ParameterizedTest
	@MethodSource("dayAndMonthsArgumentProvider")
	void addBillingCycleForContractWithExistingCycleWhenNoCycleChange(Set<Integer> days, Set<Integer> months) {
		// Arrange
		when(clientMock.getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID)).thenReturn(Optional.of(scheduledBillingAnswerMock));
		when(scheduledBillingAnswerMock.getBillingDaysOfMonth()).thenReturn(days);
		when(scheduledBillingAnswerMock.getBillingMonths()).thenReturn(months);
		when(scheduledBillingRequestMock.getBillingDaysOfMonth()).thenReturn(days);
		when(scheduledBillingRequestMock.getBillingMonths()).thenReturn(months);

		// Act
		integration.addBillingCycle(MUNICIPALITY_ID, CONTRACT_ID, scheduledBillingRequestMock);

		// Assert and verify
		verify(clientMock).getScheduledBillingByExternalId(MUNICIPALITY_ID, CONTRACT, CONTRACT_ID);
		verify(scheduledBillingAnswerMock).getBillingDaysOfMonth();
		verify(scheduledBillingAnswerMock).getBillingMonths();
		verify(scheduledBillingRequestMock).getBillingDaysOfMonth();
		verify(scheduledBillingRequestMock).getBillingMonths();
		verify(clientMock, never()).updateScheduledBilling(any(), any(), any());
	}

	private static Stream<Arguments> dayAndMonthsArgumentProvider() {
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of(Set.of(15), Set.of(6)),
			Arguments.of(Set.of(1, 2, 3, 4, 5), null),
			Arguments.of(null, Set.of(1, 3, 5, 7, 9))

		);
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
