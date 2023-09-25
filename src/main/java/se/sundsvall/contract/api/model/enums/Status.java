package se.sundsvall.contract.api.model.enums;

public enum Status {
	ACTIVE("Aktiv"), TERMINATED("Avslutad");

	private final String text;

	Status(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
