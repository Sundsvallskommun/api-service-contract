package se.sundsvall.contract.integration.billingdatacollector.mapper;

import static generated.se.sundsvall.billingdatacollector.ScheduledBilling.SourceEnum.CONTRACT;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import java.util.Set;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.model.enums.IntervalType;

/**
 * Mapper for converting {@link ContractEntity} objects to BillingDataCollector API objects.
 */
public final class BillingDataCollectorMapper {
	private static final Set<Integer> SET_OF_MONTH = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
	private static final Set<Integer> SET_OF_QUARTER = Set.of(3, 6, 9, 12);
	private static final Set<Integer> SET_OF_HALF_YEAR = Set.of(6, 12);
	private static final Set<Integer> SET_OF_YEAR = Set.of(12);

	private BillingDataCollectorMapper() {
		// Prevent instantiation
	}

	/**
	 * Converts a {@link ContractEntity} to a {@link ScheduledBilling} for the BillingDataCollector API.
	 *
	 * @param  contractEntity the contract entity to convert
	 * @return                the scheduled billing object with billing months derived from the contract's invoice interval
	 */
	public static ScheduledBilling toScheduledBilling(ContractEntity contractEntity) {
		final var billingMonths = ofNullable(contractEntity.getInvoicing())
			.map(InvoicingEmbeddable::getInvoiceInterval)
			.map(BillingDataCollectorMapper::calculateBillingMonths)
			.orElseThrow(() -> new IllegalStateException("Interval type is not defined for contract with id %s".formatted(contractEntity.getContractId())));

		final var scheduledBilling = new ScheduledBilling();

		scheduledBilling.setExternalId(contractEntity.getContractId());
		scheduledBilling.setSource(CONTRACT);
		scheduledBilling.setBillingDaysOfMonth(Set.of(1)); // Right now, always execute billing at day one of the month
		scheduledBilling.setBillingMonths(billingMonths);

		return scheduledBilling;
	}

	private static Set<Integer> calculateBillingMonths(IntervalType intervalType) {
		return switch (intervalType) {
			case MONTHLY -> SET_OF_MONTH;
			case QUARTERLY -> SET_OF_QUARTER;
			case HALF_YEARLY -> SET_OF_HALF_YEAR;
			case YEARLY -> SET_OF_YEAR;
		};
	}
}
