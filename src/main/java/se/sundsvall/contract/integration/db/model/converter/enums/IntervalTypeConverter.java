package se.sundsvall.contract.integration.db.model.converter.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.IntervalType;

import static java.util.Optional.ofNullable;

/**
 * JPA converter for {@link IntervalType}.
 */
@Converter(autoApply = true)
public class IntervalTypeConverter implements AttributeConverter<IntervalType, String> {

	/**
	 * Converts an {@link IntervalType} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(IntervalType attribute) {
		return ofNullable(attribute)
			.map(IntervalType::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to an {@link IntervalType} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public IntervalType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(IntervalType::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, IntervalType.class), e);
		}
	}
}
