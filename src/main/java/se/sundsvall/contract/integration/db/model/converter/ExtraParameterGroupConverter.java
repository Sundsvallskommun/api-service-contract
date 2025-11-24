package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.ExtraParameterGroup;

@Converter(autoApply = true)
public class ExtraParameterGroupConverter implements AttributeConverter<List<ExtraParameterGroup>, String> {

	private final ObjectMapper objectMapper;

	public ExtraParameterGroupConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public String convertToDatabaseColumn(final List<ExtraParameterGroup> extraParameterGroups) {
		return ofNullable(extraParameterGroups)
			.filter(CollectionUtils::isNotEmpty)
			.map(value -> {
				try {
					return objectMapper.writeValueAsString(value);
				} catch (Exception e) {
					throw new PersistenceException("Unable to serialize extra parameter groups", e);
				}
			})
			.orElse(null);
	}

	@Override
	public List<ExtraParameterGroup> convertToEntityAttribute(final String json) {
		return ofNullable(json)
			.filter(StringUtils::isNotBlank)
			.map(s -> {
				try {
					return objectMapper.readValue(json, new TypeReference<List<ExtraParameterGroup>>() {});
				} catch (Exception e) {
					throw new PersistenceException("Unable to deserialize extra parameter groups", e);
				}
			})
			.orElse(null);
	}
}
