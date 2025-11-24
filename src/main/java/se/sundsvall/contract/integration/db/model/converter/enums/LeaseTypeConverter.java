package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.LeaseType;

@Converter(autoApply = true)
public class LeaseTypeConverter implements AttributeConverter<LeaseType, String> {

	@Override
	public String convertToDatabaseColumn(LeaseType attribute) {
		return ofNullable(attribute)
			.map(LeaseType::name)
			.orElse(null);
	}

	@Override
	public LeaseType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(LeaseType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + LeaseType.class, e);
		}
	}
}
