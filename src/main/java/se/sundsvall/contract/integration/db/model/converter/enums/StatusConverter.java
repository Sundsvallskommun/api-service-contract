package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.Status;

/**
 * JPA converter for {@link Status}.
 */
@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

	/**
	 * Converts a {@link Status} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(Status attribute) {
		return ofNullable(attribute)
			.map(Status::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to a {@link Status} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public Status convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(Status::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, Status.class), e);
		}
	}
}
