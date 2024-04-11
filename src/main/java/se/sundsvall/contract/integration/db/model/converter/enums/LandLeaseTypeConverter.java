package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import org.apache.commons.lang3.StringUtils;

import se.sundsvall.contract.model.enums.LandLeaseType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

public class LandLeaseTypeConverter implements AttributeConverter<LandLeaseType, String> {

	@Override
	public String convertToDatabaseColumn(LandLeaseType attribute) {
		return ofNullable(attribute)
			.map(LandLeaseType::name)
			.orElse(null);
	}

	@Override
	public LandLeaseType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(LandLeaseType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + LandLeaseType.class, e);
		}
	}
}
