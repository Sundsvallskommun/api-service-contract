package se.sundsvall.contract.api.model.enums;

public enum AddressType {
	POSTAL_ADDRESS("Postadress"), BILLING_ADDRESS("Fakturaadress"), VISITING_ADDRESS("Besöksadress");

	private final String type;

	AddressType(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
