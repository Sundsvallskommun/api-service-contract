package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.InvoicedIn;

public class InvoicedInConverter implements AttributeConverter<InvoicedIn, String> {

	@Override
	public String convertToDatabaseColumn(InvoicedIn attribute) {
		return ofNullable(attribute)
			.map(InvoicedIn::name)
			.orElse(null);
	}

	@Override
	public InvoicedIn convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(not(StringUtils::isBlank))
				.map(InvoicedIn::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + InvoicedIn.class, e);
		}
	}
}
