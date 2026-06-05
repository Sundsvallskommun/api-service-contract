package se.sundsvall.contract.config;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

/**
 * Registers Jackson 3 handling for {@code JsonNullable<T>} fields (used by
 * {@link se.sundsvall.contract.api.model.PatchContract}) so that a PATCH payload can distinguish an absent field from
 * one that is explicitly set to {@code null}.
 */
@Configuration
class JacksonConfig {

	@Bean
	JacksonModule jsonNullableModule() {
		final var module = new SimpleModule("JsonNullableModule");
		module.addDeserializer(JsonNullable.class, new JsonNullableDeserializer());
		return module;
	}
}
