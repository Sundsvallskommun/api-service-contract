package se.sundsvall.contract.config;

import org.openapitools.jackson.nullable.JsonNullable;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

/**
 * Jackson 3 deserializer for {@link JsonNullable} fields. Together with the contextual handling it distinguishes the
 * three PATCH states:
 * <ul>
 * <li>a property that is absent from the payload never reaches this deserializer (the setter is not called), so the
 * field keeps its default {@code undefined()} value;</li>
 * <li>a property explicitly set to {@code null} resolves to {@link #getNullValue} → {@code JsonNullable.of(null)};</li>
 * <li>a property set to a value is wrapped as {@code JsonNullable.of(value)}.</li>
 * </ul>
 */
class JsonNullableDeserializer extends StdDeserializer<JsonNullable<?>> {

	private final transient ValueDeserializer<?> valueDeserializer;

	JsonNullableDeserializer() {
		super(JsonNullable.class);
		this.valueDeserializer = null;
	}

	private JsonNullableDeserializer(final ValueDeserializer<?> valueDeserializer) {
		super(JsonNullable.class);
		this.valueDeserializer = valueDeserializer;
	}

	@Override
	public ValueDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) {
		if (property == null) {
			return this;
		}
		final JavaType wrapperType = property.getType();
		final JavaType containedType = wrapperType.containedTypeCount() > 0
			? wrapperType.containedType(0)
			: ctxt.constructType(Object.class);
		return new JsonNullableDeserializer(ctxt.findContextualValueDeserializer(containedType, property));
	}

	@Override
	public JsonNullable<?> deserialize(final JsonParser parser, final DeserializationContext ctxt) {
		return JsonNullable.of(valueDeserializer.deserialize(parser, ctxt));
	}

	@Override
	public JsonNullable<?> getNullValue(final DeserializationContext ctxt) {
		return JsonNullable.of(null);
	}
}
