package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.sundsvall.contract.integration.db.model.converter.enums.StakeholderRoleConverter.DELIMITER;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.StakeholderRole;

import jakarta.persistence.PersistenceException;

class StakeholderRoleConverterTest {

	private final StakeholderRoleConverter converter = new StakeholderRoleConverter();
	
	private final List<StakeholderRole> stakeholderRoles = List.of(StakeholderRole.BUYER, StakeholderRole.GRANTOR, StakeholderRole.POWER_OF_ATTORNEY_ROLE);

	@Test
	void testConvertToDatabaseColumn() {
		var wanted = stakeholderRoles.stream()
				.map(Enum::name)
				.collect(Collectors.joining(DELIMITER));

		assertThat(converter.convertToDatabaseColumn(stakeholderRoles)).isEqualTo(wanted);
	}

	@Test
	void testConvertToEntityAttribute() {
		var wanted = stakeholderRoles.stream()
				.map(Enum::name)
				.collect(Collectors.joining(DELIMITER));

		assertThat(converter.convertToEntityAttribute(wanted)).isEqualTo(stakeholderRoles);
	}

	@Test
	void testConvertSingleAttributeToDatabaseColumn() {
		assertThat(converter.convertToDatabaseColumn(List.of(StakeholderRole.BUYER))).isEqualTo(StakeholderRole.BUYER.name());
	}

	@Test
	void testConvertSingleDatabaseAttributeToEntityAttribute() {
		assertThat(converter.convertToEntityAttribute(StakeholderRole.BUYER.name())).isEqualTo(List.of(StakeholderRole.BUYER));
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.StakeholderRole")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}

	@Test
	void testConvertToDatabaseColumn_shouldReturnNull_whenListIsNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertToDatabaseColumn_shouldReturnNull_whenListIsEmpty() {
		assertThat(converter.convertToDatabaseColumn(List.of())).isNull();
	}

	@Test
	void testDelimiter() {
		//Don't change delimiter...
		assertThat(DELIMITER).isEqualTo(",");
	}
}