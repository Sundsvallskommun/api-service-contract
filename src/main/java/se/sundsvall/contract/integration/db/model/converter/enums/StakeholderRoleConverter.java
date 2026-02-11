package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.StakeholderRole;

/**
 * JPA converter for a {@link List} of {@link StakeholderRole} enums, stored as a comma-delimited string.
 */
@Converter(autoApply = true)
public class StakeholderRoleConverter implements AttributeConverter<List<StakeholderRole>, String> {

	public static final String DELIMITER = ",";

	/**
	 * Converts a {@link List} of {@link StakeholderRole} to a comma-delimited database string representation.
	 *
	 * @param  list the list of enum values to convert
	 * @return      the comma-delimited string representation, or null if the list is null or empty
	 */
	@Override
	public String convertToDatabaseColumn(List<StakeholderRole> list) {
		return ofNullable(list)
			.filter(not(List::isEmpty))
			.map(this::convertListToString)
			.orElse(null);
	}

	/**
	 * Converts a comma-delimited database string to a {@link List} of {@link StakeholderRole} enum values.
	 *
	 * @param  dbData the comma-delimited database string to convert
	 * @return        the corresponding list of enum values, or null if the string is blank
	 */
	@Override
	public List<StakeholderRole> convertToEntityAttribute(String dbData) {
		try {
			return ofNullable(dbData)
				.filter(StringUtils::isNotBlank)
				.map(data -> Arrays.stream(data.split(DELIMITER))
					.filter(StringUtils::isNotBlank)
					.map(StakeholderRole::valueOf)
					.toList())
				.orElse(emptyList());
		} catch (IllegalArgumentException e) {
			throw new PersistenceException("Unable to deserialize %s to %s".formatted(dbData, StakeholderRole.class), e);
		}
	}

	private String convertListToString(List<StakeholderRole> stakeholderRoles) {
		return stakeholderRoles.stream()
			.filter(Objects::nonNull)
			.map(Enum::name)
			.collect(Collectors.joining(DELIMITER));
	}
}
