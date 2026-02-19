package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "extra_parameter_group")
public class ExtraParameterGroupEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@ElementCollection
	@CollectionTable(
		name = "extra_parameter",
		joinColumns = @JoinColumn(
			name = "extra_parameter_group_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_extra_parameter_extra_parameter_group_id")))
	@MapKeyColumn(name = "parameter_key")
	@Column(name = "parameter_value")
	private Map<String, String> parameters;
}
