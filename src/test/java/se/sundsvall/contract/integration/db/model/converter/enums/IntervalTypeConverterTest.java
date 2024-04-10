package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.IntervalType;

import jakarta.persistence.PersistenceException;

class IntervalTypeConverterTest {

	private final IntervalTypeConverter converter = new IntervalTypeConverter();

	@Test
	void testConvertToDatabaseColumn() {
		assertThat(converter.convertToDatabaseColumn(IntervalType.MONTHLY)).isEqualTo(IntervalType.MONTHLY.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertToEntityAttribute() {
		assertThat(converter.convertToEntityAttribute(IntervalType.MONTHLY.name())).isEqualTo(IntervalType.MONTHLY);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.IntervalType")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
