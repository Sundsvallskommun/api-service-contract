package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.TimeUnit;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Notice term")
public class NoticeTerm {

	@Schema(description = "The party type", examples = "LESSOR")
	@NotNull
	private Party party;

	@Schema(description = "The period of notice", examples = "3")
	@NotNull
	@Min(0)
	private Integer periodOfNotice;

	@Schema(description = "The unit of the periodOfNotice value", examples = "MONTHS")
	@NotNull
	private TimeUnit unit;
}
