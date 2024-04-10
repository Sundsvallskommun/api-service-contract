package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.sundsvall.contract.model.LeaseFees;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

public class LeaseFeesConverter implements AttributeConverter<LeaseFees, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(final LeaseFees leaseFees) {
        if (leaseFees == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(leaseFees);
        } catch (Exception e) {
            throw new PersistenceException("Unable to serialize lease fees", e);
        }
    }

    @Override
    public LeaseFees convertToEntityAttribute(final String json) {
        return ofNullable(json)
            .filter(not(String::isBlank))
            .map(s -> {
                try {
                    return OBJECT_MAPPER.readValue(json, LeaseFees.class);
                } catch (Exception e) {
                    throw new PersistenceException("Unable to deserialize lease fees", e);
                }
            })
            .orElse(null);
    }
}
