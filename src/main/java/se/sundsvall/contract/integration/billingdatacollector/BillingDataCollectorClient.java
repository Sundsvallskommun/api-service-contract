package se.sundsvall.contract.integration.billingdatacollector;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.contract.integration.billingdatacollector.configuration.BillingDataCollectorConfiguration;
import se.sundsvall.contract.integration.billingdatacollector.event.BillingEvent;

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
	 * Notify billing about a contract event. Billing fetches the contract data itself
	 * via GET /{municipalityId}/contracts/{id} using the provided id and municipalityId.
	 *
	 * @param municipalityId the municipality id
	 * @param event          the billing event payload
	 */
	@PostMapping(path = "/{municipalityId}/{source}/events", consumes = APPLICATION_JSON_VALUE)
	void sendEvent(
		@PathVariable final String municipalityId,
		@PathVariable final BillingSource source,
		@RequestBody final BillingEvent event);
}
