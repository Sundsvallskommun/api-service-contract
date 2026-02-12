package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.AttachmentCategory;

/**
 * JPA converter for {@link AttachmentCategory}.
 */
@Converter(autoApply = true)
public class AttachmentCategoryConverter implements AttributeConverter<AttachmentCategory, String> {

	/**
	 * Converts an {@link AttachmentCategory} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(AttachmentCategory attribute) {
		return ofNullable(attribute)
			.map(AttachmentCategory::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to an {@link AttachmentCategory} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public AttachmentCategory convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(AttachmentCategory::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, AttachmentCategory.class), e);
		}
	}
}
