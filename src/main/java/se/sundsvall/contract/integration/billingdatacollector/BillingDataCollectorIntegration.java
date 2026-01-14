package se.sundsvall.contract.integration.billingdatacollector;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import org.springframework.stereotype.Component;

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
	 * @param municipalityId   id of municipality owning the contract
	 * @param contractId       id of contract (in Contract service) to add or update billing cycle for
	 * @param scheduleSettings billing cycle settings to send to BillingDataCollector service
	 */
	public void addBillingCycle(String municipalityId, String contractId, ScheduledBilling scheduleSettings) {
		client.getScheduledBillingByExternalId(municipalityId, contractId)
			.ifPresentOrElse(
				presentSettings -> // An existing schedule exists, perform update of it
				client.updateScheduledBilling(municipalityId, presentSettings.getId(), scheduleSettings),
				() -> // No schedule exists, create a new one
				client.createScheduledBilling(municipalityId, scheduleSettings));
	}

	/**
	 * Method removes the billing cycle connected to the contract if a billing cycle is present
	 * in the BillingDataCollector service.
	 *
	 * @param municipalityId id of municipality owning the contract
	 * @param contractId     id of contract (in Contract service) to add or update billing cycle for
	 */
	public void removeBillingCycle(String municipalityId, String contractId) {
		client.getScheduledBillingByExternalId(municipalityId, contractId)
			.ifPresent(
				presentSettings -> // An existing schedule exists, remove it
				client.deleteScheduledBilling(municipalityId, presentSettings.getId()));
	}
}
