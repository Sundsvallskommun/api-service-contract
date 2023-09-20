package se.sundsvall.contract.api.model.enums;

public enum StakeholderRole {

	ARRENDATOR("Arrendator"), FIRMATECKNARE("Firmatecknare"), FULLMAKTSROLL("Fullmaktsroll"),
	KONTAKTPERSON("Kontaktperson"), MARKAGARE("Markägare");

	private final String text;

	StakeholderRole(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
