package se.sundsvall.contract.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.contract.api.model.Parameter;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.integration.db.model.StakeholderParameterEntity;

public final class StakeholderParameterMapper {

	private StakeholderParameterMapper() {
		// Intentionally empty
	}

	public static List<StakeholderParameterEntity> toStakeholderParameterEntityList(final List<Parameter> parameters, final StakeholderEntity entity) {
		return new ArrayList<>(toUniqueKeyList(parameters).stream()
			.map(parameter -> {
				final var result = toStakeholderParameterEntity(parameter);
				result.setStakeholderEntity(entity);
				return result;
			})
			.toList());
	}

	public static StakeholderParameterEntity toStakeholderParameterEntity(final Parameter parameter) {
		return StakeholderParameterEntity.builder()
			.withDisplayName(parameter.getDisplayName())
			.withKey(parameter.getKey())
			.withValues(parameter.getValues())
			.build();
	}

	public static Parameter toParameter(final StakeholderParameterEntity parameter) {
		return Parameter.builder()
			.withDisplayName(parameter.getDisplayName())
			.withKey(parameter.getKey())
			.withValues(parameter.getValues())
			.build();
	}

	public static List<Parameter> toParameterList(final List<StakeholderParameterEntity> parameters) {
		return Optional.ofNullable(parameters).orElse(emptyList()).stream()
			.map(StakeholderParameterMapper::toParameter)
			.toList();
	}

	public static List<Parameter> toUniqueKeyList(final List<Parameter> parameterList) {
		return new ArrayList<>(Optional.ofNullable(parameterList).orElse(emptyList()).stream()
			.collect(groupingBy(Parameter::getKey))
			.entrySet()
			.stream()
			.map(entry -> Parameter.builder()
				.withDisplayName(entry.getValue().getFirst().getDisplayName())
				.withKey(entry.getKey())
				.withValues(new ArrayList<>(entry.getValue().stream()
					.map(Parameter::getValues)
					.filter(Objects::nonNull)
					.flatMap(List::stream)
					.toList()))
				.build())
			.toList());
	}
}
