package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.AttachmentCategory;

@Converter(autoApply = true)
public class AttachmentCategoryConverter implements AttributeConverter<AttachmentCategory, String> {

	@Override
	public String convertToDatabaseColumn(AttachmentCategory attribute) {
		return ofNullable(attribute)
			.map(AttachmentCategory::name)
			.orElse(null);
	}

	@Override
	public AttachmentCategory convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(AttachmentCategory::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + AttachmentCategory.class, e);
		}
	}
}
