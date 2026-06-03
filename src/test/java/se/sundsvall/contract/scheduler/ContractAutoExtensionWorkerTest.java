package se.sundsvall.contract.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractAutoExtensionWorkerTest {

	private static final String CONTRACT_ID = "CONTRACT-1";
	private static final String MUNICIPALITY_ID = "2281";
	private static final LocalDate PERIOD_START = LocalDate.of(2021, 1, 1);
	private static final LocalDate PERIOD_END = LocalDate.of(2026, 12, 31);

	@Mock
	private ContractRepository contractRepositoryMock;

	@Mock
	private OutboxRepository outboxRepositoryMock;

	private ContractAutoExtensionWorker worker;

	@BeforeEach
	void setUp() {
		worker = new ContractAutoExtensionWorker(contractRepositoryMock, outboxRepositoryMock, new ObjectMapper());
	}

	@Test
	void extendByYears() {
		// Arrange – 5 years extension, no endDate cap
		final var contract = buildContract(5, TimeUnit.YEARS, null);

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(LocalDate.of(2027, 1, 1));
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(LocalDate.of(2031, 12, 31));
		assertThat(contract.getStatus()).isEqualTo(Status.ACTIVE);
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void extendByMonths() {
		// Arrange – 6 months extension
		final var contract = buildContract(6, TimeUnit.MONTHS, null);

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(LocalDate.of(2027, 1, 1));
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(LocalDate.of(2027, 6, 30));
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void extendByDays() {
		// Arrange – 90 days extension
		final var contract = buildContract(90, TimeUnit.DAYS, null);

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(LocalDate.of(2027, 1, 1));
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(LocalDate.of(2027, 3, 31));
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void extendCappedByEndDate() {
		// Arrange – 5 years extension, but endDate = 2029-03-14 (before extended end)
		final var endDate = LocalDate.of(2029, 3, 14);
		final var contract = buildContract(5, TimeUnit.YEARS, endDate);

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(LocalDate.of(2027, 1, 1));
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(endDate);
		assertThat(contract.getStatus()).isEqualTo(Status.ACTIVE);
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void extendNotCappedWhenEndDateAfterNewPeriodEnd() {
		// Arrange – endDate is after the extended end, so no cap should apply
		final var endDate = LocalDate.of(2035, 6, 30);
		final var contract = buildContract(5, TimeUnit.YEARS, endDate);

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(LocalDate.of(2031, 12, 31));
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void extendRollsForwardWhenNewEndIsToday() {
		// 1-day extension; job picks up yesterday's end → newEnd = today, should roll one more period
		final var contract = ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(Status.ACTIVE)
			.withAutoExtend(true)
			.withCurrentPeriodStartDate(LocalDate.now().minusDays(2))
			.withCurrentPeriodEndDate(LocalDate.now().minusDays(1))
			.withLeaseExtension(1)
			.withLeaseExtensionUnit(TimeUnit.DAYS)
			.build();

		worker.extend(contract);

		// A period ending today is still valid today; newEnd == today is accepted (>= today)
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(LocalDate.now());
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(LocalDate.now());
		assertThat(contract.getStatus()).isEqualTo(Status.ACTIVE);
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void extendRollsForwardMultiplePeriodsOnDowntime() {
		// Job was down; currentPeriodEnd is 5 days in the past with a 1-day extension interval
		final var contract = ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(Status.ACTIVE)
			.withAutoExtend(true)
			.withCurrentPeriodStartDate(LocalDate.now().minusDays(6))
			.withCurrentPeriodEndDate(LocalDate.now().minusDays(5))
			.withLeaseExtension(1)
			.withLeaseExtensionUnit(TimeUnit.DAYS)
			.build();

		worker.extend(contract);

		// Rolls forward until newEnd >= today; with 1-day steps the final period is [today, today]
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(LocalDate.now());
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(LocalDate.now());
		assertThat(contract.getStatus()).isEqualTo(Status.ACTIVE);
		verify(contractRepositoryMock).save(contract);
		verifyNoInteractions(outboxRepositoryMock);
	}

	@Test
	void terminatesWhenEndDateHasPassed() {
		// Arrange – endDate is in the past so the extension cannot reach a future date
		final var endDate = LocalDate.now().minusDays(1);
		final var contract = buildContract(5, TimeUnit.YEARS, endDate);
		final var outboxCaptor = ArgumentCaptor.forClass(OutboxEntity.class);

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getStatus()).isEqualTo(Status.TERMINATED);
		verify(contractRepositoryMock).save(contract);
		verify(outboxRepositoryMock).save(outboxCaptor.capture());

		final var outboxEntity = outboxCaptor.getValue();
		assertThat(outboxEntity.getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(outboxEntity.getEventType()).isEqualTo("TERMINATED");
	}

	@Test
	void terminatesOutboxPayloadIsCorrect() throws Exception {
		// Arrange
		final var endDate = LocalDate.now().minusDays(1);
		final var contract = buildContract(5, TimeUnit.YEARS, endDate);
		final var outboxCaptor = ArgumentCaptor.forClass(OutboxEntity.class);

		// Act
		worker.extend(contract);

		// Assert
		verify(outboxRepositoryMock).save(outboxCaptor.capture());
		final var event = new ObjectMapper().readValue(outboxCaptor.getValue().getPayload(), ContractTerminatedEvent.class);
		assertThat(event.id()).isEqualTo(CONTRACT_ID);
		assertThat(event.municipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	@Test
	void terminatesShouldThrowIllegalStateWhenSerializationFails() throws Exception {
		// Arrange
		final var endDate = LocalDate.now().minusDays(1);
		final var contract = buildContract(5, TimeUnit.YEARS, endDate);
		final var failingObjectMapper = mock(ObjectMapper.class);
		when(failingObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});
		final var failingWorker = new ContractAutoExtensionWorker(contractRepositoryMock, outboxRepositoryMock, failingObjectMapper);

		// Act & Assert
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> failingWorker.extend(contract))
			.withMessageContaining("Failed to serialize TERMINATED event for contract")
			.withMessageContaining(CONTRACT_ID);
	}

	@Test
	void extendSkipsWhenLeaseExtensionIsNull() {
		// Arrange
		final var contract = ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withStatus(Status.ACTIVE)
			.withAutoExtend(true)
			.withCurrentPeriodStartDate(PERIOD_START)
			.withCurrentPeriodEndDate(PERIOD_END)
			.withLeaseExtension(null)
			.withLeaseExtensionUnit(TimeUnit.YEARS)
			.build();

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(PERIOD_START);
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(PERIOD_END);
		verifyNoInteractions(contractRepositoryMock, outboxRepositoryMock);
	}

	@Test
	void extendSkipsWhenLeaseExtensionUnitIsNull() {
		// Arrange
		final var contract = ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withStatus(Status.ACTIVE)
			.withAutoExtend(true)
			.withCurrentPeriodStartDate(PERIOD_START)
			.withCurrentPeriodEndDate(PERIOD_END)
			.withLeaseExtension(5)
			.withLeaseExtensionUnit(null)
			.build();

		// Act
		worker.extend(contract);

		// Assert
		assertThat(contract.getCurrentPeriodStartDate()).isEqualTo(PERIOD_START);
		assertThat(contract.getCurrentPeriodEndDate()).isEqualTo(PERIOD_END);
		verifyNoInteractions(contractRepositoryMock, outboxRepositoryMock);
	}

	private ContractEntity buildContract(final Integer leaseExtension, final TimeUnit unit, final LocalDate endDate) {
		return ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withStatus(Status.ACTIVE)
			.withAutoExtend(true)
			.withCurrentPeriodStartDate(PERIOD_START)
			.withCurrentPeriodEndDate(PERIOD_END)
			.withLeaseExtension(leaseExtension)
			.withLeaseExtensionUnit(unit)
			.withEndDate(endDate)
			.build();
	}
}
