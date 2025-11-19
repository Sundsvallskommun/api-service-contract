package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.AddressType;

@Converter(autoApply = true)
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
				.filter(StringUtils::isNotBlank)
				.map(AddressType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + AddressType.class, e);
		}
	}
}
