package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.ContractType;

@Converter(autoApply = true)
public class ContractTypeConverter implements AttributeConverter<ContractType, String> {

	@Override
	public String convertToDatabaseColumn(ContractType attribute) {
		return ofNullable(attribute)
			.map(ContractType::name)
			.orElse(null);
	}

	@Override
	public ContractType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(ContractType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + ContractType.class, e);
		}
	}
}
