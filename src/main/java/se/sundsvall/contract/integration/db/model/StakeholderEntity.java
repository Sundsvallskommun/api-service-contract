package se.sundsvall.contract.integration.db.model;

import java.util.List;

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

import se.sundsvall.contract.api.model.enums.StakeholderRole;
import se.sundsvall.contract.api.model.enums.StakeholderType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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

}
