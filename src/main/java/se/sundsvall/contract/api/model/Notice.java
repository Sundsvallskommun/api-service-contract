package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.Party;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Notice information")
public class Notice {

	@ArraySchema(schema = @Schema(description = "Notice terms per party"))
	private List<@Valid NoticeTerm> terms;

	@Schema(description = "Date when notice was given", examples = "2025-01-29")
	private LocalDate noticeDate;

	@Schema(description = "Party that initiated the notice", examples = "LESSOR")
	private Party noticeGivenBy;
}
