package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geojson.FeatureCollection;
import org.hibernate.Length;
import se.sundsvall.contract.integration.db.model.generator.GenerateOnInsert;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;

@Entity
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "contract",
	uniqueConstraints = @UniqueConstraint(name = "uq_contract_contract_id_version", columnNames = {
		"contract_id", "version"
	}),
	indexes = @Index(name = "idx_contract_municipality_id_contract_id", columnList = "municipality_id, contract_id"))
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

	@Column(name = "type", length = 64, updatable = false)
	private ContractType type;

	@Column(name = "status", length = 64)
	private Status status;

	@Column(name = "municipality_id", length = 4)
	private String municipalityId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "contract_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = "fk_term_group_contract_id"))
	private List<TermGroupEntity> termGroups;

	@Column(name = "description", length = 4096)
	private String description;

	@JoinTable(
		name = "contract_stakeholder",
		uniqueConstraints = @UniqueConstraint(name = "uq_contract_stakeholder_stakeholder_id", columnNames = "stakeholder_id"),
		joinColumns = @JoinColumn(
			name = "contract_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_contract_stakeholder_contract_id")),
		inverseJoinColumns = @JoinColumn(
			name = "stakeholder_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_contract_stakeholder_stakeholder_id")))
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StakeholderEntity> stakeholders;

	@Column(name = "signed_by_witness")
	private boolean signedByWitness;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "contract_id",
		referencedColumnName = "id",
		foreignKey = @ForeignKey(name = "fk_extra_parameter_group_contract_id"))
	private List<ExtraParameterGroupEntity> extraParameters;

	@Column(name = "lease_type", length = 64)
	private LeaseType leaseType;

	@Embedded
	private LeaseholdEmbeddable leasehold;

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
	private List<PropertyDesignationEmbeddable> propertyDesignations;

	@ElementCollection
	@CollectionTable(
		name = "contract_notice",
		joinColumns = @JoinColumn(name = "contract_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_contract_notice_contract_id")))
	private List<NoticeTermEmbeddable> noticeTerms;

	@Column(name = "object_identity")
	private String objectIdentity;

	@Column(name = "lease_duration")
	private Integer leaseDuration;

	@Column(name = "lease_duration_unit", length = 32)
	private TimeUnit leaseDurationUnit;

	@Embedded
	private FeesEmbeddable fees;

	@Embedded
	private InvoicingEmbeddable invoicing;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "current_period_start_date")
	private LocalDate currentPeriodStartDate;

	@Column(name = "current_period_end_date")
	private LocalDate currentPeriodEndDate;

	@Column(name = "notice_date")
	private LocalDate noticeDate;

	@Column(name = "notice_given_by", length = 64)
	private Party noticeGivenBy;

	@Column(name = "auto_extend")
	private Boolean autoExtend;

	@Column(name = "lease_extension")
	private Integer leaseExtension;

	@Column(name = "lease_extension_unit", length = 32)
	private TimeUnit leaseExtensionUnit;

	@Column(name = "area")
	private Integer area;

	@Column(name = "area_data", length = Length.LONG32)
	private FeatureCollection areaData;

	@PrePersist
	public void prePersist() {
		this.version++;
	}
}
