package se.sundsvall.contract.integration.billingdatacollector;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.contract.integration.billingdatacollector.configuration.BillingDataCollectorConfiguration;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractCreatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractDeletedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractTerminatedEvent;
import se.sundsvall.contract.integration.billingdatacollector.event.ContractUpdatedEvent;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.contract.integration.billingdatacollector.configuration.BillingDataCollectorConfiguration.CLIENT_ID;

/**
 * Feign client for the BillingDataCollector service API.
 */
@FeignClient(
	name = CLIENT_ID,
	url = "${integration.billing-data-collector.base-url}",
	configuration = BillingDataCollectorConfiguration.class,
	dismiss404 = true)
@CircuitBreaker(name = CLIENT_ID)
public interface BillingDataCollectorClient {

	/**
	 * Notify billing that a contract has been created
	 *
	 * @param municipalityId the municipality id of the municipality that owns the contract
	 * @param event          the contract created event payload
	 */
	@PostMapping(path = "/{municipalityId}/contracts/created", consumes = APPLICATION_JSON_VALUE)
	void contractCreated(
		@PathVariable final String municipalityId,
		@RequestBody final ContractCreatedEvent event);

	/**
	 * Notify billing that a contract has been updated
	 *
	 * @param municipalityId the municipality id of the municipality that owns the contract
	 * @param event          the contract updated event payload
	 */
	@PostMapping(path = "/{municipalityId}/contracts/updated", consumes = APPLICATION_JSON_VALUE)
	void contractUpdated(
		@PathVariable final String municipalityId,
		@RequestBody final ContractUpdatedEvent event);

	/**
	 * Notify billing that a contract has been deleted
	 *
	 * @param municipalityId the municipality id of the municipality that owns the contract
	 * @param event          the contract deleted event payload
	 */
	@PostMapping(path = "/{municipalityId}/contracts/deleted", consumes = APPLICATION_JSON_VALUE)
	void contractDeleted(
		@PathVariable final String municipalityId,
		@RequestBody final ContractDeletedEvent event);

	/**
	 * Notify billing that a contract has been terminated
	 *
	 * @param municipalityId the municipality id of the municipality that owns the contract
	 * @param event          the termination event payload
	 */
	@PostMapping(path = "/{municipalityId}/contracts/terminated", consumes = APPLICATION_JSON_VALUE)
	void contractTerminated(
		@PathVariable final String municipalityId,
		@RequestBody final ContractTerminatedEvent event);
}
