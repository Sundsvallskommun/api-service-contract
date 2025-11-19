package se.sundsvall.contract.service.mapper;

import static java.util.Optional.ofNullable;
import static se.sundsvall.contract.service.mapper.StakeholderParameterMapper.toParameterList;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Duration;
import se.sundsvall.contract.api.model.Extension;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEmbeddable;
import se.sundsvall.contract.integration.db.model.NoticeEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.enums.AddressType;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.LeaseholdType;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;

@Component
public class DtoMapper {

	public Contract toContractDto(final ContractEntity contractEntity, final List<AttachmentEntity> attachmentEntities) {
		return Contract.builder()
			.withAdditionalTerms(contractEntity.getAdditionalTerms())
			.withArea(contractEntity.getArea())
			.withAreaData(contractEntity.getAreaData())
			.withAttachmentMetaData(toAttachmentMetadataDtos(attachmentEntities))
			.withContractId(contractEntity.getContractId())
			.withDescription(contractEntity.getDescription())
			.withEnd(contractEntity.getEnd())
			.withExternalReferenceId(contractEntity.getExternalReferenceId())
			.withExtraParameters(contractEntity.getExtraParameters())
			.withFees(toFeesDto(contractEntity))
			.withIndexTerms(contractEntity.getIndexTerms())
			.withInvoicing(toInvoicingDto(contractEntity))
			.withLeaseType(ofNullable(contractEntity.getLeaseType()).map(LeaseType::name).orElse(null))
			.withDuration(toDurationDto(contractEntity))
			.withExtension(toExtensionDto(contractEntity))
			.withLeasehold(toLeaseholdDto(contractEntity.getLeasehold()))
			.withMunicipalityId(contractEntity.getMunicipalityId())
			.withObjectIdentity(contractEntity.getObjectIdentity())
			.withNotices(toNoticeDtos(contractEntity.getNotices()))
			.withPropertyDesignations(contractEntity.getPropertyDesignations())
			.withSignedByWitness(contractEntity.isSignedByWitness())
			.withStakeholders(toStakeholderDtos(contractEntity.getStakeholders()))
			.withStart(contractEntity.getStart())
			.withStatus(ofNullable(contractEntity.getStatus()).map(Status::name).orElse(null))
			.withType(ofNullable(contractEntity.getType()).map(ContractType::name).orElse(null))
			.withVersion(contractEntity.getVersion())
			.build();
	}

	Duration toDurationDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity)
			.map(entity -> Duration.builder()
				.withLeaseDuration(entity.getLeaseDuration())
				.withUnit(ofNullable(entity.getLeaseDurationUnit()).map(TimeUnit::name).orElse(TimeUnit.DAYS.toString()))
				.build())
			.orElse(null);
	}

	Extension toExtensionDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity)
			.map(entity -> Extension.builder()
				.withAutoExtend(contractEntity.getAutoExtend())
				.withLeaseExtension(contractEntity.getLeaseExtension())
				.withUnit(ofNullable(contractEntity.getLeaseExtensionUnit()).map(TimeUnit::name).orElse(TimeUnit.DAYS.toString()))
				.build())
			.orElse(null);
	}

	List<Notice> toNoticeDtos(final List<NoticeEmbeddable> noticeEmbeddableList) {
		return ofNullable(noticeEmbeddableList)
			.map(noticeEmbeddables -> noticeEmbeddables.stream()
				.map(this::toNoticeDto)
				.toList())
			.orElse(null);
	}

	Notice toNoticeDto(final NoticeEmbeddable noticeEmbeddable) {
		return ofNullable(noticeEmbeddable)
			.map(embeddable -> Notice.builder()
				.withPeriodOfNotice(embeddable.getPeriodOfNotice())
				.withParty(ofNullable(embeddable.getParty()).map(Party::name).orElse(null))
				.withUnit(ofNullable(embeddable.getUnit()).map(TimeUnit::name).orElse(TimeUnit.DAYS.toString()))
				.build())
			.orElse(null);
	}

	Fees toFeesDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity.getFees())
			.map(feesEntity -> Fees.builder()
				.withAdditionalInformation(feesEntity.getAdditionalInformation())
				.withCurrency(feesEntity.getCurrency())
				.withIndexationRate(feesEntity.getIndexationRate())
				.withIndexNumber(feesEntity.getIndexNumber())
				.withIndexYear(feesEntity.getIndexYear())
				.withMonthly(feesEntity.getMonthly())
				.withTotal(feesEntity.getTotal())
				.withTotalAsText(feesEntity.getTotalAsText())
				.withYearly(feesEntity.getYearly())
				.build())
			.orElse(null);
	}

	Invoicing toInvoicingDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity.getInvoicing())
			.map(invoicing -> Invoicing.builder()
				.withInvoiceInterval(ofNullable(invoicing.getInvoiceInterval()).map(IntervalType::name).orElse(null))
				.withInvoicedIn(ofNullable(invoicing.getInvoicedIn()).map(InvoicedIn::name).orElse(null))
				.build())
			.orElse(null);
	}

	List<AttachmentMetaData> toAttachmentMetadataDtos(final List<AttachmentEntity> attachmentEntities) {
		return ofNullable(attachmentEntities)
			.map(attachments -> attachments.stream()
				.map(this::toAttachmentMetaDataDto)
				.toList())
			.orElse(null);
	}

	public AttachmentMetaData toAttachmentMetaDataDto(final AttachmentEntity attachmentEntity) {
		return ofNullable(attachmentEntity)
			.map(attachment -> AttachmentMetaData.builder()
				.withCategory(ofNullable(attachment.getCategory()).map(AttachmentCategory::name).orElse(null))
				.withFilename(attachment.getFilename())
				.withId(attachment.getId())
				.withMimeType(attachment.getMimeType())
				.withNote(attachment.getNote())
				.build())
			.orElse(null);
	}

	Leasehold toLeaseholdDto(final LeaseholdEmbeddable leaseholdEntity) {
		return ofNullable(leaseholdEntity)
			.map(leasehold -> Leasehold.builder()
				.withPurpose(ofNullable(leasehold.getPurpose())
					.map(LeaseholdType::name)
					.orElse(null))
				.withAdditionalInformation(leasehold.getAdditionalInformation())
				.withDescription(leasehold.getDescription())
				.build())
			.orElse(null);
	}

	List<Stakeholder> toStakeholderDtos(final List<StakeholderEntity> stakeholders) {
		return ofNullable(stakeholders)
			.map(stakeholderEntities -> stakeholderEntities.stream()
				.map(this::toStakeholderDto)
				.toList())
			.orElse(null);
	}

	Stakeholder toStakeholderDto(final StakeholderEntity stakeholderEntity) {
		return ofNullable(stakeholderEntity)
			.map(stakeholder -> Stakeholder.builder()
				.withAddress(toAddressDto(stakeholder.getAddress()))
				.withEmailAddress(stakeholder.getEmailAddress())
				.withFirstName(stakeholder.getFirstName())
				.withLastName(stakeholder.getLastName())
				.withOrganizationName(stakeholder.getOrganizationName())
				.withOrganizationNumber(stakeholder.getOrganizationNumber())
				.withPartyId(stakeholder.getPartyId())
				.withPhoneNumber(stakeholder.getPhoneNumber())
				.withRoles(stakeholder.getRoles().stream().filter(Objects::nonNull).map(StakeholderRole::name).toList())
				.withType(ofNullable(stakeholder.getType()).map(StakeholderType::name).orElse(null))
				.withParameters(toParameterList(stakeholderEntity.getParameters()))
				.build())
			.orElse(null);
	}

	Address toAddressDto(final AddressEntity addressEntity) {
		return ofNullable(addressEntity)
			.map(address -> Address.builder()
				.withAttention(address.getAttention())
				.withCountry(address.getCountry())
				.withPostalCode(address.getPostalCode())
				.withStreetAddress(address.getStreetAddress())
				.withCareOf(address.getCareOf())
				.withTown(address.getTown())
				.withType(ofNullable(address.getType()).map(AddressType::name).orElse(null))
				.build())
			.orElse(null);
	}

	public Attachment toAttachmentDto(final AttachmentEntity attachmentEntity) {
		return ofNullable(attachmentEntity)
			.map(attachment -> Attachment.builder()
				.withAttachmentData(AttachmentData.builder()
					.withContent(new String(attachment.getContent(), StandardCharsets.UTF_8))
					.build())
				.withMetaData(AttachmentMetaData.builder()
					.withCategory(attachment.getCategory().toString())
					.withFilename(attachment.getFilename())
					.withId(attachment.getId())
					.withMimeType(attachment.getMimeType())
					.withNote(attachment.getNote())
					.build())
				.build())
			.orElse(null);
	}
}
