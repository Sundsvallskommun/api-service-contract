package se.sundsvall.contract.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contract type", enumAsRef = true)
public enum ContractType {
	LEASE_AGREEMENT,
	PURCHASE_AGREEMENT,
	LAND_LEASE_PUBLIC,
	SHORT_TERM_LEASE_AGREEMENT
}
