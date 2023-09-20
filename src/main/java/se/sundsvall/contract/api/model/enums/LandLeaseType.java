package se.sundsvall.contract.api.model.enums;

public enum LandLeaseType {
	LEASEHOLD("Arrende"), USUFRUCT("Nyttjanderätt"), SITELEASEHOLD("Tomträtt");

	private final String text;

	LandLeaseType(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
