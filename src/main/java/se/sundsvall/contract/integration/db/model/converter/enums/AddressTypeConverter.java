package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import org.apache.commons.lang3.StringUtils;

import se.sundsvall.contract.model.enums.AddressType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

public class AddressTypeConverter implements AttributeConverter<AddressType, String> {

	@Override
	public String convertToDatabaseColumn(AddressType attribute) {
		return ofNullable(attribute)
			.map(AddressType::name)
			.orElse(null);
	}

	@Override
	public AddressType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(AddressType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + AddressType.class, e);
		}
	}
}
