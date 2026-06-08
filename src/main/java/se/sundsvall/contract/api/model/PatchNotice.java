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
import org.openapitools.jackson.nullable.JsonNullable;
import se.sundsvall.contract.model.enums.Party;

/**
 * Partial notice details for PATCH (JSON Merge Patch semantics): omitted leaves the sub-field unchanged, null clears
 * it,
 * a value updates it. The terms array is replaced as a whole when provided.
 */
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial notice details for PATCH")
public class PatchNotice {

	@ArraySchema(schema = @Schema(description = "Notice terms"))
	@Builder.Default
	private JsonNullable<List<@Valid NoticeTerm>> terms = JsonNullable.undefined();

	@Schema(description = "Date when the notice was given", examples = "2023-01-01", format = "date")
	@Builder.Default
	private JsonNullable<LocalDate> noticeDate = JsonNullable.undefined();

	@Schema(description = "The party that gave the notice")
	@Builder.Default
	private JsonNullable<Party> noticeGivenBy = JsonNullable.undefined();
}
