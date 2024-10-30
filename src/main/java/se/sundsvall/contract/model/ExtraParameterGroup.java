package se.sundsvall.contract.model;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Extra parameter group")
public class ExtraParameterGroup {

	@Schema(description = "The group name", example = "Fees")
	private String name;

	@Schema(description = "Parameters", example = "{\"key1\": \"value1\",\"key2\": \"value2\"}")
	private Map<String, String> parameters;
}
