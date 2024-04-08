package se.sundsvall.contract.integration.db.model.converter;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.contract.model.ExtraParameterGroup;

import jakarta.persistence.PersistenceException;

@ExtendWith(MockitoExtension.class)
class ExtraParameterGroupConverterExceptionTest {

	@Mock
	private ObjectMapper mockObjectMapper;

	@InjectMocks
	private ExtraParameterGroupConverter converter;

	@Test
	void testConvertToDatabaseColumn_shouldThrowPersistenceException_whenSerializationFails() throws JsonProcessingException {
		//Arrange
		when(mockObjectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("error"));

		//Act & Assert
		assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> converter.convertToDatabaseColumn(List.of(new ExtraParameterGroup())))
			.withMessage("Unable to serialize extra parameter groups");

		verifyNoMoreInteractions(mockObjectMapper);
	}

	@Test
	void testConvertToEntityAttribute_shouldThrowPersistenceException_whenSerializationFails() throws JsonProcessingException {
		//Arrange
		var json = "[{\"name\":\"name\",\"parameters\":{\"someKey\":\"someValue\"}}]";
		when(mockObjectMapper.readValue(anyString(), any(JavaType.class))).thenThrow(new RuntimeException("error"));

		//Act & Assert
		assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> converter.convertToEntityAttribute(json))
			.withMessage("Unable to deserialize extra parameter groups");
	}
}
