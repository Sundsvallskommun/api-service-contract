package se.sundsvall.contract.integration.db.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "term_group")
public class TermGroupEntity {

	public static final String TYPE_INDEX = "INDEX";
	public static final String TYPE_ADDITIONAL = "ADDITIONAL";

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "header")
	private String header;

	@Column(name = "term_type", length = 32)
	private String type;

	@ElementCollection
	@CollectionTable(
		name = "term_group_term",
		joinColumns = @JoinColumn(
			name = "term_group_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_term_group_term_term_group_id")))
	private List<TermEmbeddable> terms;
}
