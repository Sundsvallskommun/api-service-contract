package se.sundsvall.contract.integration.db.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.geojson.FeatureCollection;
import org.hibernate.Length;

import se.sundsvall.contract.integration.db.model.converter.ExtraParameterGroupConverter;
import se.sundsvall.contract.integration.db.model.converter.FeesConverter;
import se.sundsvall.contract.integration.db.model.converter.TermGroupConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.ContractTypeConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.LandLeaseTypeConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.StatusConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.UsufructTypeConverter;
import se.sundsvall.contract.integration.db.model.generator.GenerateOnInsert;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.UsufructType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contract",
	uniqueConstraints = @UniqueConstraint(columnNames = { "contract_id", "version" }))
public class ContractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@GenerateOnInsert
	@Column(name = "contract_id", length = 10, nullable = false)
	private String contractId;

	@Builder.Default
	@Column(name = "version")
	private int version = 1;

	@Column(name = "type", updatable = false)
	@Convert(converter = ContractTypeConverter.class)
	private ContractType type;

	@Column(name = "status")
	@Convert(converter = StatusConverter.class)
	private Status status;

	@Column(name = "municipality_id", length = 4)
	private String municipalityId;

	@Column(name = "index_terms")
	@Convert(converter = TermGroupConverter.class)
	private List<TermGroup> indexTerms;

	@Column(name = "description", length = 4096)
	private String description;

	@Column(name = "additional_terms")
	@Convert(converter = TermGroupConverter.class)
	private List<TermGroup> additionalTerms;

	@JoinTable(
		name = "contract_stakeholder",
		joinColumns = @JoinColumn(name = "contract_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "stakeholder_id", referencedColumnName = "id")
	)
	@OneToMany(cascade = CascadeType.ALL)
	private List<StakeholderEntity> stakeholders;

	@Column(name = "signed_by_witness")
	private boolean signedByWitness;

	@Column(name = "extra_parameters")
	@Convert(converter = ExtraParameterGroupConverter.class)
	private List<ExtraParameterGroup> extraParameters;

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
		name = "property_designation",
		joinColumns = @JoinColumn(
			name = "contract_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_contract_property_designation_contract_id")),
		indexes = @Index(name = "idx_contract_property_designation_contract_id", columnList = "contract_id"))
	@Column(name = "property_designation")
	private List<String> propertyDesignations;

	@Schema(name = "object_identity")
	private String objectIdentity;

	@Schema(name = "lease_duration")
	private Integer leaseDuration;

	@Column(name = "fees", length = 2048)
	@Convert(converter = FeesConverter.class)
	private Fees fees;

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

	@PrePersist
	@PreUpdate
	public void prePersist() {
		this.version++;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContractEntity that)) return false;
		return version == that.version && signedByWitness == that.signedByWitness && Objects.equals(id, that.id) && Objects.equals(contractId, that.contractId) && type == that.type && status == that.status && Objects.equals(municipalityId, that.municipalityId) && Objects.equals(indexTerms, that.indexTerms) && Objects.equals(description, that.description) && Objects.equals(additionalTerms, that.additionalTerms) && Objects.equals(stakeholders, that.stakeholders) && Objects.equals(extraParameters, that.extraParameters) && landLeaseType == that.landLeaseType && Objects.equals(leasehold, that.leasehold) && usufructType == that.usufructType && Objects.equals(externalReferenceId, that.externalReferenceId) && Objects.equals(propertyDesignations, that.propertyDesignations) && Objects.equals(objectIdentity, that.objectIdentity) && Objects.equals(leaseDuration, that.leaseDuration) && Objects.equals(fees, that.fees) && Objects.equals(invoicing, that.invoicing) && Objects.equals(start, that.start) && Objects.equals(end, that.end) && Objects.equals(autoExtend, that.autoExtend) && Objects.equals(leaseExtension, that.leaseExtension) && Objects.equals(periodOfNotice, that.periodOfNotice) && Objects.equals(area, that.area) && Objects.equals(areaData, that.areaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, contractId, version, type, status, municipalityId, indexTerms, description, additionalTerms, stakeholders, signedByWitness, extraParameters, landLeaseType, leasehold, usufructType, externalReferenceId, propertyDesignations, objectIdentity, leaseDuration, fees, invoicing, start, end, autoExtend, leaseExtension, periodOfNotice, area, areaData);
	}

	@Override
	public String toString() {
		return "ContractEntity{" +
			"id=" + id +
			", contractId='" + contractId + '\'' +
			", version=" + version +
			", type=" + type +
			", status=" + status +
			", municipalityId='" + municipalityId + '\'' +
			", indexTerms=" + indexTerms +
			", description='" + description + '\'' +
			", additionalTerms=" + additionalTerms +
			", stakeholders=" + stakeholders +
			", signedByWitness=" + signedByWitness +
			", extraParameters=" + extraParameters +
			", landLeaseType=" + landLeaseType +
			", leasehold=" + leasehold +
			", usufructType=" + usufructType +
			", externalReferenceId='" + externalReferenceId + '\'' +
			", propertyDesignations=" + propertyDesignations +
			", objectIdentity='" + objectIdentity + '\'' +
			", leaseDuration=" + leaseDuration +
			", fees=" + fees +
			", invoicing=" + invoicing +
			", start=" + start +
			", end=" + end +
			", autoExtend=" + autoExtend +
			", leaseExtension=" + leaseExtension +
			", periodOfNotice=" + periodOfNotice +
			", area=" + area +
			", areaData=" + areaData +
			'}';
	}
}
