package se.sundsvall.contract.service.mapper;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static se.sundsvall.contract.service.mapper.StakeholderParameterMapper.toStakeholderParameterEntityList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Duration;
import se.sundsvall.contract.api.model.Extension;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.integration.db.model.LeaseholdEmbeddable;
import se.sundsvall.contract.integration.db.model.NoticeEmbeddable;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.enums.TimeUnit;

public final class EntityMapper {

	private EntityMapper() {}

	private static <T> void setPropertyUnlessNull(final T sourceValue, final Consumer<T> setter) {
		if (nonNull(sourceValue)) {
			setter.accept(sourceValue);
		}
	}

	public static ContractEntity toContractEntity(final String municipalityId, final Contract contract) {
		return ContractEntity.builder()
			.withAdditionalTerms(contract.getAdditionalTerms())
			.withArea(contract.getArea())
			.withAreaData(contract.getAreaData())
			.withAutoExtend(ofNullable(contract.getExtension()).map(Extension::getAutoExtend).orElse(null))
			.withContractId(contract.getContractId())
			.withDescription(contract.getDescription())
			.withEnd(contract.getEnd())
			.withExternalReferenceId(contract.getExternalReferenceId())
			.withExtraParameters(contract.getExtraParameters())
			.withFees(contract.getFees())
			.withIndexTerms(contract.getIndexTerms())
			.withInvoicing(toInvoicingEntity(contract.getInvoicing()))
			.withLeaseType(contract.getLeaseType())
			.withLeaseDuration(ofNullable(contract.getDuration()).map(Duration::getLeaseDuration).orElse(null))
			.withLeaseDurationUnit(ofNullable(contract.getDuration()).map(Duration::getUnit).orElse(TimeUnit.DAYS))
			.withLeaseExtension(ofNullable(contract.getExtension()).map(Extension::getLeaseExtension).orElse(null))
			.withLeaseExtensionUnit(ofNullable(contract.getExtension()).map(Extension::getUnit).orElse(TimeUnit.DAYS))
			.withLeasehold(toLeaseholdEntity(contract.getLeasehold()))
			.withMunicipalityId(municipalityId)
			.withObjectIdentity(contract.getObjectIdentity())
			.withNotices(toNoticeEmbeddables(contract.getNotices()))
			.withPropertyDesignations(toPropertyDesignationEmbeddables(contract.getPropertyDesignations()))
			.withSignedByWitness(contract.isSignedByWitness())
			.withStakeholders(toStakeholderEntities(contract.getStakeholders()))
			.withStart(contract.getStart())
			.withStatus(contract.getStatus())   // Cannot / shouldn't be null
			.withType(contract.getType()) // Cannot / shouldn't be null
			.withVersion(contract.getVersion())
			.build();
	}

	static List<NoticeEmbeddable> toNoticeEmbeddables(final List<Notice> noticeList) {
		return ofNullable(noticeList)
			.map(notices -> notices.stream()
				.map(EntityMapper::toNoticeEmbeddable)
				.collect(toCollection(ArrayList::new)))
			.orElse(null);
	}

	static NoticeEmbeddable toNoticeEmbeddable(final Notice notice) {
		return ofNullable(notice)
			.map(object -> NoticeEmbeddable.builder()
				.withPeriodOfNotice(object.getPeriodOfNotice())
				.withParty(notice.getParty())
				.withUnit(notice.getUnit())
				.build())
			.orElse(null);
	}

	static InvoicingEmbeddable toInvoicingEntity(final Invoicing contract) {
		return ofNullable(contract)
			.map(invoicing -> InvoicingEmbeddable.builder()
				.withInvoiceInterval(invoicing.getInvoiceInterval())
				.withInvoicedIn(invoicing.getInvoicedIn())
				.build())
			.orElse(null);
	}

	static List<StakeholderEntity> toStakeholderEntities(final List<Stakeholder> stakeholderList) {
		return ofNullable(stakeholderList)
			.map(holders -> holders.stream()
				.map(fromStakeholder -> {
					final var stakeholderEntity = toStakeholderEntity(fromStakeholder);
					stakeholderEntity.setParameters(toStakeholderParameterEntityList(fromStakeholder.getParameters(), stakeholderEntity));
					return stakeholderEntity;
				})
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static StakeholderEntity toStakeholderEntity(final Stakeholder fromStakeholder) {
		return ofNullable(fromStakeholder)
			.map(stakeholder -> StakeholderEntity.builder()
				.withAddress(toAddressEntity(stakeholder.getAddress()))
				.withEmailAddress(stakeholder.getEmailAddress())
				.withFirstName(stakeholder.getFirstName())
				.withLastName(stakeholder.getLastName())
				.withOrganizationName(stakeholder.getOrganizationName())
				.withOrganizationNumber(stakeholder.getOrganizationNumber())
				.withPartyId(stakeholder.getPartyId())
				.withPhoneNumber(stakeholder.getPhoneNumber())
				.withRoles(stakeholder.getRoles().stream().filter(Objects::nonNull).collect(toCollection(ArrayList::new)))
				.withType(stakeholder.getType())
				.build())
			.orElse(null);
	}

	static LeaseholdEmbeddable toLeaseholdEntity(final Leasehold fromLeasehold) {
		return ofNullable(fromLeasehold)
			.map(leasehold -> LeaseholdEmbeddable.builder()
				.withAdditionalInformation(leasehold.getAdditionalInformation())
				.withDescription(leasehold.getDescription())
				.withPurpose(leasehold.getPurpose())
				.build())
			.orElse(null);
	}

	static AddressEntity toAddressEntity(final Address fromAddress) {
		return ofNullable(fromAddress)
			.map(address -> AddressEntity.builder()
				.withAttention(address.getAttention())
				.withCountry(address.getCountry())
				.withPostalCode(address.getPostalCode())
				.withStreetAddress(address.getStreetAddress())
				.withCareOf(address.getCareOf())
				.withTown(address.getTown())
				.withType(address.getType())
				.build())
			.orElse(null);
	}

	public static AttachmentEntity toAttachmentEntity(final String municipalityId, final String contractId, final Attachment attachment) {
		return AttachmentEntity.builder()
			.withCategory(attachment.getMetadata().getCategory())
			.withContent(attachment.getAttachmentData().getContent().getBytes(UTF_8))
			.withContractId(contractId)
			.withFilename(attachment.getMetadata().getFilename())
			.withMimeType(attachment.getMetadata().getMimeType())
			.withMunicipalityId(municipalityId)
			.withNote(attachment.getMetadata().getNote())
			.build();
	}

	public static AttachmentEntity updateAttachmentEntity(final AttachmentEntity entity, final Attachment attachment) {
		setPropertyUnlessNull(attachment.getMetadata().getCategory(), entity::setCategory);
		setPropertyUnlessNull(attachment.getMetadata().getFilename(), entity::setFilename);
		setPropertyUnlessNull(attachment.getMetadata().getMimeType(), entity::setMimeType);
		setPropertyUnlessNull(attachment.getMetadata().getNote(), entity::setNote);
		setPropertyUnlessNull(attachment.getAttachmentData().getContent().getBytes(UTF_8), entity::setContent);

		return entity;
	}

	public static ContractEntity createNewContractEntity(final String municipalityId, final ContractEntity oldContract, final Contract contract) {
		final var contractEntity = toContractEntity(municipalityId, contract);

		// Set the version, the PrePersist / PreUpdate will take care of upping the version by one.
		contractEntity.setVersion(oldContract.getVersion());
		// Set the contractId since it will be generated otherwise.
		contractEntity.setContractId(oldContract.getContractId());

		return contractEntity;
	}

	static List<PropertyDesignationEmbeddable> toPropertyDesignationEmbeddables(List<PropertyDesignation> propertyDesignationList) {
		return ofNullable(propertyDesignationList)
			.map(propertyDesignations -> propertyDesignations.stream()
				.map(EntityMapper::toPropertyDesignationEmbeddable)
				.collect(toCollection(ArrayList::new)))
			.orElse(null);
	}

	private static PropertyDesignationEmbeddable toPropertyDesignationEmbeddable(PropertyDesignation fromPropertyDesignation) {
		return ofNullable(fromPropertyDesignation)
			.map(propertyDesignation -> PropertyDesignationEmbeddable.builder()
				.withName(propertyDesignation.getName())
				.withDistrict(propertyDesignation.getDistrict())
				.build())
			.orElse(null);
	}
}
