package se.sundsvall.contract.integration.db.model;


import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import org.geojson.FeatureCollection;
import org.hibernate.Length;

import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.UsufructType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder(setterPrefix = "with")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

	@Enumerated(EnumType.STRING)
	private IntervalType invoiceInterval;

	private LocalDate start;

	private LocalDate end;

	private Boolean autoExtend;

	private Integer leaseExtension;

	private Integer periodOfNotice;

	private Integer area;

	@Column(name = "area_data", length = Length.LONG32)
	private FeatureCollection areaData;

}
