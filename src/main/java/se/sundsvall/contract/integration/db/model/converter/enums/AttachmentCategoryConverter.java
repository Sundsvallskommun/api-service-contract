package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.AttachmentCategory;

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
				.filter(not(StringUtils::isBlank))
				.map(AttachmentCategory::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + AttachmentCategory.class, e);
		}
	}
}
