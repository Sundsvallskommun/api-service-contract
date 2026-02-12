package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.ContractType;

/**
 * JPA converter for {@link ContractType}.
 */
@Converter(autoApply = true)
public class ContractTypeConverter implements AttributeConverter<ContractType, String> {

	/**
	 * Converts a {@link ContractType} to its database string representation.
	 *
	 * @param  attribute the enum value to convert
	 * @return           the string representation, or null if the attribute is null
	 */
	@Override
	public String convertToDatabaseColumn(ContractType attribute) {
		return ofNullable(attribute)
			.map(ContractType::name)
			.orElse(null);
	}

	/**
	 * Converts a database string to a {@link ContractType} enum value.
	 *
	 * @param  dbData the database string to convert
	 * @return        the corresponding enum value, or null if the string is blank
	 */
	@Override
	public ContractType convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(ContractType::valueOf)
				.orElse(null);
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, ContractType.class), e);
		}
	}
}
