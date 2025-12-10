package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.TimeUnit;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Extension")
public class Extension {

	@Schema(description = "Marker for whether an agreement should be extended automatically or not", examples = "true", defaultValue = "true")
	private Boolean autoExtend;

	@Schema(description = "The lease extension value", examples = "2")
	@NotNull
	private Integer leaseExtension;

	@Schema(description = "The unit of the extension value", examples = "MONTHS")
	@NotNull
	private TimeUnit unit;
}
