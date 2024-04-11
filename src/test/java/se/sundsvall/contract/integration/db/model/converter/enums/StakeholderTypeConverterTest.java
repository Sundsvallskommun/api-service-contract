package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import se.sundsvall.contract.model.enums.StakeholderType;

import jakarta.persistence.PersistenceException;

class StakeholderTypeConverterTest {

	private final StakeholderTypeConverter converter = new StakeholderTypeConverter();

	@ParameterizedTest
	@EnumSource(StakeholderType.class)
	void testConvertToDatabaseColumn(StakeholderType enumValue) {
		assertThat(converter.convertToDatabaseColumn(enumValue))
			.isNotNull()
			.isEqualTo(enumValue.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@ParameterizedTest
	@EnumSource(StakeholderType.class)
	void testConvertToEntityAttribute(StakeholderType enumValue) {
		assertThat(converter.convertToEntityAttribute(enumValue.name())).isEqualTo(enumValue);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.StakeholderType")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
