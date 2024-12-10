package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.UsufructType;

public class UsufructTypeConverter implements AttributeConverter<UsufructType, String> {

	@Override
	public String convertToDatabaseColumn(UsufructType attribute) {
		return ofNullable(attribute)
			.map(UsufructType::name)
			.orElse(null);
	}

	@Override
	public UsufructType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(UsufructType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + UsufructType.class, e);
		}
	}
}
