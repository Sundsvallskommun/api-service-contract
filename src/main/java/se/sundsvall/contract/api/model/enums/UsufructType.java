package se.sundsvall.contract.api.model.enums;

public enum UsufructType {
	HUNTING("Jakt"), FISHING("Fiske"), MAINTENANCE("Skötsel"), OTHER("Övrigt");

	private final String text;

	UsufructType(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
