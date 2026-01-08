package se.sundsvall.contract.integration.db.model.converter;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.Fees;

@Converter(autoApply = true)
public class FeesConverter implements AttributeConverter<Fees, String> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(final Fees fees) {
		return ofNullable(fees)
			.map(feesObj -> {
				try {
					return OBJECT_MAPPER.writeValueAsString(feesObj);
				} catch (Exception e) {
					throw new PersistenceException("Unable to serialize fees", e);
				}
			})
			.orElse(null);
	}

	@Override
	public Fees convertToEntityAttribute(final String json) {
		return ofNullable(json)
			.filter(StringUtils::isNotBlank)
			.map(jsonString -> {
				try {
					return OBJECT_MAPPER.readValue(jsonString, Fees.class);
				} catch (Exception e) {
					throw new PersistenceException("Unable to deserialize fees", e);
				}
			})
			.orElse(null);
	}
}
