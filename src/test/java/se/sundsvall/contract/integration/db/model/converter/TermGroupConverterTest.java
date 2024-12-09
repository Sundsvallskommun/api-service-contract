package se.sundsvall.contract.integration.db.model.converter;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.persistence.PersistenceException;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;

class TermGroupConverterTest {

	public static final String JSON = """
		[
			{
				"header": "Some header",
				"terms": [
					{
						"term": "Payment",
						"description": "Bla bla bla"
					}
				]
			}
		]
		""";

	private final TermGroupConverter converter = new TermGroupConverter();

	@Test
	void convertToDatabaseColumnWithNullInputReturnsNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void convertToDatabaseColumn() {
		var termGroup = TermGroup.builder()
			.withHeader("Some header")
			.withTerms(List.of(
				Term.builder()
					.withName("Payment")
					.withDescription("Bla bla bla")
					.build()))
			.build();

		var result = converter.convertToDatabaseColumn(List.of(termGroup));

		assertThatJson(result).isEqualTo(JSON);
	}

	@Test
	void convertToEntityAttributeWithNullInputReturnsNull() {
		assertThat(converter.convertToEntityAttribute(null)).isNull();
	}

	@Test
	void convertToEntityAttributeWithBlankInputReturnsNull() {
		assertThat(converter.convertToEntityAttribute("")).isNull();
	}

	@Test
	void convertToEntityAttributeWithInvalidInputThrowsException() {
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> converter.convertToEntityAttribute("not-json"))
			.withMessage("Unable to deserialize term groups");
	}

	@Test
	void convertToEntityAttribute() {
		var result = converter.convertToEntityAttribute(JSON);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).satisfies(termGroup -> {
			assertThat(termGroup.getHeader()).isEqualTo("Some header");
			assertThat(termGroup.getTerms()).hasSize(1);
			assertThat(termGroup.getTerms().getFirst()).satisfies(term -> {
				assertThat(term.getName()).isEqualTo("Payment");
				assertThat(term.getDescription()).isEqualTo("Bla bla bla");
			});
		});
	}
}
