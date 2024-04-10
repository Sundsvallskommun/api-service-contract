package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import org.apache.commons.lang3.StringUtils;

import se.sundsvall.contract.model.enums.Status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

public class StatusConverter implements AttributeConverter<Status, String> {

	@Override
	public String convertToDatabaseColumn(Status attribute) {
		return ofNullable(attribute)
			.map(Status::name)
			.orElse(null);
	}

	@Override
	public Status convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(Status::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + Status.class, e);
		}
	}
}
