package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import se.sundsvall.contract.model.enums.AttachmentCategory;

import jakarta.persistence.PersistenceException;

class AttachmentCategoryConverterTest {

	private final AttachmentCategoryConverter converter = new AttachmentCategoryConverter();

	@ParameterizedTest
	@EnumSource(AttachmentCategory.class)
	void testConvertToDatabaseColumn(AttachmentCategory enumValue) {
		assertThat(converter.convertToDatabaseColumn(enumValue))
			.isNotNull()
			.isEqualTo(enumValue.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@ParameterizedTest
	@EnumSource(AttachmentCategory.class)
	void testConvertToEntityAttribute(AttachmentCategory enumValue) {
		assertThat(converter.convertToEntityAttribute(enumValue.name())).isEqualTo(enumValue);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.AttachmentCategory")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
