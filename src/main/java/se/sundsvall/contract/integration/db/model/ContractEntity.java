package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.EnumType.STRING;

import java.util.List;

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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
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

}
