package se.sundsvall.contract.integration.db.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.geojson.FeatureCollection;
import org.hibernate.Length;

import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.contract.model.enums.UsufructType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import se.sundsvall.contract.integration.db.model.converter.LeaseFeesConverter;
import se.sundsvall.contract.model.LeaseFees;

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
	private LeaseholdEntity leasehold;

	@Enumerated(EnumType.STRING)
	private UsufructType usufructType;

	private String externalReferenceId;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "land_lease_contract_property_designations",
		joinColumns = @JoinColumn(
			name = "contract_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_land_lease_contract_property_designations_contract_id")),
	indexes = @Index(name = "idx_land_lease_contract_property_designations_contract_id", columnList = "contract_id"))
	private List<String> propertyDesignations;

	private String objectIdentity;

	private Integer leaseDuration;

	@Column(name = "lease_fees")
	@Convert(converter = LeaseFeesConverter.class)
	private LeaseFees leaseFees;

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
			Objects.equals(leasehold, that.leasehold) &&
			usufructType == that.usufructType &&
			isSignedByWitness() == that.isSignedByWitness() &&
			Objects.equals(externalReferenceId, that.externalReferenceId) &&
			Objects.equals(propertyDesignations, that.propertyDesignations) &&
			Objects.equals(objectIdentity, that.objectIdentity) &&
			Objects.equals(leaseDuration, that.leaseDuration) &&
			Objects.equals(leaseFees, that.leaseFees) &&
			Objects.equals(invoicing, that.invoicing) &&
			Objects.equals(start, that.start) && Objects.equals(end, that.end) &&
			Objects.equals(autoExtend, that.autoExtend) &&
			Objects.equals(leaseExtension, that.leaseExtension) &&
			Objects.equals(periodOfNotice, that.periodOfNotice) &&
			Objects.equals(area, that.area);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getMunicipalityId(), landLeaseType, leasehold, usufructType, isSignedByWitness(), externalReferenceId, propertyDesignations, objectIdentity, leaseDuration, leaseFees, invoicing, start, end, autoExtend, leaseExtension, periodOfNotice, area);
	}

	@Override
	public String toString() {
		return "LandLeaseContractEntity{" +
			"landLeaseType=" + landLeaseType +
			", leaseholdType=" + leasehold +
			", usufructType=" + usufructType +
			", municipalityId=" + getMunicipalityId() +
			", signedByWitness=" + isSignedByWitness() +
			", externalReferenceId='" + externalReferenceId + '\'' +
			", propertyDesignations='" + propertyDesignations + '\'' +
			", objectIdentity='" + objectIdentity + '\'' +
			", leaseDuration=" + leaseDuration +
			", leaseFees=" + leaseFees +
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
