package se.sundsvall.contract.service.mapper;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.ObjectUtils.anyNotNull;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_ADDITIONAL;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_INDEX;
import static se.sundsvall.contract.model.enums.ContractType.LEASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.TimeUnit.DAYS;
import static se.sundsvall.contract.service.mapper.StakeholderParameterMapper.toParameterList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Duration;
import se.sundsvall.contract.api.model.Extension;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEmbeddable;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.ExtraParameterGroupEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEmbeddable;
import se.sundsvall.contract.integration.db.model.NoticeEmbeddable;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.integration.db.model.TermEmbeddable;
import se.sundsvall.contract.integration.db.model.TermGroupEntity;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.service.businessrule.model.Action;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;

/**
 * Mapper for converting JPA entity objects to API model (DTO) objects.
 */
public final class DtoMapper {

	private DtoMapper() {}

	/**
	 * Converts a {@link ContractEntity} and its associated attachments to a {@link Contract} DTO.
	 *
	 * @param  contractEntity     the contract entity to convert
	 * @param  attachmentEntities the attachment entities associated with the contract
	 * @return                    the converted contract DTO
	 */
	public static Contract toContractDto(final ContractEntity contractEntity, final List<AttachmentEntity> attachmentEntities) {
		return Contract.builder()
			.withAdditionalTerms(toTermGroupDtos(filterByType(contractEntity.getTermGroups(), TYPE_ADDITIONAL)))
			.withArea(contractEntity.getArea())
			.withAreaData(contractEntity.getAreaData())
			.withAttachmentMetaData(toAttachmentMetadataDtos(attachmentEntities))
			.withContractId(contractEntity.getContractId())
			.withDescription(contractEntity.getDescription())
			.withEnd(contractEntity.getEnd())
			.withExternalReferenceId(contractEntity.getExternalReferenceId())
			.withExtraParameters(toExtraParameterGroupDtos(contractEntity.getExtraParameters()))
			.withFees(toFeesDto(contractEntity))
			.withIndexTerms(toTermGroupDtos(filterByType(contractEntity.getTermGroups(), TYPE_INDEX)))
			.withInvoicing(toInvoicingDto(contractEntity))
			.withLeaseType(contractEntity.getLeaseType())
			.withDuration(toDurationDto(contractEntity))
			.withExtension(toExtensionDto(contractEntity))
			.withLeasehold(toLeaseholdDto(contractEntity.getLeasehold()))
			.withMunicipalityId(contractEntity.getMunicipalityId())
			.withObjectIdentity(contractEntity.getObjectIdentity())
			.withNotices(toNoticeDtos(contractEntity.getNotices()))
			.withPropertyDesignations(toPropertyDesignationsDtos(contractEntity.getPropertyDesignations()))
			.withSignedByWitness(contractEntity.isSignedByWitness())
			.withStakeholders(toStakeholderDtos(contractEntity.getStakeholders()))
			.withStart(contractEntity.getStart())
			.withStatus(contractEntity.getStatus())
			.withType(contractEntity.getType())
			.withVersion(contractEntity.getVersion())
			.build();
	}

	static boolean isLeaseAgreement(ContractEntity contractEntity) {
		return Objects.equals(LEASE_AGREEMENT, contractEntity.getType());
	}

	static Duration toDurationDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity)
			.filter(DtoMapper::isLeaseAgreement)
			.map(entity -> Duration.builder()
				.withLeaseDuration(entity.getLeaseDuration())
				.withUnit(ofNullable(entity.getLeaseDurationUnit()).orElse(DAYS))
				.build())
			.orElse(null);
	}

	static Extension toExtensionDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity)
			.filter(DtoMapper::isLeaseAgreement)
			.map(entity -> Extension.builder()
				.withAutoExtend(entity.getAutoExtend())
				.withLeaseExtension(entity.getLeaseExtension())
				.withUnit(ofNullable(entity.getLeaseExtensionUnit()).orElse(DAYS))
				.build())
			.orElse(null);
	}

	static List<Notice> toNoticeDtos(final List<NoticeEmbeddable> noticeEmbeddableList) {
		return ofNullable(noticeEmbeddableList)
			.map(noticeEmbeddables -> noticeEmbeddables.stream()
				.map(DtoMapper::toNoticeDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static Notice toNoticeDto(final NoticeEmbeddable noticeEmbeddable) {
		return ofNullable(noticeEmbeddable)
			.map(embeddable -> Notice.builder()
				.withPeriodOfNotice(embeddable.getPeriodOfNotice())
				.withParty(embeddable.getParty())
				.withUnit(ofNullable(embeddable.getUnit()).orElse(DAYS))
				.withNoticeDate(embeddable.getNoticeDate())
				.build())
			.orElse(null);
	}

	static Fees toFeesDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity.getFees())
			.map(feesEmbeddable -> Fees.builder()
				.withAdditionalInformation(feesEmbeddable.getAdditionalInformation())
				.withCurrency(feesEmbeddable.getCurrency())
				.withIndexType(feesEmbeddable.getIndexType())
				.withIndexationRate(feesEmbeddable.getIndexationRate())
				.withIndexNumber(feesEmbeddable.getIndexNumber())
				.withIndexYear(feesEmbeddable.getIndexYear())
				.withMonthly(feesEmbeddable.getMonthly())
				.withTotal(feesEmbeddable.getTotal())
				.withTotalAsText(feesEmbeddable.getTotalAsText())
				.withYearly(feesEmbeddable.getYearly())
				.build())
			.orElse(null);
	}

	static Invoicing toInvoicingDto(final ContractEntity contractEntity) {
		return ofNullable(contractEntity.getInvoicing())
			.map(invoicing -> Invoicing.builder()
				.withInvoiceInterval(invoicing.getInvoiceInterval())
				.withInvoicedIn(invoicing.getInvoicedIn())
				.build())
			.orElse(null);
	}

	static List<AttachmentMetadata> toAttachmentMetadataDtos(final List<AttachmentEntity> attachmentEntities) {
		return ofNullable(attachmentEntities)
			.map(attachments -> attachments.stream()
				.map(DtoMapper::toAttachmentMetaDataDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	/**
	 * Converts an {@link AttachmentEntity} to an {@link AttachmentMetadata} DTO.
	 *
	 * @param  attachmentEntity the entity to convert
	 * @return                  the converted metadata DTO, or null if input is null
	 */
	public static AttachmentMetadata toAttachmentMetaDataDto(final AttachmentEntity attachmentEntity) {
		return ofNullable(attachmentEntity)
			.map(attachment -> AttachmentMetadata.builder()
				.withCategory(attachment.getCategory())
				.withFilename(attachment.getFilename())
				.withId(attachment.getId())
				.withMimeType(attachment.getMimeType())
				.withNote(attachment.getNote())
				.build())
			.orElse(null);
	}

	static Leasehold toLeaseholdDto(final LeaseholdEmbeddable leaseholdEntity) {
		return ofNullable(leaseholdEntity)
			.map(leasehold -> Leasehold.builder()
				.withPurpose(leasehold.getPurpose())
				.withAdditionalInformation(leasehold.getAdditionalInformation())
				.withDescription(leasehold.getDescription())
				.build())
			.filter(leasehold -> anyNotNull(leasehold.getDescription(), leasehold.getPurpose()) || isNotEmpty(leasehold.getAdditionalInformation()))
			.orElse(null);
	}

	static List<Stakeholder> toStakeholderDtos(final List<StakeholderEntity> stakeholders) {
		return ofNullable(stakeholders)
			.map(stakeholderEntities -> stakeholderEntities.stream()
				.map(DtoMapper::toStakeholderDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static Stakeholder toStakeholderDto(final StakeholderEntity stakeholderEntity) {
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
				.withRoles(ofNullable(stakeholder.getRoles()).orElse(emptyList()).stream().filter(Objects::nonNull).collect(toCollection(ArrayList::new)))
				.withType(stakeholder.getType())
				.withParameters(toParameterList(stakeholderEntity.getParameters()))
				.build())
			.orElse(null);
	}

	static Address toAddressDto(final AddressEmbeddable addressEntity) {
		return ofNullable(addressEntity)
			.map(address -> Address.builder()
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

	/**
	 * Converts an {@link AttachmentEntity} to an {@link Attachment} DTO, including content data.
	 *
	 * @param  attachmentEntity the entity to convert
	 * @return                  the converted DTO with attachment data and metadata, or null if input is null
	 */
	public static Attachment toAttachmentDto(final AttachmentEntity attachmentEntity) {
		return ofNullable(attachmentEntity)
			.map(attachment -> Attachment.builder()
				.withAttachmentData(AttachmentData.builder()
					.withContent(new String(attachment.getContent(), UTF_8))
					.build())
				.withMetadata(AttachmentMetadata.builder()
					.withCategory(attachment.getCategory())
					.withFilename(attachment.getFilename())
					.withId(attachment.getId())
					.withMimeType(attachment.getMimeType())
					.withNote(attachment.getNote())
					.build())
				.build())
			.orElse(null);
	}

	static List<PropertyDesignation> toPropertyDesignationsDtos(final List<PropertyDesignationEmbeddable> propertyDesignationEmbeddableList) {
		return ofNullable(propertyDesignationEmbeddableList)
			.map(propertyDesignations -> propertyDesignations.stream()
				.map(DtoMapper::toPropertyDesignationDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static PropertyDesignation toPropertyDesignationDto(PropertyDesignationEmbeddable propertyDesignationEmbeddable) {
		return ofNullable(propertyDesignationEmbeddable)
			.map(propertyDesignation -> PropertyDesignation.builder()
				.withName(propertyDesignation.getName())
				.withDistrict(propertyDesignation.getDistrict())
				.build())
			.orElse(null);
	}

	static List<TermGroupEntity> filterByType(final List<TermGroupEntity> termGroups, final String type) {
		return ofNullable(termGroups)
			.map(groups -> groups.stream()
				.filter(group -> type.equals(group.getType()))
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static List<TermGroup> toTermGroupDtos(final List<TermGroupEntity> termGroupEntities) {
		return ofNullable(termGroupEntities)
			.map(groups -> groups.stream()
				.map(DtoMapper::toTermGroupDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static TermGroup toTermGroupDto(final TermGroupEntity termGroupEntity) {
		return ofNullable(termGroupEntity)
			.map(entity -> TermGroup.builder()
				.withHeader(entity.getHeader())
				.withTerms(toTermDtos(entity.getTerms()))
				.build())
			.orElse(null);
	}

	static List<Term> toTermDtos(final List<TermEmbeddable> termEmbeddables) {
		return ofNullable(termEmbeddables)
			.map(terms -> terms.stream()
				.map(DtoMapper::toTermDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static Term toTermDto(final TermEmbeddable termEmbeddable) {
		return ofNullable(termEmbeddable)
			.map(embeddable -> Term.builder()
				.withName(embeddable.getName())
				.withDescription(embeddable.getDescription())
				.build())
			.orElse(null);
	}

	static List<ExtraParameterGroup> toExtraParameterGroupDtos(final List<ExtraParameterGroupEntity> extraParameterGroupEntities) {
		return ofNullable(extraParameterGroupEntities)
			.map(groups -> groups.stream()
				.map(DtoMapper::toExtraParameterGroupDto)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static ExtraParameterGroup toExtraParameterGroupDto(final ExtraParameterGroupEntity extraParameterGroupEntity) {
		return ofNullable(extraParameterGroupEntity)
			.map(entity -> ExtraParameterGroup.builder()
				.withName(entity.getName())
				.withParameters(entity.getParameters())
				.build())
			.orElse(null);
	}

	/**
	 * Creates a {@link BusinessruleParameters} from a {@link ContractEntity} and an {@link Action}.
	 *
	 * @param  contractEntity the contract entity
	 * @param  action         the action to associate with the parameters
	 * @return                the business rule parameters
	 */
	public static BusinessruleParameters toBusinessruleParameters(ContractEntity contractEntity, Action action) {
		return new BusinessruleParameters(contractEntity, action);
	}
}
