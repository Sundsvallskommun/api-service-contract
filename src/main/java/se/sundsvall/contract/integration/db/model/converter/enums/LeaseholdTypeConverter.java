package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.LeaseholdType;

@Converter(autoApply = true)
public class LeaseholdTypeConverter implements AttributeConverter<LeaseholdType, String> {

	@Override
	public String convertToDatabaseColumn(LeaseholdType attribute) {
		return ofNullable(attribute)
			.map(LeaseholdType::name)
			.orElse(null);
	}

	@Override
	public LeaseholdType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(LeaseholdType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + LeaseholdType.class, e);
		}
	}
}
