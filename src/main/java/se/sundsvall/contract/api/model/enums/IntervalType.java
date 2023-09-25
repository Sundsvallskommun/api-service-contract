package se.sundsvall.contract.api.model.enums;

public enum IntervalType {
	YEARLY("årligen"), QUARTERLY("kvartalsvis"), MONTHLY("månadsvis");

	private final String type;

	IntervalType(final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
