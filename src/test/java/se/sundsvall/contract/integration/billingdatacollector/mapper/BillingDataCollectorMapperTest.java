package se.sundsvall.contract.integration.billingdatacollector.mapper;

import static generated.se.sundsvall.billingdatacollector.ScheduledBilling.SourceEnum.CONTRACT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.model.enums.IntervalType.HALF_YEARLY;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.model.enums.IntervalType;

@ExtendWith(MockitoExtension.class)
class BillingDataCollectorMapperTest {

	@Mock
	private ContractEntity contractEntityMock;

	@Mock
	private InvoicingEmbeddable invoicingEmbeddableMock;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(
			contractEntityMock,
			invoicingEmbeddableMock);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("toScheduledBillingArgumentProvider")
	void toScheduledBilling(String description, IntervalType interval, Set<Integer> expectedDays, Set<Integer> expectedMonths) {
		// Arrange
		final var randomId = UUID.randomUUID().toString();

		when(contractEntityMock.getContractId()).thenReturn(randomId);
		when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);
		when(invoicingEmbeddableMock.getInvoiceInterval()).thenReturn(interval);

		// Act
		final var scheduledBilling = BillingDataCollectorMapper.toScheduledBilling(contractEntityMock);

		// Assert and verify
		assertThat(scheduledBilling).isNotNull().hasAllNullFieldsOrPropertiesExcept("source", "externalId", "billingDaysOfMonth", "billingMonths", "paused");
		assertThat(scheduledBilling.getBillingDaysOfMonth()).isEqualTo(expectedDays);
		assertThat(scheduledBilling.getBillingMonths()).isEqualTo(expectedMonths);
		assertThat(scheduledBilling.getExternalId()).isEqualTo(randomId);
		assertThat(scheduledBilling.getPaused()).isFalse();
		assertThat(scheduledBilling.getSource()).isEqualTo(CONTRACT);
		verify(contractEntityMock).getContractId();
		verify(contractEntityMock).getInvoicing();
		verify(invoicingEmbeddableMock).getInvoiceInterval();
	}

	private static final Stream<Arguments> toScheduledBillingArgumentProvider() {
		return Stream.of(
			Arguments.of("Agreement with monthly billing cycle", MONTHLY, Set.of(1), Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)),
			Arguments.of("Agreement with quarterly billing cycle", QUARTERLY, Set.of(1), Set.of(3, 6, 9, 12)),
			Arguments.of("Agreement with half yearly billing cycle", HALF_YEARLY, Set.of(1), Set.of(6, 12)),
			Arguments.of("Agreement with yearly billing cycle", YEARLY, Set.of(1), Set.of(12)));
	}

	@Test
	void toScheduledBillingWithNullAsInvoicing() {
		// Arrange
		final var randomId = UUID.randomUUID().toString();

		when(contractEntityMock.getContractId()).thenReturn(randomId);

		// Act
		final var e = assertThrows(IllegalStateException.class, () -> BillingDataCollectorMapper.toScheduledBilling(contractEntityMock));

		// Assert and verify
		assertThat(e.getClass()).isEqualTo(IllegalStateException.class);
		assertThat(e.getMessage()).isEqualTo("Interval type is not defined for contract with id %s".formatted(randomId));
		verify(contractEntityMock).getInvoicing();
	}

	@Test
	void toScheduledBillingWithNullAsIntervalType() {
		// Arrange
		final var randomId = UUID.randomUUID().toString();

		when(contractEntityMock.getContractId()).thenReturn(randomId);
		when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);

		// Act
		final var e = assertThrows(IllegalStateException.class, () -> BillingDataCollectorMapper.toScheduledBilling(contractEntityMock));

		// Assert and verify
		assertThat(e.getClass()).isEqualTo(IllegalStateException.class);
		assertThat(e.getMessage()).isEqualTo("Interval type is not defined for contract with id %s".formatted(randomId));
		verify(contractEntityMock).getInvoicing();
		verify(invoicingEmbeddableMock).getInvoiceInterval();
	}
}
