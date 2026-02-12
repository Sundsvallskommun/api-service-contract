package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.AddressType;

/**
 * JPA converter for {@link AddressType}.
 */
@Converter(autoApply = true)
public class AddressTypeConverter implements AttributeConverter<AddressType, String> {

	/**
	 * Converts a {@link AddressType} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(AddressType attribute) {
		return ofNullable(attribute)
			.map(AddressType::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to a {@link AddressType} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public AddressType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(AddressType::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, AddressType.class), e);
		}
	}
}
