package se.sundsvall.contract.integration.db.model;

import se.sundsvall.contract.integration.db.model.converter.enums.IntervalTypeConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.InvoicedInConverter;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
@Embeddable
public class InvoicingEntity {

	@Convert(converter = IntervalTypeConverter.class)
	private IntervalType invoiceInterval;

	@Convert(converter = InvoicedInConverter.class)
	private InvoicedIn invoicedIn;
}
