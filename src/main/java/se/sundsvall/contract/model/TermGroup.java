package se.sundsvall.contract.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Schema(description = "Term group")
public class TermGroup {

	@Schema(description = "The term group header", example = "Basic Terms")
	private String header;

	@ArraySchema(schema = @Schema(description = "Terms"))
	private List<Term> terms;
}
