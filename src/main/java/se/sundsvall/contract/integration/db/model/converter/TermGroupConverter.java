package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.TermGroup;

@Converter(autoApply = true)
public class TermGroupConverter implements AttributeConverter<List<TermGroup>, String> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(final List<TermGroup> termGroup) {
		return ofNullable(termGroup)
			.map(termGroupObj -> {
				try {
					return OBJECT_MAPPER.writeValueAsString(termGroupObj);
				} catch (Exception e) {
					throw new PersistenceException("Unable to serialize term groups", e);
				}
			})
			.orElse(null);
	}

	@Override
	public List<TermGroup> convertToEntityAttribute(final String json) {
		return ofNullable(json)
			.filter(StringUtils::isNotBlank)
			.map(jsonString -> {
				try {
					return OBJECT_MAPPER.readValue(jsonString, new TypeReference<List<TermGroup>>() {});
				} catch (Exception e) {
					throw new PersistenceException("Unable to deserialize term groups", e);
				}
			})
			.orElse(null);
	}
}
