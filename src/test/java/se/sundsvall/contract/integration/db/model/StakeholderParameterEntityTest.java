package se.sundsvall.contract.integration.db.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class StakeholderParameterEntityTest {

	@Test
	void builder() {

		// Arrange
		final var id = 1L;
		final var key = "someKey";
		final var displayName = "someDisplayName";
		final var values = List.of("value1", "value2");
		final var stakeholderEntity = StakeholderEntity.builder().build();

		// Act
		final var result = StakeholderParameterEntity.builder()
			.withId(id)
			.withKey(key)
			.withDisplayName(displayName)
			.withValues(values)
			.withStakeholderEntity(stakeholderEntity)
			.build();

		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getKey()).isEqualTo(key);
		assertThat(result.getDisplayName()).isEqualTo(displayName);
		assertThat(result.getValues()).isEqualTo(values);
		assertThat(result.getStakeholderEntity()).isEqualTo(stakeholderEntity);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(StakeholderParameterEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new StakeholderParameterEntity()).hasAllNullFieldsOrProperties();
	}
}
