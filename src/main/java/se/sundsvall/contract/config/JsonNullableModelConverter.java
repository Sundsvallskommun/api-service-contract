package se.sundsvall.contract.config;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Iterator;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

/**
 * springdoc/swagger model converter that unwraps {@code JsonNullable<T>} to {@code T} when generating the OpenAPI
 * schema, so that {@link se.sundsvall.contract.api.model.PatchContract} exposes the underlying field types instead of a
 * leaked {@code JsonNullable...} wrapper schema. The PATCH null-clears-the-field semantics are documented on the
 * PatchContract schema itself.
 */
@Component
class JsonNullableModelConverter implements ModelConverter {

	@Override
	public Schema resolve(final AnnotatedType type, final ModelConverterContext context, final Iterator<ModelConverter> chain) {
		final JavaType javaType = Json.mapper().constructType(type.getType());
		if (javaType != null && JsonNullable.class.isAssignableFrom(javaType.getRawClass()) && javaType.containedTypeCount() > 0) {
			final var unwrapped = new AnnotatedType(javaType.containedType(0))
				.ctxAnnotations(type.getCtxAnnotations())
				.parent(type.getParent())
				.schemaProperty(type.isSchemaProperty())
				.name(type.getName())
				.propertyName(type.getPropertyName())
				.resolveAsRef(type.isResolveAsRef())
				.jsonViewAnnotation(type.getJsonViewAnnotation())
				.skipSchemaName(type.isSkipSchemaName());
			return context.resolve(unwrapped);
		}
		return chain.hasNext() ? chain.next().resolve(type, context, chain) : null;
	}
}
