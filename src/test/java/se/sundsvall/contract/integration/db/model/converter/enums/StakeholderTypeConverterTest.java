package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.StakeholderType;

import jakarta.persistence.PersistenceException;

class StakeholderTypeConverterTest {

	private final StakeholderTypeConverter converter = new StakeholderTypeConverter();

	@Test
	void testConvertToDatabaseColumn() {
		assertThat(converter.convertToDatabaseColumn(StakeholderType.ASSOCIATION)).isEqualTo(StakeholderType.ASSOCIATION.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertToEntityAttribute() {
		assertThat(converter.convertToEntityAttribute(StakeholderType.ASSOCIATION.name())).isEqualTo(StakeholderType.ASSOCIATION);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.StakeholderType")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
