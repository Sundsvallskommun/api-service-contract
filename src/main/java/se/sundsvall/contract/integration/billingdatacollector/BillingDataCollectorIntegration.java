package se.sundsvall.contract.integration.billingdatacollector;

import static generated.se.sundsvall.billingdatacollector.ScheduledBilling.SourceEnum.CONTRACT;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration component for managing scheduled billing cycles via the BillingDataCollector service.
 */
@Component
public class BillingDataCollectorIntegration {

	private final BillingDataCollectorClient client;

	public BillingDataCollectorIntegration(BillingDataCollectorClient client) {
		this.client = client;
	}

	/**
	 * Method adds or updates the billing cycle for the contract in the BillingDataCollector service. If a billing cycle
	 * is present it will be replaced, else a new billing cycle will be created.
	 *
	 * @param municipalityId id of municipality owning the contract
	 * @param contractId     id of contract (in Contract service) to add or update billing cycle for
	 * @param futureSettings billing cycle settings to send to BillingDataCollector service
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void addBillingCycle(String municipalityId, String contractId, ScheduledBilling futureSettings) {
		client.getScheduledBillingByExternalId(municipalityId, CONTRACT, contractId)
			.ifPresentOrElse(
				presentSettings -> {// An existing schedule exists, perform update of it if cycle settings has changed
					if (differs(presentSettings, futureSettings)) {
						client.updateScheduledBilling(municipalityId, presentSettings.getId(), futureSettings);
					}
				}, () -> // No schedule exists, create a new one
				client.createScheduledBilling(municipalityId, futureSettings));
	}

	/**
	 * Method for determining if billing cycle has been changed
	 *
	 * @param  presentSettings        present cycle settings for the contract
	 * @param  nullableFutureSettings proposed cycle settings for the contract
	 * @return                        true if current and future cycle settings differ, false otherwise
	 */
	boolean differs(ScheduledBilling presentSettings, ScheduledBilling nullableFutureSettings) {
		return ofNullable(nullableFutureSettings)
			.map(futureSettings -> !Objects.equals(futureSettings.getBillingDaysOfMonth(), presentSettings.getBillingDaysOfMonth()) ||
				!Objects.equals(futureSettings.getBillingMonths(), presentSettings.getBillingMonths()))
			.orElse(false);
	}

	/**
	 * Method removes the billing cycle connected to the contract if a billing cycle is present
	 * in the BillingDataCollector service.
	 *
	 * @param municipalityId id of municipality owning the contract
	 * @param contractId     id of contract (in Contract service) to add or update billing cycle for
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void removeBillingCycle(String municipalityId, String contractId) {
		client.getScheduledBillingByExternalId(municipalityId, CONTRACT, contractId)
			.ifPresent(
				presentSettings -> // An existing schedule exists, remove it
				client.deleteScheduledBilling(municipalityId, presentSettings.getId()));
	}
}
