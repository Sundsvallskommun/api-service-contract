package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.IntervalType;

public class IntervalTypeConverter implements AttributeConverter<IntervalType, String> {

	@Override
	public String convertToDatabaseColumn(IntervalType attribute) {
		return ofNullable(attribute)
			.map(IntervalType::name)
			.orElse(null);
	}

	@Override
	public IntervalType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(IntervalType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + IntervalType.class, e);
		}
	}
}
