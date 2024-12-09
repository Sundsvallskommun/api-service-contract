package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import java.util.List;
import se.sundsvall.contract.model.TermGroup;

public class TermGroupConverter implements AttributeConverter<List<TermGroup>, String> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(final List<TermGroup> termGroup) {
		return ofNullable(termGroup)
			.map(termGroups -> {
				try {
					return OBJECT_MAPPER.writeValueAsString(termGroup);
				} catch (Exception e) {
					throw new PersistenceException("Unable to serialize term groups", e);
				}
			})
			.orElse(null);
	}

	@Override
	public List<TermGroup> convertToEntityAttribute(final String json) {
		return ofNullable(json)
			.filter(not(String::isBlank))
			.map(s -> {
				try {
					return OBJECT_MAPPER.readValue(json, new TypeReference<List<TermGroup>>() {});
				} catch (Exception e) {
					throw new PersistenceException("Unable to deserialize term groups", e);
				}
			})
			.orElse(null);
	}
}
