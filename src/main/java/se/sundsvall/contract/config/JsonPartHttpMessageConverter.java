package se.sundsvall.contract.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * Reads a JSON body that arrives without a content type of its own, by handing it to the ordinary JSON converter with
 * the content type it should have had.
 *
 * <p>
 * Every part of a multipart request carries its own {@code Content-Type} header, and most clients leave it off for a
 * plain form field: browsers, {@code curl -F 'name={...}'} and the Swagger UI that API portals are built on all send
 * the JSON metadata part bare. Spring falls back to {@code application/octet-stream} for a body without a content type,
 * finds no converter that reads JSON from it, and answers <em>415 Unsupported Media Type</em> before the controller is
 * ever reached. The same happens for the generic {@code text/plain} that some clients label the field with instead.
 * </p>
 *
 * <p>
 * The delegate is the JSON converter the application has already registered, so a part read through here is
 * deserialized, validated and error-reported exactly like a properly labelled one. Reading is all this converter does -
 * responses are left to the converters that were registered before it.
 * </p>
 */
class JsonPartHttpMessageConverter implements HttpMessageConverter<Object> {

	/**
	 * What a client that does not label its JSON ends up sending: nothing at all, which Spring turns into
	 * {@code application/octet-stream} before any converter is consulted, or one of these two by name.
	 */
	private static final List<MediaType> SUPPORTED_MEDIA_TYPES = List.of(APPLICATION_OCTET_STREAM, TEXT_PLAIN);

	private final HttpMessageConverter<Object> jsonConverter;

	@SuppressWarnings("unchecked")
	JsonPartHttpMessageConverter(final HttpMessageConverter<?> jsonConverter) {
		this.jsonConverter = (HttpMessageConverter<Object>) jsonConverter;
	}

	@Override
	public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
		return isUnlabelledOrGeneric(mediaType) && jsonConverter.canRead(clazz, APPLICATION_JSON);
	}

	/**
	 * Never writes - a response is only ever sent as what the client asked for.
	 */
	@Override
	public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return SUPPORTED_MEDIA_TYPES;
	}

	@Override
	public Object read(final Class<?> clazz, final HttpInputMessage inputMessage) throws IOException {
		return jsonConverter.read(clazz, asJson(inputMessage));
	}

	@Override
	public void write(final Object object, final MediaType contentType, final HttpOutputMessage outputMessage) {
		throw new UnsupportedOperationException("This converter reads unlabelled JSON bodies and does not write responses");
	}

	private static boolean isUnlabelledOrGeneric(final MediaType mediaType) {
		return (mediaType == null) || SUPPORTED_MEDIA_TYPES.stream().anyMatch(supported -> supported.includes(mediaType));
	}

	/**
	 * The very same body, presented as {@code application/json}. A charset the client did state is kept, since the
	 * bytes are encoded the way it says they are and only the type itself was wrong.
	 */
	private static HttpInputMessage asJson(final HttpInputMessage message) {
		final var statedContentType = message.getHeaders().getContentType();
		final var charset = (statedContentType != null) ? statedContentType.getCharset() : null;

		final var headers = HttpHeaders.copyOf(message.getHeaders());
		headers.setContentType((charset != null) ? new MediaType(APPLICATION_JSON, charset) : APPLICATION_JSON);

		return new HttpInputMessage() {

			@Override
			public InputStream getBody() throws IOException {
				return message.getBody();
			}

			@Override
			public HttpHeaders getHeaders() {
				return headers;
			}
		};
	}
}
