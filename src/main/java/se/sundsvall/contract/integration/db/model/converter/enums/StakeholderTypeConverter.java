package se.sundsvall.contract.integration.db.model.converter.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.StakeholderType;

import static java.util.Optional.ofNullable;

/**
 * JPA converter for {@link StakeholderType}.
 */
@Converter(autoApply = true)
public class StakeholderTypeConverter implements AttributeConverter<StakeholderType, String> {

	/**
	 * Converts a {@link StakeholderType} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(StakeholderType attribute) {
		return ofNullable(attribute)
			.map(StakeholderType::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to a {@link StakeholderType} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public StakeholderType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(StakeholderType::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, StakeholderType.class), e);
		}
	}
}
