package se.sundsvall.contract.integration.db.model.converter.enums;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import se.sundsvall.contract.model.enums.IntervalType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class IntervalTypeConverterTest {

	private final IntervalTypeConverter converter = new IntervalTypeConverter();

	@ParameterizedTest
	@EnumSource(IntervalType.class)
	void testConvertToDatabaseColumn(IntervalType enumValue) {
		assertThat(converter.convertToDatabaseColumn(enumValue))
			.isNotNull()
			.isEqualTo(enumValue.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@ParameterizedTest
	@EnumSource(IntervalType.class)
	void testConvertToEntityAttribute(IntervalType enumValue) {
		assertThat(converter.convertToEntityAttribute(enumValue.name())).isEqualTo(enumValue);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.IntervalType")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
