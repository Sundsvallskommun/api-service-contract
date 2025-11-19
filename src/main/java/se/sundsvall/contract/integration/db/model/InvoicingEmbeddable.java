package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.integration.db.model.converter.enums.IntervalTypeConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.InvoicedInConverter;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

@Embeddable
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class InvoicingEmbeddable {

	@Convert(converter = IntervalTypeConverter.class)
	private IntervalType invoiceInterval;

	@Convert(converter = InvoicedInConverter.class)
	private InvoicedIn invoicedIn;
}
