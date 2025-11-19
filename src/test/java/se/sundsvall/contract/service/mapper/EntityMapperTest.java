package se.sundsvall.contract.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.createAttachment;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContract;
import static se.sundsvall.contract.TestFactory.createContractEntity;

import org.junit.jupiter.api.Test;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.integration.db.model.NoticeEmbeddable;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;

class EntityMapperTest {

	private static final String MUNICIPALITY_ID = "1984";

	private EntityMapper mapper = new EntityMapper();

	@Test
	void testToContractEntity() {

		// Arrange
		final var dto = createContract();

		// Act
		final var entity = mapper.toContractEntity(MUNICIPALITY_ID, dto);

		// Assert
		assertThat(entity.getAdditionalTerms()).isEqualTo(dto.getAdditionalTerms());
		assertThat(entity.getArea()).isEqualTo(dto.getArea());
		assertThat(entity.getAreaData()).isEqualTo(dto.getAreaData());
		assertThat(entity.getContractId()).isEqualTo(dto.getContractId());
		assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
		assertThat(entity.getEnd()).isEqualTo(dto.getEnd());
		assertThat(entity.getExternalReferenceId()).isEqualTo(dto.getExternalReferenceId());
		assertThat(entity.getExtraParameters()).isEqualTo(dto.getExtraParameters());
		assertThat(entity.getFees()).isEqualTo(dto.getFees());
		assertThat(entity.getIndexTerms()).isEqualTo(dto.getIndexTerms());
		assertThat(entity.getInvoicing()).isNotNull();  // Is tested in its own method
		assertThat(entity.getLeaseType()).isEqualTo(dto.getLeaseType());
		assertThat(entity.getLeaseDuration()).isEqualTo(dto.getDuration().getLeaseDuration());
		assertThat(entity.getLeaseDurationUnit()).isEqualTo(dto.getDuration().getUnit());
		assertThat(entity.getLeaseExtension()).isEqualTo(dto.getExtension().getLeaseExtension());
		assertThat(entity.getLeaseExtensionUnit()).isEqualTo(dto.getExtension().getUnit());
		assertThat(entity.getAutoExtend()).isEqualTo(dto.getExtension().getAutoExtend());
		assertThat(entity.getLeasehold()).isNotNull(); // Is tested in its own method
		assertThat(entity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entity.getObjectIdentity()).isEqualTo(dto.getObjectIdentity());
		assertThat(entity.getNotices()).isNotNull(); // Is tested in its own method
		assertThat(entity.getPropertyDesignations()).isEqualTo(dto.getPropertyDesignations());
		assertThat(entity.isSignedByWitness()).isEqualTo(dto.isSignedByWitness());
		assertThat(entity.getStakeholders()).isNotNull(); // Is tested in its own method
		assertThat(entity.getStart()).isEqualTo(dto.getStart());
		assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
		assertThat(entity.getType()).isEqualTo(dto.getType());
		assertThat(entity.getVersion()).isEqualTo(dto.getVersion());
	}

	@Test
	void testToInvoicingEntity() {

		// Arrange
		final var dto = createContract();

		// Act
		final var entity = mapper.toInvoicingEntity(dto.getInvoicing());

		// Assert
		assertThat(entity.getInvoicedIn()).isEqualTo(dto.getInvoicing().getInvoicedIn());
		assertThat(entity.getInvoiceInterval()).isEqualTo(dto.getInvoicing().getInvoiceInterval());
	}

	@Test
	void testToStakeholderEntities() {

		// Arrange
		final var dto = createContract();

		// Act
		final var entities = mapper.toStakeholderEntities(dto.getStakeholders());

		// Assert
		assertThat(entities).isNotNull().isNotEmpty().hasSize(dto.getStakeholders().size());
	}

	@Test
	void testToStakeholderEntity() {

		// Arrange
		final var dto = createContract().getStakeholders().getFirst();

		// Act
		final var entity = mapper.toStakeholderEntity(dto);

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
		var array = dto.getRoles().stream().toList();
		assertThat(entity.getRoles()).containsAll(array);
	}

	@Test
	void testToLeaseholdEntity() {

		// Arrange
		final var dto = createContract().getLeasehold();

		// Act
		final var entity = mapper.toLeaseholdEntity(dto);

		// Assert
		assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
		assertThat(entity.getAdditionalInformation()).isEqualTo(dto.getAdditionalInformation());
		assertThat(entity.getPurpose()).isEqualTo(dto.getPurpose());
	}

	@Test
	void testToAddressEntity() {

		// Arrange
		final var dto = createContract().getStakeholders().getFirst().getAddress();

		// Act
		final var entity = mapper.toAddressEntity(dto);

		// Assert
		assertThat(entity.getAttention()).isEqualTo(dto.getAttention());
		assertThat(entity.getCountry()).isEqualTo(dto.getCountry());
		assertThat(entity.getPostalCode()).isEqualTo(dto.getPostalCode());
		assertThat(entity.getStreetAddress()).isEqualTo(dto.getStreetAddress());
		assertThat(entity.getTown()).isEqualTo(dto.getTown());
		assertThat(entity.getType()).isEqualTo(dto.getType());
	}

	@Test
	void testToAttachmentEntity() {

		// Arrange
		final var dto = createAttachment();

		// Act
		final var entity = mapper.toAttachmentEntity(MUNICIPALITY_ID, "2024-12345", dto);

		// Assert
		assertThat(entity.getCategory()).isEqualTo(dto.getMetadata().getCategory());
		assertThat(entity.getContent()).isEqualTo(dto.getAttachmentData().getContent().getBytes());
		assertThat(entity.getContractId()).isEqualTo("2024-12345");
		assertThat(entity.getFilename()).isEqualTo(dto.getMetadata().getFilename());
		assertThat(entity.getMimeType()).isEqualTo(dto.getMetadata().getMimeType());
		assertThat(entity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entity.getNote()).isEqualTo(dto.getMetadata().getNote());
	}

	@Test
	void testUpdateAttachmentEntity() {

		// Arrange
		final var entity = createAttachmentEntity();
		final var dto = createAttachment();

		// Act
		final var updatedEntity = mapper.updateAttachmentEntity(entity, dto);

		// Assert
		assertThat(updatedEntity.getCategory()).isEqualTo(dto.getMetadata().getCategory());
		assertThat(updatedEntity.getFilename()).isEqualTo(dto.getMetadata().getFilename());
		assertThat(updatedEntity.getMimeType()).isEqualTo(dto.getMetadata().getMimeType());
		assertThat(updatedEntity.getNote()).isEqualTo(dto.getMetadata().getNote());
		assertThat(updatedEntity.getContent()).isEqualTo(dto.getAttachmentData().getContent().getBytes());
	}

	@Test
	void testCreateNewContractEntity() {

		// Arrange
		final var oldContractEntity = createContractEntity();
		final var newContract = createContract();

		// Act
		final var updatedEntity = mapper.createNewContractEntity(MUNICIPALITY_ID, oldContractEntity, newContract);

		assertThat(updatedEntity.getVersion()).isEqualTo(oldContractEntity.getVersion());
		assertThat(updatedEntity.getContractId()).isEqualTo(oldContractEntity.getContractId());
	}

	@Test
	void testMinimalToContractEntity() {

		// Arrange
		final var dto = Contract.builder()
			.withStatus(Status.DRAFT)
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		// Act
		final var entity = mapper.toContractEntity(MUNICIPALITY_ID, dto);

		// Assert
		assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
		assertThat(entity.getType()).isEqualTo(dto.getType());
	}

	@Test
	void testToNoticeEmbeddables() {

		// Arrange
		final var entity = createContract();

		// Act
		final var noticeEmbeddables = mapper.toNoticeEmbeddables(entity.getNotices());

		// Assert
		assertThat(noticeEmbeddables)
			.hasSize(2)
			.containsExactlyInAnyOrder(
				NoticeEmbeddable.builder()
					.withParty(Party.LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(TimeUnit.MONTHS)
					.build(),
				NoticeEmbeddable.builder()
					.withParty(Party.LESSOR)
					.withPeriodOfNotice(1)
					.withUnit(TimeUnit.MONTHS)
					.build());
	}
}
