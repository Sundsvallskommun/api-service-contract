package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.model.enums.AddressType;

@Embeddable
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class AddressEmbeddable {

	@Column(name = "address_type", length = 64)
	private AddressType type;

	@Column(name = "street_address")
	private String streetAddress;

	@Column(name = "care_of")
	private String careOf;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "town")
	private String town;

	@Column(name = "country")
	private String country;

	@Column(name = "attention")
	private String attention;
}
