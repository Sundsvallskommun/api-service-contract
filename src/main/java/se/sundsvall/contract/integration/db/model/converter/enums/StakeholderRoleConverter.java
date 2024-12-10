package se.sundsvall.contract.integration.db.model.converter.enums;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import se.sundsvall.contract.model.enums.StakeholderRole;

public class StakeholderRoleConverter implements AttributeConverter<List<StakeholderRole>, String> {

	public static final String DELIMITER = ",";

	@Override
	public String convertToDatabaseColumn(List<StakeholderRole> list) {
		return ofNullable(list)
			.filter(not(List::isEmpty))
			.map(this::convertListToString)
			.orElse(null);
	}

	@Override
	public List<StakeholderRole> convertToEntityAttribute(String dbData) {
		try {
			return Arrays.stream(dbData.split(DELIMITER))
				.filter(not(StringUtils::isBlank))
				.map(StakeholderRole::valueOf)
				.toList();
		} catch (Exception e) {
			throw new PersistenceException("Unable to deserialize " + dbData + " to " + StakeholderRole.class, e);
		}
	}

	private String convertListToString(List<StakeholderRole> stakeholderRoles) {
		return stakeholderRoles.stream()
			.filter(Objects::nonNull)
			.map(Enum::name)
			.collect(Collectors.joining(DELIMITER));
	}
}
