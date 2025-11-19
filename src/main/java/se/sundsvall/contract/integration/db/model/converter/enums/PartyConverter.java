package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.Party;

public class PartyConverter implements AttributeConverter<Party, String> {

	@Override
	public String convertToDatabaseColumn(Party attribute) {
		return ofNullable(attribute)
			.map(Party::name)
			.orElse(null);
	}

	@Override
	public Party convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(Party::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + Party.class, e);
		}
	}
}
