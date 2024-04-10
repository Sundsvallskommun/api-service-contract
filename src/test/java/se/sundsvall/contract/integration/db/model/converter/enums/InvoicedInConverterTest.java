package se.sundsvall.contract.integration.db.model.converter.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.enums.InvoicedIn;

import jakarta.persistence.PersistenceException;

class InvoicedInConverterTest {

	private final InvoicedInConverter converter = new InvoicedInConverter();

	@Test
	void testConvertToDatabaseColumn() {
		assertThat(converter.convertToDatabaseColumn(InvoicedIn.ARREARS)).isEqualTo(InvoicedIn.ARREARS.name());
	}

	@Test
	void testConvertNullToDatabaseColumn_shouldReturnNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertToEntityAttribute() {
		assertThat(converter.convertToEntityAttribute(InvoicedIn.ARREARS.name())).isEqualTo(InvoicedIn.ARREARS);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowException_whenUnableToDeserialize() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("INVALID"))
			.withMessage("Unable to deserialize INVALID to class se.sundsvall.contract.model.enums.InvoicedIn")
			.satisfies(problem -> assertThat(problem.getCause()).isInstanceOf(IllegalArgumentException.class));
	}
}
