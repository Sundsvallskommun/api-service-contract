package se.sundsvall.contract.integration.billingdatacollector;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.contract.integration.billingdatacollector.configuration.BillingDataCollectorConfiguration.CLIENT_ID;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.contract.integration.billingdatacollector.configuration.BillingDataCollectorConfiguration;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.billing-data-collector.base-url}",
	configuration = BillingDataCollectorConfiguration.class,
	dismiss404 = true)
@CircuitBreaker(name = CLIENT_ID)
public interface BillingDataCollectorClient {

	/**
	 * Create a new scheduled billing cycle
	 *
	 * @param  municipalityId   the municipality id of the municipality that owns the contract
	 * @param  scheduledBilling request containing billing cycle information
	 * @return                  an object of type ScheduledBilling representing the created billing cycle information
	 */
	@PostMapping(path = "/{municipalityId}/scheduled-billing", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ScheduledBilling createScheduledBilling(
		@PathVariable final String municipalityId,
		@RequestBody final ScheduledBilling scheduledBilling);

	/**
	 * Read a scheduled billing cycle by external id
	 *
	 * @param  municipalityId the municipality id of the municipality that owns the contract
	 * @param  externalId     the id of the contract
	 * @return                an optional with object of ScheduledBilling class if billing cycle is found, otherwise an
	 *                        empty optional
	 */
	@GetMapping(path = "/{municipalityId}/scheduled-billing/external/{externalId}", produces = APPLICATION_JSON_VALUE)
	Optional<ScheduledBilling> getScheduledBillingByExternalId(
		@PathVariable final String municipalityId,
		@PathVariable final String externalId);

	/**
	 * Update an existing scheduled billing cycle
	 *
	 * @param  municipalityId   the municipality id of the municipality that owns the contract
	 * @param  Id               the id of the billing cycle
	 * @param  scheduledBilling request containing billing cycle information
	 * @return                  an object of type ScheduledBilling representing the updated billing cycle information
	 */
	@PutMapping(path = "/{municipalityId}/scheduled-billing/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ScheduledBilling updateScheduledBilling(
		@PathVariable final String municipalityId,
		@PathVariable final String id,
		@RequestBody final ScheduledBilling scheduledBilling);

	/**
	 * Delete an existing scheduled billing cycle
	 *
	 * @param municipalityId the municipality id of the municipality that owns the contract
	 * @param Id             the id of the billing cycle
	 */
	@PutMapping(path = "/{municipalityId}/scheduled-billing/{id}")
	void deleteScheduledBilling(
		@PathVariable final String municipalityId,
		@PathVariable final String id);
}
