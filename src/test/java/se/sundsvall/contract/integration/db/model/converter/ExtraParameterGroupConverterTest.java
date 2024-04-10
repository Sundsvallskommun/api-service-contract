package se.sundsvall.contract.integration.db.model.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.contract.model.ExtraParameterGroup;

@ExtendWith(MockitoExtension.class)
class ExtraParameterGroupConverterTest {

	private ExtraParameterGroupConverter converter;

	@BeforeEach
	public void setup() {
		this.converter = new ExtraParameterGroupConverter(new ObjectMapper());
	}

	private static final List<ExtraParameterGroup> validExtraParameters = List.of(ExtraParameterGroup.builder()
		.withName("name")
		.withParameters(Map.of("someKey", "someValue"))
		.build());

	@Test
	void testConvertToDatabaseColumn() {
		var converted = converter.convertToDatabaseColumn(validExtraParameters);
		var wanted = "[{\"name\":\"name\",\"parameters\":{\"someKey\":\"someValue\"}}]";

		assertThat(converted).isEqualTo(wanted);
	}

	@Test
	void testConvertToDatabaseColumn_shouldReturnNull_whenExtraParameterGroupsIsNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void testConvertToDatabaseColumn_shouldReturnNull_whenExtraParameterGroupsIsEmpty() {
		assertThat(converter.convertToDatabaseColumn(List.of())).isNull();
	}

	@Test
	void testConvertToEntityAttribute() {
		var converted = converter.convertToEntityAttribute("[{\"name\":\"name\",\"parameters\":{\"someKey\":\"someValue\"}}]");

		assertThat(converted).isNotNull().hasSize(1);
		assertThat(converted.getFirst().getName()).isEqualTo("name");
		assertThat(converted.getFirst().getParameters()).containsEntry("someKey", "someValue");
	}

	@Test
	void testConvertToEntityAttribute_shouldReturnNull_whenJsonIsNull() {
		assertThat(converter.convertToEntityAttribute(null)).isNull();
	}

	@Test
	void testConvertToEntityAttribute_shouldReturnNull_whenJsonIsBlank() {
		assertThat(converter.convertToEntityAttribute(" ")).isNull();
	}
}
