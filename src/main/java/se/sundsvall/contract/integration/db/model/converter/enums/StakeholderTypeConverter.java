package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.StakeholderType;

@Converter(autoApply = true)
public class StakeholderTypeConverter implements AttributeConverter<StakeholderType, String> {

	@Override
	public String convertToDatabaseColumn(StakeholderType attribute) {
		return ofNullable(attribute)
			.map(StakeholderType::name)
			.orElse(null);
	}

	@Override
	public StakeholderType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(StakeholderType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + StakeholderType.class, e);
		}
	}
}
