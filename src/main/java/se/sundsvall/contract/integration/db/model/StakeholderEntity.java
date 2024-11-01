package se.sundsvall.contract.integration.db.model;

import java.util.List;
import java.util.Objects;

import se.sundsvall.contract.integration.db.model.converter.enums.StakeholderRoleConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.StakeholderTypeConverter;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Table(name = "stakeholder")
public class StakeholderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "type")
	@Convert(converter = StakeholderTypeConverter.class)
	private StakeholderType type;

	@Column(name = "roles")
	@Convert(converter = StakeholderRoleConverter.class)
	private List<StakeholderRole> roles;

	@Column(name = "organization_name")
	private String organizationName;

	@Column(name = "organization_number")
	private String organizationNumber;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email_address")
	private String emailAddress;

	@Embedded
	private AddressEntity address;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StakeholderEntity that)) {
			return false;
		}
		return Objects.equals(id, that.id) && type == that.type && Objects.equals(organizationName, that.organizationName) && Objects.equals(organizationNumber, that.organizationNumber) && Objects.equals(firstName, that.firstName) && Objects.equals(
			lastName, that.lastName) && Objects.equals(partyId, that.partyId) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(emailAddress, that.emailAddress) && Objects.equals(address, that.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, organizationName, organizationNumber, firstName, lastName, partyId, phoneNumber, emailAddress, address);
	}
}
