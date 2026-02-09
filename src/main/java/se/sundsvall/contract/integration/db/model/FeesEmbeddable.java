package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class FeesEmbeddable {

	@Column(name = "fee_currency")
	private String currency;

	@Column(name = "fee_yearly")
	private BigDecimal yearly;

	@Column(name = "fee_monthly")
	private BigDecimal monthly;

	@Column(name = "fee_total")
	private BigDecimal total;

	@Column(name = "fee_total_as_text")
	private String totalAsText;

	@Column(name = "fee_index_type")
	private String indexType;

	@Column(name = "fee_index_year")
	private Integer indexYear;

	@Column(name = "fee_index_number")
	private Integer indexNumber;

	@Column(name = "fee_indexation_rate")
	private BigDecimal indexationRate;

	@ElementCollection
	@CollectionTable(
		name = "fee_additional_information",
		joinColumns = @JoinColumn(
			name = "contract_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_fee_additional_information_contract_id")))
	private List<String> additionalInformation;
}
