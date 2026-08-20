package se.sundsvall.contract.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.model.enums.AttachmentCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.TEXT_PLAIN;

class JsonPartHttpMessageConverterTest {

	private static final String METADATA = """
		{"category":"OTHER","filename":"aFilename","mimeType":"application/pdf","note":"aNote"}""";

	private final HttpMessageConverter<Object> jsonConverter = mock();

	private final JsonPartHttpMessageConverter converter = new JsonPartHttpMessageConverter(jsonConverter);

	/**
	 * The content types a client that does not label its JSON part ends up sending, {@code null} being the part that
	 * carries no Content-Type header at all.
	 */
	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"application/octet-stream", "text/plain", "text/plain;charset=UTF-8"
	})
	void canReadUnlabelledJson(final String contentType) {
		when(jsonConverter.canRead(AttachmentMetadata.class, APPLICATION_JSON)).thenReturn(true);

		assertThat(converter.canRead(AttachmentMetadata.class, parse(contentType))).isTrue();
	}

	/**
	 * A body that says what it is has a converter of its own, registered ahead of this one - stepping in for it would
	 * only mean reading it as something the client did not say it was.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"application/json", "application/pdf", "application/xml", "multipart/form-data"
	})
	void cannotReadALabelledBody(final String contentType) {
		assertThat(converter.canRead(AttachmentMetadata.class, parse(contentType))).isFalse();
		verifyNoInteractions(jsonConverter);
	}

	@Test
	void cannotReadWhatTheJsonConverterCannotRead() {
		when(jsonConverter.canRead(AttachmentMetadata.class, APPLICATION_JSON)).thenReturn(false);

		assertThat(converter.canRead(AttachmentMetadata.class, null)).isFalse();
	}

	@Test
	void readsAnUnlabelledBodyThroughTheJsonConverter() throws IOException {
		// Arrange - the real JSON converter, to show that this ends in a deserialized object and not just a delegation
		final var realConverter = new JsonPartHttpMessageConverter(new JacksonJsonHttpMessageConverter());

		// Act
		final var result = realConverter.read(AttachmentMetadata.class, new MockHttpInputMessage(METADATA.getBytes(StandardCharsets.UTF_8)));

		// Assert
		assertThat(result).isEqualTo(AttachmentMetadata.builder()
			.withCategory(AttachmentCategory.OTHER)
			.withFilename("aFilename")
			.withMimeType("application/pdf")
			.withNote("aNote")
			.build());
	}

	/**
	 * Only the content type is rewritten: the bytes are handed on untouched, a charset the client did state is kept,
	 * and the headers the part came with are still there.
	 */
	@Test
	void readPassesTheBodyOnAsJson() throws IOException {
		// Arrange
		final var body = METADATA.getBytes(StandardCharsets.ISO_8859_1);
		final var message = new MockHttpInputMessage(body);
		message.getHeaders().setContentType(new MediaType("text", "plain", StandardCharsets.ISO_8859_1));
		message.getHeaders().setContentDispositionFormData("attachment", null);
		final var metadata = AttachmentMetadata.builder().withFilename("aFilename").build();
		when(jsonConverter.read(any(), any())).thenReturn(metadata);

		// Act
		final var result = converter.read(AttachmentMetadata.class, message);

		// Assert
		final var captor = ArgumentCaptor.forClass(HttpInputMessage.class);
		verify(jsonConverter).read(eq(AttachmentMetadata.class), captor.capture());
		assertThat(result).isSameAs(metadata);
		assertThat(captor.getValue().getHeaders().getContentType())
			.isEqualTo(new MediaType("application", "json", StandardCharsets.ISO_8859_1));
		assertThat(captor.getValue().getHeaders().getFirst(CONTENT_DISPOSITION)).isEqualTo("form-data; name=\"attachment\"");
		assertThat(captor.getValue().getBody().readAllBytes()).isEqualTo(body);
	}

	@Test
	void neverWrites() {
		assertThat(converter.canWrite(AttachmentMetadata.class, APPLICATION_JSON)).isFalse();
		assertThat(converter.canWrite(AttachmentMetadata.class, null)).isFalse();
		assertThatExceptionOfType(UnsupportedOperationException.class)
			.isThrownBy(() -> converter.write(new AttachmentMetadata(), APPLICATION_JSON, new MockHttpOutputMessage()));
		verifyNoInteractions(jsonConverter);
	}

	@Test
	void supportsTheContentTypesAnUnlabelledPartArrivesAs() {
		assertThat(converter.getSupportedMediaTypes()).containsExactly(APPLICATION_OCTET_STREAM, TEXT_PLAIN);
	}

	private static MediaType parse(final String contentType) {
		return (contentType != null) ? MediaType.parseMediaType(contentType) : null;
	}
}
