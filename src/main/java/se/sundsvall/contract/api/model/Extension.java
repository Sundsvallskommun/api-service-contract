package se.sundsvall.contract.api.model;

import static java.lang.Boolean.TRUE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
	private Integer leaseExtension;

	@Schema(description = "The unit of the extension value", examples = "MONTHS")
	private TimeUnit unit;

	@AssertTrue(message = "If 'autoExtend' is true, both 'leaseExtension' and 'unit' must be provided!")
	boolean hasValidExtensionProperties() {
		if (TRUE.equals(autoExtend)) {
			return leaseExtension != null && unit != null;
		}

		return true;
	}
}
