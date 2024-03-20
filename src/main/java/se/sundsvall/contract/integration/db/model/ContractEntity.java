package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.EnumType.STRING;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import se.sundsvall.contract.integration.db.model.converter.ExtraParameterGroupConverter;
import se.sundsvall.contract.integration.db.model.converter.TermGroupConverter;
import se.sundsvall.contract.integration.db.model.generator.GenerateOnInsert;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contract",
	uniqueConstraints = @UniqueConstraint(columnNames = { "contract_id", "version" }))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ContractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@GenerateOnInsert
	@Column(name = "contract_id", length = 10, nullable = false)
	private String contractId;

	@Column(name = "version")
	private int version = 1;

	@Enumerated(STRING)
	@Column(name = "type", updatable = false)
	private ContractType type;

	@Enumerated(STRING)
	@Column(name = "status")
	private Status status;

	@Column(name = "municipality_id")
	private String municipalityId;

	@Column(name = "case_id")
	private Long caseId;

	@Column(name = "index_terms")
	@Convert(converter = TermGroupConverter.class)
	private List<TermGroup> indexTerms;

	@Column(name = "description")
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

	@PrePersist
	@PreUpdate
	public void prePersist() {
		this.version++;
	}

	//Excluding stakeholders and attachments from equals, hashcode and toString
	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if (!(o instanceof ContractEntity that)) {
            return false;
        }
		return Objects.equals(id, that.id) &&
			type == that.type &&
			Objects.equals(version, that.version) &&
			status == that.status &&
			Objects.equals(caseId, that.caseId) &&
			Objects.equals(indexTerms, that.indexTerms) &&
			Objects.equals(description, that.description) &&
			Objects.equals(additionalTerms, that.additionalTerms) &&
			Objects.equals(extraParameters, that.extraParameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, version, status, caseId, indexTerms, description, additionalTerms, extraParameters);
	}

	@Override
	public String toString() {
		return "ContractEntity{" +
			"id=" + id +
			", version=" + version +
			", status=" + status +
			", caseId=" + caseId +
			", indexTerms='" + indexTerms + '\'' +
			", description='" + description + '\'' +
			", additionalTerms='" + additionalTerms + '\'' +
			", extraParameters='" + extraParameters + '\'' +
			'}';
	}
}
