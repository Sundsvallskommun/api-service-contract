package se.sundsvall.contract.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.createAddressEntity;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContractEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.model.enums.StakeholderRole;

class DtoMapperTest {

	private final DtoMapper mapper = new DtoMapper();

	@Test
	void testToContractDto() {
		//Arrange
		var contractEntity = createContractEntity();
		var attachments = List.of(createAttachmentEntity());

		//Act
		var dto = mapper.toContractDto(contractEntity, attachments);

		assertThat(dto.getAdditionalTerms()).isEqualTo(contractEntity.getAdditionalTerms());
		assertThat(dto.getArea()).isEqualTo(contractEntity.getArea());
		assertThat(dto.getAreaData()).isEqualTo(contractEntity.getAreaData());
		assertThat(dto.getAttachmentMetaData()).isNotNull(); //Is tested in its own method
		assertThat(dto.getAutoExtend()).isEqualTo(contractEntity.getAutoExtend());
		assertThat(dto.getContractId()).isEqualTo(contractEntity.getContractId());
		assertThat(dto.getDescription()).isEqualTo(contractEntity.getDescription());
		assertThat(dto.getEnd()).isEqualTo(contractEntity.getEnd());
		assertThat(dto.getExternalReferenceId()).isEqualTo(contractEntity.getExternalReferenceId());
		assertThat(dto.getExtraParameters()).isEqualTo(contractEntity.getExtraParameters());
		assertThat(dto.getFees()).isNotNull(); //Is tested in its own method
		assertThat(dto.getIndexTerms()).isEqualTo(contractEntity.getIndexTerms());
		assertThat(dto.getInvoicing()).isNotNull(); //Is tested in its own method
		assertThat(dto.getLandLeaseType()).isEqualTo(contractEntity.getLandLeaseType().name());
		assertThat(dto.getLeaseDuration()).isEqualTo(contractEntity.getLeaseDuration());
		assertThat(dto.getLeaseExtension()).isEqualTo(contractEntity.getLeaseExtension());
		assertThat(dto.getLeasehold()).isNotNull(); //Is tested in its own method
		assertThat(dto.getMunicipalityId()).isEqualTo(contractEntity.getMunicipalityId());
		assertThat(dto.getObjectIdentity()).isEqualTo(contractEntity.getObjectIdentity());
		assertThat(dto.getPeriodOfNotice()).isEqualTo(contractEntity.getPeriodOfNotice());
		assertThat(dto.getPropertyDesignations()).isEqualTo(contractEntity.getPropertyDesignations());
		assertThat(dto.isSignedByWitness()).isEqualTo(contractEntity.isSignedByWitness());
		assertThat(dto.getStakeholders()).isNotNull(); //Is tested in its own method
		assertThat(dto.getStart()).isEqualTo(contractEntity.getStart());
		assertThat(dto.getStatus()).isEqualTo(contractEntity.getStatus().name());
		assertThat(dto.getType()).isEqualTo(contractEntity.getType().name());
		assertThat(dto.getUsufructType()).isEqualTo(contractEntity.getUsufructType().name());
		assertThat(dto.getVersion()).isEqualTo(contractEntity.getVersion());
	}

	@Test
	void testToFeesDto() {
		//Arrange
		var entity = createContractEntity();

		//Act
		var fees = mapper.toFeesDto(entity);

		//Assert
		assertThat(fees.getMonthly()).isEqualTo(entity.getFees().getMonthly());
		assertThat(fees.getYearly()).isEqualTo(entity.getFees().getYearly());
		assertThat(fees.getMonthly()).isEqualTo(entity.getFees().getMonthly());
		assertThat(fees.getTotal()).isEqualTo(entity.getFees().getTotal());
		assertThat(fees.getTotalAsText()).isEqualTo(entity.getFees().getTotalAsText());
		assertThat(fees.getCurrency()).isEqualTo(entity.getFees().getCurrency());
		assertThat(fees.getIndexYear()).isEqualTo(entity.getFees().getIndexYear());
		assertThat(fees.getIndexNumber()).isEqualTo(entity.getFees().getIndexNumber());
		assertThat(fees.getAdditionalInformation()).isEqualTo(entity.getFees().getAdditionalInformation());
	}

	@Test
	void testToInvoicingDto() {
		//Arrange
		var entity = createContractEntity();

		//Act
		var invoicing = mapper.toInvoicingDto(entity);

		//Assert
		assertThat(invoicing.getInvoicedIn()).isEqualTo(entity.getInvoicing().getInvoicedIn().name());
		assertThat(invoicing.getInvoiceInterval()).isEqualTo(entity.getInvoicing().getInvoiceInterval().name());
	}

	@Test
	void testToAttachmentMetaDataDto() {
		//Arrange
		var attachmentEntity = createAttachmentEntity();

		//Act
		var metadata = mapper.toAttachmentMetaDataDto(attachmentEntity);

		//Assert
		assertThat(metadata).isEqualTo(new AttachmentMetaData(
			attachmentEntity.getId(),
			attachmentEntity.getCategory().name(),
			attachmentEntity.getFilename(),
			attachmentEntity.getMimeType(),
			attachmentEntity.getNote()));
	}

	@Test
	void testToAttachmentMetaDataDtos() {
		//Arrange
		var attachmentEntities = List.of(createAttachmentEntity(), createAttachmentEntity());

		//Act
		var metadata = mapper.toAttachmentMetadataDtos(attachmentEntities);

		//Assert
		//We only check that the size is correct, since the actual content is tested in the previous test
		assertThat(metadata).hasSize(2);
	}

	@Test
	void testToLeaseholdDto() {
		//Arrange
		var entity = createContractEntity();

		//Act
		var leasehold = mapper.toLeaseholdDto(entity.getLeasehold());

		//Assert
		assertThat(leasehold.getPurpose()).isEqualTo(entity.getLeasehold().getPurpose().name());
		assertThat(leasehold.getDescription()).isEqualTo(entity.getLeasehold().getDescription());
		assertThat(leasehold.getAdditionalInformation()).isEqualTo(entity.getLeasehold().getAdditionalInformation());
	}

	@Test
	void testToStakeholderDtos() {
		//Arrange
		var entity = createContractEntity();

		//Act
		var stakeholders = mapper.toStakeholderDtos(entity.getStakeholders());

		//Assert
		assertThat(stakeholders).isNotNull().isNotEmpty().hasSize(entity.getStakeholders().size());
	}

	@Test
	void testToStakeholderDto() {
		//Arrange
		var entity = createContractEntity().getStakeholders().getFirst();

		//Act
		var stakeholder = mapper.toStakeholderDto(entity);

		//Assert
		assertThat(stakeholder.getAddress()).isNotNull(); //Is tested in its own method
		assertThat(stakeholder.getEmailAddress()).isEqualTo(entity.getEmailAddress());
		assertThat(stakeholder.getFirstName()).isEqualTo(entity.getFirstName());
		assertThat(stakeholder.getLastName()).isEqualTo(entity.getLastName());
		assertThat(stakeholder.getOrganizationName()).isEqualTo(entity.getOrganizationName());
		assertThat(stakeholder.getOrganizationNumber()).isEqualTo(entity.getOrganizationNumber());
		assertThat(stakeholder.getPartyId()).isEqualTo(entity.getPartyId());
		assertThat(stakeholder.getPhoneNumber()).isEqualTo(entity.getPhoneNumber());
		assertThat(stakeholder.getRoles()).containsAll(entity.getRoles().stream().map(StakeholderRole::name).toList());
		assertThat(stakeholder.getType()).isEqualTo(entity.getType().name());
	}

	@Test
	void testToAddressDto() {
		//Arrange
		var entity = createAddressEntity();

		//Act
		var address = mapper.toAddressDto(entity);

		//Assert
		assertThat(address.getStreetAddress()).isEqualTo(entity.getStreetAddress());
		assertThat(address.getPostalCode()).isEqualTo(entity.getPostalCode());
		assertThat(address.getCountry()).isEqualTo(entity.getCountry());
		assertThat(address.getType()).isEqualTo(entity.getType().name());
		assertThat(address.getAttention()).isEqualTo(entity.getAttention());
		assertThat(address.getTown()).isEqualTo(entity.getTown());
	}

	@Test
	void testToAttachmentDto() {
		//Arrange
		var entity = createAttachmentEntity();

		//Act
		var attachment = mapper.toAttachmentDto(entity);

		//Assert
		assertThat(attachment.getAttachmentData().getContent()).isEqualTo(new String(entity.getContent(), StandardCharsets.UTF_8));
		assertThat(attachment.getMetaData().getCategory()).isEqualTo(entity.getCategory().name());
		assertThat(attachment.getMetaData().getFilename()).isEqualTo(entity.getFilename());
		assertThat(attachment.getMetaData().getId()).isEqualTo(entity.getId());
		assertThat(attachment.getMetaData().getMimeType()).isEqualTo(entity.getMimeType());
		assertThat(attachment.getMetaData().getNote()).isEqualTo(entity.getNote());
	}
}
