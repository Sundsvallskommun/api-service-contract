package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.integration.db.model.converter.enums.StakeholderRoleConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.StakeholderTypeConverter;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;

@Entity
@Data
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

	@OneToMany(mappedBy = "stakeholderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "parameter_order", nullable = false, columnDefinition = "integer default 0")
	private List<StakeholderParameterEntity> parameters;

}
