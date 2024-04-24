package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.sundsvall.contract.model.Fees;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

public class FeesConverter implements AttributeConverter<Fees, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(final Fees fees) {
        if (fees == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(fees);
        } catch (Exception e) {
            throw new PersistenceException("Unable to serialize fees", e);
        }
    }

    @Override
    public Fees convertToEntityAttribute(final String json) {
        return ofNullable(json)
            .filter(not(String::isBlank))
            .map(s -> {
                try {
                    return OBJECT_MAPPER.readValue(json, Fees.class);
                } catch (Exception e) {
                    throw new PersistenceException("Unable to deserialize fees", e);
                }
            })
            .orElse(null);
    }
}
