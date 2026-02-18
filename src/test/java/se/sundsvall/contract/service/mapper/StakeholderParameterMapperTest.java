package se.sundsvall.contract.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.api.model.Parameter;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.integration.db.model.StakeholderParameterEntity;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class StakeholderParameterMapperTest {

	@Test
	void toStakeholderParameterEntityList() {
		// Arrange
		final var stakeholderEntity = StakeholderEntity.builder().build();
		final var parameters = List.of(
			Parameter.builder().withDisplayName("displayNameA").withKey("keyA").withValues(List.of("value1")).build(),
			Parameter.builder().withDisplayName("displayNameB").withKey("keyB").withValues(List.of("value2")).build());

		// Act
		final var result = StakeholderParameterMapper.toStakeholderParameterEntityList(parameters, stakeholderEntity);

		// Assert
		assertThat(result)
			.hasSize(2)
			.extracting("displayName", "key", "values", "stakeholderEntity")
			.containsExactlyInAnyOrder(
				tuple("displayNameA", "keyA", List.of("value1"), stakeholderEntity),
				tuple("displayNameB", "keyB", List.of("value2"), stakeholderEntity));
	}

	@Test
	void toStakeholderParameterEntity() {
		// Arrange
		final var parameter = Parameter.builder().withDisplayName("displayNameA").withKey("keyA").withValues(List.of("value1")).build();

		// Act
		final var result = StakeholderParameterMapper.toStakeholderParameterEntity(parameter);

		// Assert
		assertThat(result)
			.extracting("displayName", "key", "values")
			.containsExactly("displayNameA", "keyA", List.of("value1"));
	}

	@Test
	void toParameter() {
		// Arrange
		final var stakeholderParameterEntity = StakeholderParameterEntity.builder().withDisplayName("displayNameA").withKey("keyA").withValues(List.of("value1")).build();

		// Act
		final var result = StakeholderParameterMapper.toParameter(stakeholderParameterEntity);

		// Assert
		assertThat(result)
			.extracting("displayName", "key", "values")
			.containsExactly("displayNameA", "keyA", List.of("value1"));
	}

	@Test
	void toParameterList() {
		// Arrange
		final var stakeholderParameterEntities = List.of(
			StakeholderParameterEntity.builder().withDisplayName("displayNameA").withKey("keyA").withValues(List.of("value1")).build(),
			StakeholderParameterEntity.builder().withDisplayName("displayNameB").withKey("keyB").withValues(List.of("value2")).build());

		// Act
		final var result = StakeholderParameterMapper.toParameterList(stakeholderParameterEntities);

		// Assert
		assertThat(result)
			.hasSize(2)
			.extracting("displayName", "key", "values")
			.containsExactlyInAnyOrder(
				tuple("displayNameA", "keyA", List.of("value1")),
				tuple("displayNameB", "keyB", List.of("value2")));
	}

	@Test
	void toUniqueKeyList() {

		// Arrange
		final var parameterList = List.of(
			Parameter.builder()
				.withDisplayName("displayNameA")
				.withKey("keyA")
				.withValues(List.of("value1", "value2", "value3"))
				.build(),
			Parameter.builder()
				.withDisplayName("displayNameB")
				.withKey("keyB")
				.withValues(List.of("value4", "value5", "value6"))
				.build(),
			Parameter.builder()
				.withDisplayName("displayNameC")
				.withKey("keyA")
				.withValues(List.of("value7", "value8", "value9"))
				.build());

		final var result = StakeholderParameterMapper.toUniqueKeyList(parameterList);

		// Assert
		assertThat(result)
			.hasSize(2)
			.isEqualTo(List.of(
				Parameter.builder()
					.withDisplayName("displayNameA")
					.withKey("keyA")
					.withValues(List.of("value1", "value2", "value3", "value7", "value8", "value9"))
					.build(),
				Parameter.builder()
					.withDisplayName("displayNameB")
					.withKey("keyB")
					.withValues(List.of("value4", "value5", "value6"))
					.build()));
	}

	@Test
	void toUniqueKeyListSingleValuedParametersWithTheSameKey() {

		// Arrange
		final var parameterList = List.of(
			Parameter.builder()
				.withDisplayName("displayNameA")
				.withKey("keyA")
				.withValues(List.of("value1"))
				.build(),
			Parameter.builder()
				.withDisplayName("displayNameB")
				.withKey("keyA")
				.withValues(List.of("value2"))
				.build(),
			Parameter.builder()
				.withDisplayName("displayNameC")
				.withKey("keyA")
				.withValues(List.of("value3"))
				.build());

		final var result = StakeholderParameterMapper.toUniqueKeyList(parameterList);

		// Assert
		assertThat(result)
			.hasSize(1)
			.isEqualTo(List.of(
				Parameter.builder()
					.withDisplayName("displayNameA")
					.withKey("keyA")
					.withValues(List.of("value1", "value2", "value3"))
					.build()));
	}

	@Test
	void toUniqueKeyListWhenNullValuesInOneElement() {

		// Arrange
		final var parameterList = List.of(
			Parameter.builder()
				.withDisplayName("displayNameA")
				.withKey("keyA")
				.withValues(null)
				.build(), // List with null value in one parameter object.
			Parameter.builder()
				.withDisplayName("displayNameB")
				.withKey("keyB")
				.withValues(List.of("value4", "value5", "value6"))
				.build(),
			Parameter.builder()
				.withDisplayName("displayNameC")
				.withKey("keyA")
				.withValues(List.of("value7", "value8", "value9"))
				.build());

		final var result = StakeholderParameterMapper.toUniqueKeyList(parameterList);

		// Assert
		assertThat(result)
			.hasSize(2)
			.isEqualTo(List.of(
				Parameter.builder()
					.withDisplayName("displayNameA")
					.withKey("keyA")
					.withValues(List.of("value7", "value8", "value9"))
					.build(),
				Parameter.builder()
					.withDisplayName("displayNameB")
					.withKey("keyB")
					.withValues(List.of("value4", "value5", "value6"))
					.build()));
	}

	@Test
	void toUniqueKeyListWhenNullValueInSingleElement() {

		// Arrange
		final var parameterList = List.of(
			Parameter.builder()
				.withDisplayName("displayNameA")
				.withKey("keyA")
				.withValues(null)
				.build());

		final var result = StakeholderParameterMapper.toUniqueKeyList(parameterList);

		// Assert
		assertThat(result)
			.hasSize(1)
			.isEqualTo(List.of(
				Parameter.builder()
					.withDisplayName("displayNameA")
					.withKey("keyA")
					.withValues(emptyList())
					.build()));
	}

	@Test
	void toUniqueKeyListWhenInputIsEmpty() {

		// Act
		final var result = StakeholderParameterMapper.toUniqueKeyList(emptyList());

		// Assert
		assertThat(result).isEmpty();
	}
}
