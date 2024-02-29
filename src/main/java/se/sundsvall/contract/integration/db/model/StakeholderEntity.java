package se.sundsvall.contract.integration.db.model;

import java.util.List;
import java.util.Objects;

import se.sundsvall.contract.api.model.enums.StakeholderRole;
import se.sundsvall.contract.api.model.enums.StakeholderType;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(setterPrefix = "with")
@Table(name = "stakeholder")
public class StakeholderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private StakeholderType type;

	@ElementCollection
	@JoinTable(name = "stakeholder_roles")
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private List<StakeholderRole> roles;

	@Column(name = "organization_name")
	private String organizationName;

	@Column(name = "organization_number")
	private String organizationNumber;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "person_id")
	private String personId;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email_address")
	private String emailAddress;

	@Embedded
	private AddressEntity address;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StakeholderEntity that)) return false;
		return Objects.equals(id, that.id) && type == that.type && Objects.equals(organizationName, that.organizationName) && Objects.equals(organizationNumber, that.organizationNumber) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(personId, that.personId) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(emailAddress, that.emailAddress) && Objects.equals(address, that.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, organizationName, organizationNumber, firstName, lastName, personId, phoneNumber, emailAddress, address);
	}
}
