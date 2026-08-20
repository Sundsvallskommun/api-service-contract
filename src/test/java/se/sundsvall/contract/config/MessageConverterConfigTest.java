package se.sundsvall.contract.config;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import se.sundsvall.contract.api.model.AttachmentMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class MessageConverterConfigTest {

	private final MessageConverterConfig messageConverterConfig = new MessageConverterConfig();

	/**
	 * Last in the list, so that every body that was already being read is still read by the converter that read it
	 * before, and only a body no other converter wanted reaches this one.
	 */
	@Test
	void appendsTheJsonPartConverterAfterTheDefaultOnes() {
		final var builder = HttpMessageConverters.forServer().registerDefaults();

		messageConverterConfig.configureMessageConverters(builder);

		final var converters = build(builder);
		assertThat(converters.getLast()).isInstanceOf(JsonPartHttpMessageConverter.class);
		assertThat(converters).filteredOn(JsonPartHttpMessageConverter.class::isInstance).hasSize(1);
		// The wrapped converter is the one that reads the application's JSON, which is what makes this true
		assertThat(converters.getLast().canRead(AttachmentMetadata.class, null)).isTrue();
	}

	@Test
	void addsNothingWhenThereIsNoJsonConverterToWrap() {
		final var builder = HttpMessageConverters.forServer().disableDefaults();

		messageConverterConfig.configureMessageConverters(builder);

		assertThat(build(builder)).noneMatch(JsonPartHttpMessageConverter.class::isInstance);
	}

	private static ArrayList<HttpMessageConverter<?>> build(final HttpMessageConverters.ServerBuilder builder) {
		final var converters = new ArrayList<HttpMessageConverter<?>>();
		builder.build().forEach(converters::add);
		return converters;
	}
}
