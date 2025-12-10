package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Notice")
public class Notice {

	@Schema(description = "The party type", examples = "LESSOR")
	private Party party;

	@Schema(description = "The period of notice", examples = "3")
	@NotNull
	private Integer periodOfNotice;

	@Schema(description = "The unit of the periodOfNotice value", examples = "MONTHS")
	@NotNull
	private TimeUnit unit;
}
