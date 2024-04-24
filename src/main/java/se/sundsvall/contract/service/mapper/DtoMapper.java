package se.sundsvall.contract.service.mapper;

import static java.util.Optional.ofNullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.enums.AddressType;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LandLeaseType;
import se.sundsvall.contract.model.enums.LeaseholdType;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.UsufructType;

@Component
public class DtoMapper {

	public Contract toContractDto(final ContractEntity contractEntity, final List<AttachmentEntity> attachmentEntities) {
		return Contract.builder()
			.withAdditionalTerms(contractEntity.getAdditionalTerms())
			.withArea(contractEntity.getArea())
			.withAreaData(contractEntity.getAreaData())
			.withAttachmentMetaData(toAttachmentMetadataDtos(attachmentEntities))
			.withAutoExtend(contractEntity.getAutoExtend())
			.withContractId(contractEntity.getContractId())
			.withDescription(contractEntity.getDescription())
			.withEnd(contractEntity.getEnd())
			.withExternalReferenceId(contractEntity.getExternalReferenceId())
			.withExtraParameters(contractEntity.getExtraParameters())
			.withFees(toFeesDto(contractEntity))
			.withIndexTerms(contractEntity.getIndexTerms())
			.withInvoicing(toInvoicingDto(contractEntity))
			.withLandLeaseType(ofNullable(contractEntity.getLandLeaseType()).map(LandLeaseType::name).orElse(null))
			.withLeaseDuration(contractEntity.getLeaseDuration())
			.withLeaseExtension(contractEntity.getLeaseExtension())
			.withLeasehold(toLeaseholdDto(contractEntity.getLeasehold()))
			.withMunicipalityId(contractEntity.getMunicipalityId())
			.withObjectIdentity(contractEntity.getObjectIdentity())
			.withPeriodOfNotice(contractEntity.getPeriodOfNotice())
			.withPropertyDesignations(contractEntity.getPropertyDesignations())
			.withSignedByWitness(contractEntity.isSignedByWitness())
			.withStakeholders(toStakeholderDtos(contractEntity.getStakeholders()))
			.withStart(contractEntity.getStart())
			.withStatus(ofNullable(contractEntity.getStatus()).map(Status::name).orElse(null))
			.withType(ofNullable(contractEntity.getType()).map(ContractType::name).orElse(null))
			.withUsufructType(ofNullable(contractEntity.getUsufructType()).map(UsufructType::name).orElse(null))
			.withVersion(contractEntity.getVersion())
			.build();
	}

	Fees toFeesDto(ContractEntity contractEntity) {
		return ofNullable(contractEntity.getFees())
			.map(feesEntity -> Fees.builder()
				.withAdditionalInformation(feesEntity.getAdditionalInformation())
				.withCurrency(feesEntity.getCurrency())
				.withIndexNumber(feesEntity.getIndexNumber())
				.withIndexYear(feesEntity.getIndexYear())
				.withMonthly(feesEntity.getMonthly())
				.withTotal(feesEntity.getTotal())
				.withTotalAsText(feesEntity.getTotalAsText())
				.withYearly(feesEntity.getYearly())
				.build())
			.orElse(null);
	}

	Invoicing toInvoicingDto(ContractEntity contractEntity) {
		return ofNullable(contractEntity.getInvoicing())
			.map(invoicing -> Invoicing.builder()
				.withInvoiceInterval(ofNullable(invoicing.getInvoiceInterval()).map(IntervalType::name).orElse(null))
				.withInvoicedIn(ofNullable(invoicing.getInvoicedIn()).map(InvoicedIn::name).orElse(null))
				.build())
			.orElse(null);
	}

	List<AttachmentMetaData> toAttachmentMetadataDtos(List<AttachmentEntity> attachmentEntities) {
		return ofNullable(attachmentEntities)
			.map(attachments -> attachments.stream()
				.map(this::toAttachmentMetaDataDto)
				.toList())
			.orElse(null);
	}

	public AttachmentMetaData toAttachmentMetaDataDto(final AttachmentEntity attachmentEntity) {
		return AttachmentMetaData.builder()
			.withCategory(ofNullable(attachmentEntity.getCategory()).map(AttachmentCategory::name).orElse(null))
			.withFilename(attachmentEntity.getFilename())
			.withId(attachmentEntity.getId())
			.withMimeType(attachmentEntity.getMimeType())
			.withNote(attachmentEntity.getNote())
			.build();
	}

	Leasehold toLeaseholdDto(final LeaseholdEntity leaseholdEntity) {
		return Leasehold.builder()
			.withPurpose(ofNullable(leaseholdEntity.getPurpose())
				.map(LeaseholdType::name)
				.orElse(null))
			.withAdditionalInformation(leaseholdEntity.getAdditionalInformation())
			.withDescription(leaseholdEntity.getDescription())
			.build();
	}

	List<Stakeholder> toStakeholderDtos(final List<StakeholderEntity> stakeholders) {
		return ofNullable(stakeholders)
			.map(stakeholderEntities -> stakeholderEntities.stream()
				.map(this::toStakeholderDto)
				.toList())
			.orElse(null);
	}

	Stakeholder toStakeholderDto(final StakeholderEntity stakeholderEntity) {
		return Stakeholder.builder()
			.withAddress(toAddressDto(stakeholderEntity.getAddress()))
			.withEmailAddress(stakeholderEntity.getEmailAddress())
			.withFirstName(stakeholderEntity.getFirstName())
			.withLastName(stakeholderEntity.getLastName())
			.withOrganizationName(stakeholderEntity.getOrganizationName())
			.withOrganizationNumber(stakeholderEntity.getOrganizationNumber())
			.withPartyId(stakeholderEntity.getPartyId())
			.withPhoneNumber(stakeholderEntity.getPhoneNumber())
			.withRoles(stakeholderEntity.getRoles().stream().filter(Objects::nonNull).map(StakeholderRole::name).toList())
			.withType(ofNullable(stakeholderEntity.getType()).map(StakeholderType::name).orElse(null))
			.build();
	}

	Address toAddressDto(final AddressEntity addressEntity) {
		return Address.builder()
			.withAttention(addressEntity.getAttention())
			.withCountry(addressEntity.getCountry())
			.withPostalCode(addressEntity.getPostalCode())
			.withStreetAddress(addressEntity.getStreetAddress())
			.withTown(addressEntity.getTown())
			.withType(ofNullable(addressEntity.getType()).map(AddressType::name).orElse(null))
			.build();
	}

	public Attachment toAttachmentDto(final AttachmentEntity attachmentEntity) {
		return Attachment.builder()
			.withAttachmentData(AttachmentData.builder()
				.withContent(new String(attachmentEntity.getContent(), StandardCharsets.UTF_8))
				.build())
			.withMetaData(AttachmentMetaData.builder()
				.withCategory(attachmentEntity.getCategory().toString())
				.withFilename(attachmentEntity.getFilename())
				.withId(attachmentEntity.getId())
				.withMimeType(attachmentEntity.getMimeType())
				.withNote(attachmentEntity.getNote())
				.build())
			.build();
	}
}
