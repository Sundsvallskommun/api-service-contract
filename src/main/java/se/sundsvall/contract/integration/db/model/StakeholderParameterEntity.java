package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "stakeholder_parameter")
public class StakeholderParameterEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stakeholder_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stakeholder_parameter_stakeholder_id"))
	private StakeholderEntity stakeholderEntity;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "parameters_key")
	private String key;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "stakeholder_parameter_values",
		joinColumns = @JoinColumn(name = "stakeholder_parameter_id", foreignKey = @ForeignKey(name = "fk_stakeholder_parameter_values_stakeholder_parameter_id")))
	@Column(name = "value")
	private List<String> values;
}
