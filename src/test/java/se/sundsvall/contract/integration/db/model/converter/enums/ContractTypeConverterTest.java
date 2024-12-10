package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import se.sundsvall.contract.model.enums.ContractType;

class ContractTypeConverterTest {

	private final ContractTypeConverter converter = new ContractTypeConverter();

	@ParameterizedTest
	@EnumSource(ContractType.class)
	void testConvertToDatabaseColumn(ContractType enumValue) {
		assertThat(converter.convertToDatabaseColumn(enumValue))
			.isNotNull()
			.isEqualTo(enumValue.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@ParameterizedTest
	@EnumSource(ContractType.class)
	void testConvertToEntityAttribute(ContractType enumValue) {
		assertThat(converter.convertToEntityAttribute(enumValue.name())).isEqualTo(enumValue);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.ContractType")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
