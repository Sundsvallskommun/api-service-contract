package se.sundsvall.contract.integration.db.model.converter.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.LeaseholdType;

import static java.util.Optional.ofNullable;

/**
 * JPA converter for {@link LeaseholdType}.
 */
@Converter(autoApply = true)
public class LeaseholdTypeConverter implements AttributeConverter<LeaseholdType, String> {

	/**
	 * Converts a {@link LeaseholdType} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(LeaseholdType attribute) {
		return ofNullable(attribute)
			.map(LeaseholdType::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to a {@link LeaseholdType} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public LeaseholdType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(LeaseholdType::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, LeaseholdType.class), e);
		}
	}
}
