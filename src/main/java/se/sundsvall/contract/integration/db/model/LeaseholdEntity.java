package se.sundsvall.contract.integration.db.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import se.sundsvall.contract.api.model.enums.LeaseholdType;

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
@Table(name = "leasehold")
public class LeaseholdEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "leasehold_type")
	private LeaseholdType type;

	@Column(name = "leasehold_description")
	private String description;

}
