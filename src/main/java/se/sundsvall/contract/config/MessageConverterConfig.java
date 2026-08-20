package se.sundsvall.contract.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters.ServerBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se.sundsvall.contract.api.model.AttachmentMetadata;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Web MVC message converter setup.
 */
@Configuration
class MessageConverterConfig implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(final ServerBuilder builder) {
		builder.configureMessageConvertersList(MessageConverterConfig::addJsonPartConverter);
	}

	/**
	 * Appends the {@link JsonPartHttpMessageConverter} that lets an unlabelled JSON multipart part through, wrapping
	 * whichever JSON converter the application is already using so that the two read a body the same way.
	 *
	 * <p>
	 * It goes last on purpose: a converter earlier in the list is the one Spring picks, so everything that was already
	 * being read keeps being read exactly as before, and this one is only reached for a body no other converter wanted.
	 * </p>
	 */
	private static void addJsonPartConverter(final List<HttpMessageConverter<?>> converters) {
		final var jsonConverter = converters.stream()
			.filter(converter -> converter.canRead(AttachmentMetadata.class, APPLICATION_JSON))
			.findFirst();

		jsonConverter.ifPresent(converter -> converters.add(new JsonPartHttpMessageConverter(converter)));
	}
}
