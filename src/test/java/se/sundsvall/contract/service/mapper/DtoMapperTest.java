package se.sundsvall.contract.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.createAddressEntity;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContractEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;

class DtoMapperTest {

	@Test
	void testToContractDto() {
		// Arrange
		var contractEntity = createContractEntity();
		var attachments = List.of(createAttachmentEntity());

		// Act
		var dto = DtoMapper.toContractDto(contractEntity, attachments);

		assertThat(dto.getAdditionalTerms()).isEqualTo(contractEntity.getAdditionalTerms());
		assertThat(dto.getArea()).isEqualTo(contractEntity.getArea());
		assertThat(dto.getAreaData()).isEqualTo(contractEntity.getAreaData());
		assertThat(dto.getAttachmentMetaData()).isNotNull(); // Is tested in its own method
		assertThat(dto.getContractId()).isEqualTo(contractEntity.getContractId());
		assertThat(dto.getDescription()).isEqualTo(contractEntity.getDescription());
		assertThat(dto.getEnd()).isEqualTo(contractEntity.getEnd());
		assertThat(dto.getExternalReferenceId()).isEqualTo(contractEntity.getExternalReferenceId());
		assertThat(dto.getExtraParameters()).isEqualTo(contractEntity.getExtraParameters());
		assertThat(dto.getFees()).isNotNull(); // Is tested in its own method
		assertThat(dto.getIndexTerms()).isEqualTo(contractEntity.getIndexTerms());
		assertThat(dto.getInvoicing()).isNotNull(); // Is tested in its own method
		assertThat(dto.getLeaseType()).isEqualTo(contractEntity.getLeaseType());
		assertThat(dto.getDuration()).isNotNull(); // Is tested in its own method
		assertThat(dto.getExtension()).isNotNull(); // Is tested in its own method
		assertThat(dto.getLeasehold()).isNotNull(); // Is tested in its own method
		assertThat(dto.getMunicipalityId()).isEqualTo(contractEntity.getMunicipalityId());
		assertThat(dto.getObjectIdentity()).isEqualTo(contractEntity.getObjectIdentity());
		assertThat(dto.getNotices()).isNotNull(); // Is tested in its own method
		assertThat(dto.getPropertyDesignations())
			.flatExtracting(PropertyDesignation::getName, PropertyDesignation::getDistrict)
			.containsExactlyElementsOf(
				contractEntity.getPropertyDesignations().stream()
					.flatMap(prop -> Stream.of(prop.getName(), prop.getDistrict()))
					.toList());
		assertThat(dto.isSignedByWitness()).isEqualTo(contractEntity.isSignedByWitness());
		assertThat(dto.getStakeholders()).isNotNull(); // Is tested in its own method
		assertThat(dto.getStart()).isEqualTo(contractEntity.getStart());
		assertThat(dto.getStatus()).isEqualTo(contractEntity.getStatus());
		assertThat(dto.getType()).isEqualTo(contractEntity.getType());
		assertThat(dto.getVersion()).isEqualTo(contractEntity.getVersion());
	}

	@Test
	void testToFeesDto() {
		// Arrange
		var entity = createContractEntity();

		// Act
		var fees = DtoMapper.toFeesDto(entity);

		// Assert
		assertThat(fees.getMonthly()).isEqualTo(entity.getFees().getMonthly());
		assertThat(fees.getYearly()).isEqualTo(entity.getFees().getYearly());
		assertThat(fees.getMonthly()).isEqualTo(entity.getFees().getMonthly());
		assertThat(fees.getTotal()).isEqualTo(entity.getFees().getTotal());
		assertThat(fees.getTotalAsText()).isEqualTo(entity.getFees().getTotalAsText());
		assertThat(fees.getCurrency()).isEqualTo(entity.getFees().getCurrency());
		assertThat(fees.getIndexationRate()).isEqualTo(entity.getFees().getIndexationRate());
		assertThat(fees.getIndexYear()).isEqualTo(entity.getFees().getIndexYear());
		assertThat(fees.getIndexNumber()).isEqualTo(entity.getFees().getIndexNumber());
		assertThat(fees.getAdditionalInformation()).isEqualTo(entity.getFees().getAdditionalInformation());
		assertThat(fees.getIndexType()).isEqualTo(entity.getFees().getIndexType());
	}

	@Test
	void testToInvoicingDto() {
		// Arrange
		var entity = createContractEntity();

		// Act
		var invoicing = DtoMapper.toInvoicingDto(entity);

		// Assert
		assertThat(invoicing.getInvoicedIn()).isEqualTo(entity.getInvoicing().getInvoicedIn());
		assertThat(invoicing.getInvoiceInterval()).isEqualTo(entity.getInvoicing().getInvoiceInterval());
	}

	@Test
	void testToAttachmentMetaDataDto() {
		// Arrange
		var attachmentEntity = createAttachmentEntity();

		// Act
		var metadata = DtoMapper.toAttachmentMetaDataDto(attachmentEntity);

		// Assert
		var attachmentMetaData = AttachmentMetadata.builder()
			.withId(attachmentEntity.getId())
			.withCategory(attachmentEntity.getCategory())
			.withFilename(attachmentEntity.getFilename())
			.withMimeType(attachmentEntity.getMimeType())
			.withNote(attachmentEntity.getNote())
			.build();

		assertThat(metadata).isEqualTo(attachmentMetaData);
	}

	@Test
	void testToAttachmentMetaDataDtos() {
		// Arrange
		var attachmentEntities = List.of(createAttachmentEntity(), createAttachmentEntity());

		// Act
		var metadata = DtoMapper.toAttachmentMetadataDtos(attachmentEntities);

		// Assert
		// We only check that the size is correct, since the actual content is tested in the previous test
		assertThat(metadata).hasSize(2);
	}

	@Test
	void testToLeaseholdDto() {
		// Arrange
		var entity = createContractEntity();

		// Act
		var leasehold = DtoMapper.toLeaseholdDto(entity.getLeasehold());

		// Assert
		assertThat(leasehold.getPurpose()).isEqualTo(entity.getLeasehold().getPurpose());
		assertThat(leasehold.getDescription()).isEqualTo(entity.getLeasehold().getDescription());
		assertThat(leasehold.getAdditionalInformation()).isEqualTo(entity.getLeasehold().getAdditionalInformation());
	}

	@Test
	void testToDurationDto() {

		// Arrange
		var entity = createContractEntity();

		// Act
		var duration = DtoMapper.toDurationDto(entity);

		// Assert
		assertThat(duration).isNotNull();
		assertThat(duration.getLeaseDuration()).isEqualTo(20);
		assertThat(duration.getUnit()).isEqualTo(TimeUnit.MONTHS);
	}

	@Test
	void testToExtensionDto() {

		// Arrange
		var entity = createContractEntity();

		// Act
		var extension = DtoMapper.toExtensionDto(entity);

		// Assert
		assertThat(extension).isNotNull();
		assertThat(extension.getAutoExtend()).isTrue();
		assertThat(extension.getLeaseExtension()).isEqualTo(2);
		assertThat(extension.getUnit()).isEqualTo(TimeUnit.MONTHS);
	}

	@Test
	void testToStakeholderDtos() {
		// Arrange
		var entity = createContractEntity();

		// Act
		var stakeholders = DtoMapper.toStakeholderDtos(entity.getStakeholders());

		// Assert
		assertThat(stakeholders).isNotNull().isNotEmpty().hasSize(entity.getStakeholders().size());
	}

	@Test
	void testToStakeholderDto() {
		// Arrange
		var entity = createContractEntity().getStakeholders().getFirst();

		// Act
		var stakeholder = DtoMapper.toStakeholderDto(entity);

		// Assert
		assertThat(stakeholder.getAddress()).isNotNull(); // Is tested in its own method
		assertThat(stakeholder.getEmailAddress()).isEqualTo(entity.getEmailAddress());
		assertThat(stakeholder.getFirstName()).isEqualTo(entity.getFirstName());
		assertThat(stakeholder.getLastName()).isEqualTo(entity.getLastName());
		assertThat(stakeholder.getOrganizationName()).isEqualTo(entity.getOrganizationName());
		assertThat(stakeholder.getOrganizationNumber()).isEqualTo(entity.getOrganizationNumber());
		assertThat(stakeholder.getPartyId()).isEqualTo(entity.getPartyId());
		assertThat(stakeholder.getPhoneNumber()).isEqualTo(entity.getPhoneNumber());
		assertThat(stakeholder.getRoles()).containsAll(entity.getRoles().stream().toList());
		assertThat(stakeholder.getType()).isEqualTo(entity.getType());
	}

	@Test
	void testToAddressDto() {
		// Arrange
		var entity = createAddressEntity();

		// Act
		var address = DtoMapper.toAddressDto(entity);

		// Assert
		assertThat(address.getStreetAddress()).isEqualTo(entity.getStreetAddress());
		assertThat(address.getPostalCode()).isEqualTo(entity.getPostalCode());
		assertThat(address.getCountry()).isEqualTo(entity.getCountry());
		assertThat(address.getType()).isEqualTo(entity.getType());
		assertThat(address.getAttention()).isEqualTo(entity.getAttention());
		assertThat(address.getTown()).isEqualTo(entity.getTown());
	}

	@Test
	void testToAttachmentDto() {
		// Arrange
		var entity = createAttachmentEntity();

		// Act
		var attachment = DtoMapper.toAttachmentDto(entity);

		// Assert
		assertThat(attachment.getAttachmentData().getContent()).isEqualTo(new String(entity.getContent(), StandardCharsets.UTF_8));
		assertThat(attachment.getMetadata().getCategory()).isEqualTo(entity.getCategory());
		assertThat(attachment.getMetadata().getFilename()).isEqualTo(entity.getFilename());
		assertThat(attachment.getMetadata().getId()).isEqualTo(entity.getId());
		assertThat(attachment.getMetadata().getMimeType()).isEqualTo(entity.getMimeType());
		assertThat(attachment.getMetadata().getNote()).isEqualTo(entity.getNote());
	}

	@Test
	void testMinimalToContractDto() {

		// Arrange
		var contract = ContractEntity.builder()
			.withStatus(Status.DRAFT)
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		// Act
		var dto = DtoMapper.toContractDto(contract, List.of());

		// Assert
		assertThat(dto.getStatus()).isEqualTo(contract.getStatus());
		assertThat(dto.getType()).isEqualTo(contract.getType());
	}

	@Test
	void testToNoticeDtos() {

		// Arrange
		var entity = createContractEntity().getNotices();

		// Act
		var notices = DtoMapper.toNoticeDtos(entity);

		// Assert
		assertThat(notices)
			.hasSize(2)
			.containsExactlyInAnyOrder(
				Notice.builder()
					.withParty(Party.LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(TimeUnit.MONTHS)
					.build(),
				Notice.builder()
					.withParty(Party.LESSOR)
					.withPeriodOfNotice(1)
					.withUnit(TimeUnit.MONTHS)
					.build());
	}
}
