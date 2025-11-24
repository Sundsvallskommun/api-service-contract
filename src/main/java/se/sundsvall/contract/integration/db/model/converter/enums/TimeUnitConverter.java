package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.TimeUnit;

@Converter(autoApply = true)
public class TimeUnitConverter implements AttributeConverter<TimeUnit, String> {

	@Override
	public String convertToDatabaseColumn(TimeUnit attribute) {
		return ofNullable(attribute)
			.map(TimeUnit::name)
			.orElse(null);
	}

	@Override
	public TimeUnit convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(TimeUnit::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + TimeUnit.class, e);
		}
	}
}
