package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import org.apache.commons.lang3.StringUtils;

import se.sundsvall.contract.model.enums.ContractType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;

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
				.filter(not(StringUtils::isBlank))
				.map(ContractType::valueOf)
				.orElse(null);
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + ContractType.class, e);
		}
	}
}
