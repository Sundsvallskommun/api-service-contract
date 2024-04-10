package se.sundsvall.contract.integration.db.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.geojson.FeatureCollection;
import org.hibernate.Length;

import se.sundsvall.contract.integration.db.model.converter.LeaseFeesConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.LandLeaseTypeConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.UsufructTypeConverter;
import se.sundsvall.contract.model.LeaseFees;
import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.contract.model.enums.UsufructType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
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

	@Column(name = "land_lease_type")
	@Convert(converter = LandLeaseTypeConverter.class)
	private LandLeaseType landLeaseType;

	@Embedded
	private LeaseholdEntity leasehold;

	@Column(name = "usufruct_type")
	@Convert(converter = UsufructTypeConverter.class)
	private UsufructType usufructType;

	@Column(name = "external_reference_id")
	private String externalReferenceId;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "land_lease_contract_property_designation",
		joinColumns = @JoinColumn(
			name = "land_lease_contract_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_land_lease_contract_property_designation_contract_id")),
			indexes = @Index(name = "idx_land_lease_contract_property_designation_contract_id", columnList = "land_lease_contract_id"))
	@Column(name = "property_designation")
	private List<String> propertyDesignations;

	@Schema(name = "object_identity")
	private String objectIdentity;

	@Schema(name = "lease_duration")
	private Integer leaseDuration;

	@Column(name = "lease_fees", length = 2048)
	@Convert(converter = LeaseFeesConverter.class)
	private LeaseFees leaseFees;

	@Embedded
	private InvoicingEntity invoicing;

	@Schema(name = "start")
	private LocalDate start;

	@Schema(name = "end")
	private LocalDate end;

	@Column(name = "auto_extend")
	private Boolean autoExtend;

	@Column(name = "lease_extension")
	private Integer leaseExtension;

	@Column(name = "period_of_notice")
	private Integer periodOfNotice;

	@Column(name = "area")
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
		return super.equals(o) &&
			landLeaseType == that.landLeaseType &&
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
