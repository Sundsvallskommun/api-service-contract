package se.sundsvall.contract.integration.billingdatacollector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.billing-data-collector")
public record BillingDataCollectorProperties(int connectTimeout, int readTimeout) {
}
