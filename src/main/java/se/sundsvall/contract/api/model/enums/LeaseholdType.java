package se.sundsvall.contract.api.model.enums;

public enum LeaseholdType {
	APARTMENT("Lägenhetsarrende"), BUILDING("Anläggningsarrende"), AGRICULTURE("Jordbruksarrende"),
	DWELLING("Bostadsarrende"), OTHER("Annat");

	private final String text;

	LeaseholdType(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
