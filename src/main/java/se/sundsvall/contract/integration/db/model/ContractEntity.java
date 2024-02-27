package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.EnumType.STRING;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import se.sundsvall.contract.api.model.enums.Status;

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
@Table(name = "contract")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ContractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "version")
	private Integer version;

	@Enumerated(STRING)
	private Status status;

	@Column(name = "case_id")
	private Long caseId;

	@Column(name = "index_terms")
	private String indexTerms;

	@Column(name = "description")
	private String description;

	@Column(name = "additional_terms")
	private String additionalTerms;

	@OneToMany(cascade = CascadeType.ALL)
	private List<StakeholderEntity> stakeholders;

	@OneToMany(cascade = CascadeType.ALL)
	private List<AttachmentEntity> attachments;

	@Column(name = "signed_by_witness")
	private boolean signedByWitness;

	//Excluding stakeholders and attachments from equals, hashcode and toString
	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if (!(o instanceof ContractEntity that)) {
            return false;
        }
		return Objects.equals(id, that.id) && Objects.equals(version, that.version) && status == that.status && Objects.equals(caseId, that.caseId) && Objects.equals(indexTerms, that.indexTerms) && Objects.equals(description, that.description) && Objects.equals(additionalTerms, that.additionalTerms);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, version, status, caseId, indexTerms, description, additionalTerms);
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
			'}';
	}
}
