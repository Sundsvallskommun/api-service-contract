package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.AttachmentCategory;

import jakarta.persistence.PersistenceException;

class AttachmentCategoryConverterTest {

	private final AttachmentCategoryConverter converter = new AttachmentCategoryConverter();

	@Test
	void testConvertToDatabaseColumn() {
		assertThat(converter.convertToDatabaseColumn(AttachmentCategory.CONTRACT)).isEqualTo(AttachmentCategory.CONTRACT.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertFromDatabaseColumn() {
		assertThat(converter.convertToEntityAttribute(AttachmentCategory.CONTRACT.name())).isEqualTo(AttachmentCategory.CONTRACT);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.AttachmentCategory")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
