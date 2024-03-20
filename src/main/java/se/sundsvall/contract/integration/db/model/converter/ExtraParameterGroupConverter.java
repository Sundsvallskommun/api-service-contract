package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.sundsvall.contract.model.ExtraParameterGroup;

public class ExtraParameterGroupConverter implements AttributeConverter<List<ExtraParameterGroup>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(final List<ExtraParameterGroup> extraParameterGroups) {
        return ofNullable(extraParameterGroups)
            .map(value -> {
                try {
                    return OBJECT_MAPPER.writeValueAsString(value);
                } catch (Exception e) {
                    throw new PersistenceException("Unable to serialize extra parameter groups", e);
                }
            })
            .orElse(null);
    }

    @Override
    public List<ExtraParameterGroup> convertToEntityAttribute(final String json) {
        return ofNullable(json)
            .filter(not(String::isBlank))
            .map(s -> {
                try {
                    return OBJECT_MAPPER.readValue(json, new TypeReference<List<ExtraParameterGroup>>() {});
                } catch (Exception e) {
                    throw new PersistenceException("Unable to deserialize extra parameter groups", e);
                }
            })
            .orElse(null);
    }
}
