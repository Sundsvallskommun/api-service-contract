package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;

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

    @Enumerated(EnumType.STRING)
    private IntervalType invoiceInterval;

    @Enumerated(EnumType.STRING)
    private InvoicedIn invoicedIn;
}
