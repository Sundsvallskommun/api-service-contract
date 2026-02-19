package se.sundsvall.contract.integration.db.model.converter.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.InvoicedIn;

import static java.util.Optional.ofNullable;

/**
 * JPA converter for {@link InvoicedIn}.
 */
@Converter(autoApply = true)
public class InvoicedInConverter implements AttributeConverter<InvoicedIn, String> {

	/**
	 * Converts an {@link InvoicedIn} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(InvoicedIn attribute) {
		return ofNullable(attribute)
			.map(InvoicedIn::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to an {@link InvoicedIn} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public InvoicedIn convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(InvoicedIn::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, InvoicedIn.class), e);
		}
	}
}
