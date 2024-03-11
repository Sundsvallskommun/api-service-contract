package se.sundsvall.contract.integration.db.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import org.geojson.FeatureCollection;
import org.hibernate.Length;

import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.UsufructType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "land_lease_contract")
public class LandLeaseContractEntity extends ContractEntity {

	@Enumerated(EnumType.STRING)
	private LandLeaseType landLeaseType;

	@Embedded
	private LeaseholdEntity leaseholdType;

	@Enumerated(EnumType.STRING)
	private UsufructType usufructType;

	private String externalReferenceId;

	private String propertyDesignation;

	private String objectIdentity;

	private Integer leaseDuration;

	private BigDecimal rental;

	@Embedded
	private InvoicingEntity invoicing;

	private LocalDate start;

	private LocalDate end;

	private Boolean autoExtend;

	private Integer leaseExtension;

	private Integer periodOfNotice;

	private Integer area;

	@Column(name = "area_data", length = Length.LONG32)
	private FeatureCollection areaData;

	//Excluded areaData and attachments from equals, hashCode and toString
	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if (!(o instanceof LandLeaseContractEntity that)) {
            return false;
        }
		if (!super.equals(o)) {
            return false;
        }
		return landLeaseType == that.landLeaseType &&
			Objects.equals(getMunicipalityId(), that.getMunicipalityId()) &&
			Objects.equals(leaseholdType, that.leaseholdType) &&
			usufructType == that.usufructType &&
			isSignedByWitness() == that.isSignedByWitness() &&
			Objects.equals(externalReferenceId, that.externalReferenceId) &&
			Objects.equals(propertyDesignation, that.propertyDesignation) &&
			Objects.equals(objectIdentity, that.objectIdentity) &&
			Objects.equals(leaseDuration, that.leaseDuration) &&
			Objects.equals(rental, that.rental) &&
			Objects.equals(invoicing, that.invoicing) &&
			Objects.equals(start, that.start) && Objects.equals(end, that.end) &&
			Objects.equals(autoExtend, that.autoExtend) &&
			Objects.equals(leaseExtension, that.leaseExtension) &&
			Objects.equals(periodOfNotice, that.periodOfNotice) &&
			Objects.equals(area, that.area);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getMunicipalityId(), landLeaseType, leaseholdType, usufructType, isSignedByWitness(), externalReferenceId, propertyDesignation, objectIdentity, leaseDuration, rental, invoicing, start, end, autoExtend, leaseExtension, periodOfNotice, area);
	}

	@Override
	public String toString() {
		return "LandLeaseContractEntity{" +
			"landLeaseType=" + landLeaseType +
			", leaseholdType=" + leaseholdType +
			", usufructType=" + usufructType +
			", municipalityId=" + getMunicipalityId() +
			", signedByWitness=" + isSignedByWitness() +
			", externalReferenceId='" + externalReferenceId + '\'' +
			", propertyDesignation='" + propertyDesignation + '\'' +
			", objectIdentity='" + objectIdentity + '\'' +
			", leaseDuration=" + leaseDuration +
			", rental=" + rental +
			", invoicing=" + invoicing +
			", start=" + start +
			", end=" + end +
			", autoExtend=" + autoExtend +
			", leaseExtension=" + leaseExtension +
			", periodOfNotice=" + periodOfNotice +
			", area=" + area +
			", areaData=" + areaData +
			"} " + super.toString();
	}
}
