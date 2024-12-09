package se.sundsvall.contract.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.createAttachment;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContract;
import static se.sundsvall.contract.TestFactory.createContractEntity;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.model.enums.AddressType;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.contract.model.enums.LeaseholdType;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.UsufructType;

class EntityMapperTest {

	private EntityMapper mapper = new EntityMapper();

	private static final String MUNICIPALITY_ID = "1984";

	@Test
	void testToContractEntity() {
		// Arrange
		var dto = createContract();

		// Act
		var entity = mapper.toContractEntity(MUNICIPALITY_ID, dto);

		// Assert
		assertThat(entity.getAdditionalTerms()).isEqualTo(dto.getAdditionalTerms());
		assertThat(entity.getArea()).isEqualTo(dto.getArea());
		assertThat(entity.getAreaData()).isEqualTo(dto.getAreaData());
		assertThat(entity.getAutoExtend()).isEqualTo(dto.getAutoExtend());
		assertThat(entity.getContractId()).isEqualTo(dto.getContractId());
		assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
		assertThat(entity.getEnd()).isEqualTo(dto.getEnd());
		assertThat(entity.getExternalReferenceId()).isEqualTo(dto.getExternalReferenceId());
		assertThat(entity.getExtraParameters()).isEqualTo(dto.getExtraParameters());
		assertThat(entity.getFees()).isEqualTo(dto.getFees());
		assertThat(entity.getFees()).isEqualTo(dto.getFees());
		assertThat(entity.getIndexTerms()).isEqualTo(dto.getIndexTerms());
		assertThat(entity.getInvoicing()).isNotNull();  // Is tested in its own method
		assertThat(entity.getLandLeaseType()).isEqualTo(LandLeaseType.valueOf(dto.getLandLeaseType()));
		assertThat(entity.getLeaseDuration()).isEqualTo(dto.getLeaseDuration());
		assertThat(entity.getLeaseExtension()).isEqualTo(dto.getLeaseExtension());
		assertThat(entity.getLeasehold()).isNotNull(); // Is tested in its own method
		assertThat(entity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entity.getObjectIdentity()).isEqualTo(dto.getObjectIdentity());
		assertThat(entity.getPeriodOfNotice()).isEqualTo(dto.getPeriodOfNotice());
		assertThat(entity.getPropertyDesignations()).isEqualTo(dto.getPropertyDesignations());
		assertThat(entity.isSignedByWitness()).isEqualTo(dto.isSignedByWitness());
		assertThat(entity.getStakeholders()).isNotNull(); // Is tested in its own method
		assertThat(entity.getStart()).isEqualTo(dto.getStart());
		assertThat(entity.getStatus()).isEqualTo(Status.valueOf(dto.getStatus()));
		assertThat(entity.getType()).isEqualTo(ContractType.valueOf(dto.getType()));
		assertThat(entity.getUsufructType()).isEqualTo(UsufructType.valueOf(dto.getUsufructType()));
		assertThat(entity.getVersion()).isEqualTo(dto.getVersion());
	}

	@Test
	void testToInvoicingEntity() {
		// Arrange
		var dto = createContract();

		// Act
		var entity = mapper.toInvoicingEntity(dto.getInvoicing());

		// Assert
		assertThat(entity.getInvoicedIn()).isEqualTo(InvoicedIn.valueOf(dto.getInvoicing().getInvoicedIn()));
		assertThat(entity.getInvoiceInterval()).isEqualTo(IntervalType.valueOf(dto.getInvoicing().getInvoiceInterval()));
	}

	@Test
	void testToStakeholderEntities() {
		// Arrange
		var dto = createContract();

		// Act
		var entities = mapper.toStakeholderEntities(dto.getStakeholders());

		// Assert
		assertThat(entities).isNotNull().isNotEmpty().hasSize(dto.getStakeholders().size());
	}

	@Test
	void testToStakeholderEntity() {
		// Arrange
		var dto = createContract().getStakeholders().getFirst();

		// Act
		var entity = mapper.toStakeholderEntity(dto);

		// Assert
		assertThat(entity.getAddress()).isNotNull();    // Mapping is tested in another method
		assertThat(entity.getEmailAddress()).isEqualTo(dto.getEmailAddress());
		assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
		assertThat(entity.getLastName()).isEqualTo(dto.getLastName());
		assertThat(entity.getOrganizationName()).isEqualTo(dto.getOrganizationName());
		assertThat(entity.getOrganizationNumber()).isEqualTo(dto.getOrganizationNumber());
		assertThat(entity.getPartyId()).isEqualTo(dto.getPartyId());
		assertThat(entity.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
		// Assert that all roles are mapped
		var array = dto.getRoles().stream().map(StakeholderRole::valueOf).toList();
		assertThat(entity.getRoles()).containsAll(array);
	}

	@Test
	void testToLeaseholdEntity() {
		// Arrange
		var dto = createContract().getLeasehold();

		// Act
		var entity = mapper.toLeaseholdEntity(dto);

		// Assert
		assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
		assertThat(entity.getAdditionalInformation()).isEqualTo(dto.getAdditionalInformation());
		assertThat(entity.getPurpose()).isEqualTo(LeaseholdType.valueOf(dto.getPurpose()));
	}

	@Test
	void testToAddressEntity() {
		// Arrange
		var dto = createContract().getStakeholders().getFirst().getAddress();

		// Act
		var entity = mapper.toAddressEntity(dto);

		// Assert
		assertThat(entity.getAttention()).isEqualTo(dto.getAttention());
		assertThat(entity.getCountry()).isEqualTo(dto.getCountry());
		assertThat(entity.getPostalCode()).isEqualTo(dto.getPostalCode());
		assertThat(entity.getStreetAddress()).isEqualTo(dto.getStreetAddress());
		assertThat(entity.getTown()).isEqualTo(dto.getTown());
		assertThat(entity.getType()).isEqualTo(AddressType.valueOf(dto.getType()));
	}

	@Test
	void testToAttachmentEntity() {
		// Arrange
		var dto = createAttachment();

		// Act
		var entity = mapper.toAttachmentEntity(MUNICIPALITY_ID, "2024-12345", dto);

		// Assert
		assertThat(entity.getCategory()).isEqualTo(AttachmentCategory.valueOf(dto.getMetaData().getCategory()));
		assertThat(entity.getContent()).isEqualTo(dto.getAttachmentData().getContent().getBytes());
		assertThat(entity.getContractId()).isEqualTo("2024-12345");
		assertThat(entity.getFilename()).isEqualTo(dto.getMetaData().getFilename());
		assertThat(entity.getMimeType()).isEqualTo(dto.getMetaData().getMimeType());
		assertThat(entity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entity.getNote()).isEqualTo(dto.getMetaData().getNote());
	}

	@Test
	void testUpdateAttachmentEntity() {
		// Arrange
		var entity = createAttachmentEntity();
		var dto = createAttachment();

		// Act
		var updatedEntity = mapper.updateAttachmentEntity(entity, dto);

		// Assert
		assertThat(updatedEntity.getCategory()).isEqualTo(AttachmentCategory.valueOf(dto.getMetaData().getCategory()));
		assertThat(updatedEntity.getFilename()).isEqualTo(dto.getMetaData().getFilename());
		assertThat(updatedEntity.getMimeType()).isEqualTo(dto.getMetaData().getMimeType());
		assertThat(updatedEntity.getNote()).isEqualTo(dto.getMetaData().getNote());
		assertThat(updatedEntity.getContent()).isEqualTo(dto.getAttachmentData().getContent().getBytes());
	}

	@Test
	void testCreateNewContractEntity() {
		// Arrange
		var oldContractEntity = createContractEntity();
		var newContract = createContract();

		// Act
		var updatedEntity = mapper.createNewContractEntity(MUNICIPALITY_ID, oldContractEntity, newContract);

		assertThat(updatedEntity.getVersion()).isEqualTo(oldContractEntity.getVersion());
		assertThat(updatedEntity.getContractId()).isEqualTo(oldContractEntity.getContractId());
	}

	@Test
	void testMinimalToContractEntity() {
		// Arrange
		var dto = Contract.builder()
			.withStatus(Status.DRAFT.name())
			.withType(ContractType.LAND_LEASE.name())
			.build();

		// Act
		var entity = mapper.toContractEntity(MUNICIPALITY_ID, dto);

		// Assert
		assertThat(entity.getStatus()).isEqualTo(Status.valueOf(dto.getStatus()));
		assertThat(entity.getType()).isEqualTo(ContractType.valueOf(dto.getType()));
	}
}
