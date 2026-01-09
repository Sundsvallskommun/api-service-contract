package se.sundsvall.contract.service.businessrule;

public class BusinessruleException extends RuntimeException {
	private static final long serialVersionUID = -4487795425119652802L;

	public BusinessruleException(String message, Throwable cause) {
		super(message, cause);
	}
}
