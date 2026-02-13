package se.sundsvall.contract.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.contract.api.model.Parameter;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.integration.db.model.StakeholderParameterEntity;

/**
 * Mapper for converting between {@link Parameter} and {@link StakeholderParameterEntity} objects.
 */
public final class StakeholderParameterMapper {

	private StakeholderParameterMapper() {}

	/**
	 * Converts a list of {@link Parameter} to a list of {@link StakeholderParameterEntity}, merging duplicate keys.
	 *
	 * @param  parameters the list of parameters to convert
	 * @param  entity     the stakeholder entity to associate with each parameter entity
	 * @return            the converted list of parameter entities
	 */
	public static List<StakeholderParameterEntity> toStakeholderParameterEntityList(final List<Parameter> parameters, final StakeholderEntity entity) {
		return toUniqueKeyList(parameters).stream()
			.map(parameter -> {
				final var result = toStakeholderParameterEntity(parameter);
				result.setStakeholderEntity(entity);
				return result;
			})
			.collect(toCollection(ArrayList::new));
	}

	/**
	 * Converts a {@link Parameter} to a {@link StakeholderParameterEntity}.
	 *
	 * @param  parameter the parameter to convert
	 * @return           the converted entity
	 */
	public static StakeholderParameterEntity toStakeholderParameterEntity(final Parameter parameter) {
		return StakeholderParameterEntity.builder()
			.withDisplayName(parameter.getDisplayName())
			.withKey(parameter.getKey())
			.withValues(parameter.getValues())
			.build();
	}

	/**
	 * Converts a {@link StakeholderParameterEntity} to a {@link Parameter}.
	 *
	 * @param  parameter the entity to convert
	 * @return           the converted parameter
	 */
	public static Parameter toParameter(final StakeholderParameterEntity parameter) {
		return Parameter.builder()
			.withDisplayName(parameter.getDisplayName())
			.withKey(parameter.getKey())
			.withValues(parameter.getValues())
			.build();
	}

	/**
	 * Converts a list of {@link StakeholderParameterEntity} to a list of {@link Parameter}.
	 *
	 * @param  parameters the list to convert
	 * @return            the converted list, or an empty list if input is null
	 */
	public static List<Parameter> toParameterList(final List<StakeholderParameterEntity> parameters) {
		return Optional.ofNullable(parameters).orElse(emptyList()).stream()
			.map(StakeholderParameterMapper::toParameter)
			.collect(toCollection(ArrayList::new));
	}

	/**
	 * Merges parameters with duplicate keys into single entries, combining their values.
	 *
	 * @param  parameterList the list of parameters to merge
	 * @return               the deduplicated list, or an empty list if input is null
	 */
	public static List<Parameter> toUniqueKeyList(final List<Parameter> parameterList) {
		return Optional.ofNullable(parameterList).orElse(emptyList()).stream()
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
			.collect(toCollection(ArrayList::new));
	}
}
