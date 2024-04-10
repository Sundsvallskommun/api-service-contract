package se.sundsvall.contract.integration.db.model;

import java.util.List;

import se.sundsvall.contract.integration.db.model.converter.enums.LeaseholdTypeConverter;
import se.sundsvall.contract.model.enums.LeaseholdType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
public class LeaseholdEntity {

	@Column(name = "leasehold_type")
	@Convert(converter = LeaseholdTypeConverter.class)
	private LeaseholdType purpose;

	@Column(name = "leasehold_description")
	private String description;

	@ElementCollection
	@CollectionTable(
		name = "land_lease_contract_leasehold_additional_information",
		joinColumns = @JoinColumn(
			name = "land_lease_contract_id",
			referencedColumnName = "id",
			foreignKey = @ForeignKey(name = "fk_llc_leasehold_additional_information_land_lease_contract_id")
		)
	)
	private List<String> additionalInformation;
}
