package se.sundsvall.contract.integration.db.model;

import se.sundsvall.contract.integration.db.model.converter.enums.AddressTypeConverter;
import se.sundsvall.contract.model.enums.AddressType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Table(name = "address")
public class AddressEntity {

	@Column(name = "address_type")
	@Convert(converter = AddressTypeConverter.class)
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
