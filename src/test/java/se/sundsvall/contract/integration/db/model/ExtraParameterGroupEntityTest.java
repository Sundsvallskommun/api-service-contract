package se.sundsvall.contract.integration.db.model;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExtraParameterGroupEntityTest {

	@Test
	void builder() {

		// Arrange
		final var id = 1L;
		final var name = "someName";
		final var parameters = Map.of("key1", "value1", "key2", "value2");

		// Act
		final var result = ExtraParameterGroupEntity.builder()
			.withId(id)
			.withName(name)
			.withParameters(parameters)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getParameters()).isEqualTo(parameters);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ExtraParameterGroupEntity.builder().build()).hasAllNullFieldsOrProperties();

		assertThat(new ExtraParameterGroupEntity()).hasAllNullFieldsOrProperties();
	}
}
