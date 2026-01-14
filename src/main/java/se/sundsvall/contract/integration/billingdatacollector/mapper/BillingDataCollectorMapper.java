package se.sundsvall.contract.integration.billingdatacollector.mapper;

import static generated.se.sundsvall.billingdatacollector.ScheduledBilling.SourceEnum.CONTRACT;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import java.util.Set;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.IntervalType;

public final class BillingDataCollectorMapper {
	private BillingDataCollectorMapper() {
		// Prevent instantiation
	}

	public static ScheduledBilling toScheduledBilling(ContractEntity contractEntity) {
		final var scheduledBilling = new ScheduledBilling();

		scheduledBilling.setExternalId(contractEntity.getContractId());
		scheduledBilling.setSource(CONTRACT);
		scheduledBilling.setBillingDaysOfMonth(Set.of(1)); // Right now, always execute billing at day one of the month
		scheduledBilling.setBillingMonths(calculateBillingMonths(contractEntity.getInvoicing().getInvoiceInterval()));

		return scheduledBilling;
	}

	private static Set<Integer> calculateBillingMonths(IntervalType intervalType) {
		return switch (intervalType) {
			case MONTHLY -> Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
			case QUARTERLY -> Set.of(3, 6, 9, 12);
			case HALF_YEARLY -> Set.of(6, 12);
			case YEARLY -> Set.of(12);
		};
	}
}
