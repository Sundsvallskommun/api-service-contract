package se.sundsvall.contract.integration.db.model;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_INDEX;

class TermGroupEntityTest {

	@Test
	void builder() {

		// Arrange
		final var id = 1L;
		final var header = "someHeader";
		final var type = TYPE_INDEX;
		final var terms = List.of(TermEmbeddable.builder()
			.withName("someName")
			.withDescription("someDescription")
			.build());

		// Act
		final var result = TermGroupEntity.builder()
			.withId(id)
			.withHeader(header)
			.withType(type)
			.withTerms(terms)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getHeader()).isEqualTo(header);
		assertThat(result.getType()).isEqualTo(type);
		assertThat(result.getTerms()).isEqualTo(terms);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(TermGroupEntity.builder().build()).hasAllNullFieldsOrProperties();

		assertThat(new TermGroupEntity()).hasAllNullFieldsOrProperties();
	}
}
