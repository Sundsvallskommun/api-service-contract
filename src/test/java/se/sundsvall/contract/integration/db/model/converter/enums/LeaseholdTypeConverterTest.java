package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.LeaseholdType;

import jakarta.persistence.PersistenceException;

class LeaseholdTypeConverterTest {

	private final LeaseholdTypeConverter converter = new LeaseholdTypeConverter();

	@Test
	void testConvertToDatabaseColumn() {
		assertThat(converter.convertToDatabaseColumn(LeaseholdType.APARTMENT)).isEqualTo(LeaseholdType.APARTMENT.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertToEntityAttribute() {
		assertThat(converter.convertToEntityAttribute(LeaseholdType.APARTMENT.name())).isEqualTo(LeaseholdType.APARTMENT);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.LeaseholdType")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}