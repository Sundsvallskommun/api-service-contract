package se.sundsvall.contract.service.mapper;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_ADDITIONAL;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_INDEX;
import static se.sundsvall.contract.model.enums.TimeUnit.DAYS;
import static se.sundsvall.contract.service.mapper.StakeholderParameterMapper.toStakeholderParameterEntityList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Duration;
import se.sundsvall.contract.api.model.Extension;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.NoticeTerm;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEmbeddable;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.ExtraParameterGroupEntity;
import se.sundsvall.contract.integration.db.model.FeesEmbeddable;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.integration.db.model.LeaseholdEmbeddable;
import se.sundsvall.contract.integration.db.model.NoticeTermEmbeddable;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.integration.db.model.TermEmbeddable;
import se.sundsvall.contract.integration.db.model.TermGroupEntity;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;

/**
 * Mapper for converting API model objects to JPA entity objects.
 */
public final class EntityMapper {

	private EntityMapper() {}

	private static <T> void setPropertyUnlessNull(final T sourceValue, final Consumer<T> setter) {
		if (nonNull(sourceValue)) {
			setter.accept(sourceValue);
		}
	}

	/**
	 * Converts a {@link Contract} to a {@link ContractEntity}.
	 *
	 * @param  municipalityId the municipality ID to set on the entity
	 * @param  contract       the contract to convert
	 * @return                the converted entity
	 */
	public static ContractEntity toContractEntity(final String municipalityId, final Contract contract) {
		return ContractEntity.builder()
			.withTermGroups(toTermGroupEntities(contract.getIndexTerms(), contract.getAdditionalTerms()))
			.withArea(contract.getArea())
			.withAreaData(contract.getAreaData())
			.withAutoExtend(ofNullable(contract.getExtension()).map(Extension::getAutoExtend).orElse(null))
			.withContractId(contract.getContractId())
			.withDescription(contract.getDescription())
			.withEnd(contract.getEndDate())
			.withExternalReferenceId(contract.getExternalReferenceId())
			.withExtraParameters(toExtraParameterGroupEntities(contract.getExtraParameters()))
			.withFees(toFeesEmbeddable(contract.getFees()))
			.withInvoicing(toInvoicingEntity(contract.getInvoicing()))
			.withLeaseType(contract.getLeaseType())
			.withLeaseDuration(ofNullable(contract.getDuration()).map(Duration::getLeaseDuration).orElse(null))
			.withLeaseDurationUnit(ofNullable(contract.getDuration()).map(Duration::getUnit).orElse(DAYS))
			.withLeaseExtension(ofNullable(contract.getExtension()).map(Extension::getLeaseExtension).orElse(null))
			.withLeaseExtensionUnit(ofNullable(contract.getExtension()).map(Extension::getUnit).orElse(DAYS))
			.withLeasehold(toLeaseholdEntity(contract.getLeasehold()))
			.withMunicipalityId(municipalityId)
			.withObjectIdentity(contract.getObjectIdentity())
			.withNoticeTerms(toNoticeTermEmbeddables(contract.getNotice()))
			.withPropertyDesignations(toPropertyDesignationEmbeddables(contract.getPropertyDesignations()))
			.withSignedByWitness(contract.isSignedByWitness())
			.withStakeholders(toStakeholderEntities(contract.getStakeholders()))
			.withStart(contract.getStartDate())
			.withStatus(contract.getStatus())   // Cannot / shouldn't be null
			.withType(contract.getType()) // Cannot / shouldn't be null
			.withVersion(contract.getVersion())
			.build();
	}

	static List<NoticeTermEmbeddable> toNoticeTermEmbeddables(final Notice notice) {
		return ofNullable(notice)
			.map(Notice::getTerms)
			.map(terms -> terms.stream()
				.map(EntityMapper::toNoticeTermEmbeddable)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static NoticeTermEmbeddable toNoticeTermEmbeddable(final NoticeTerm noticeTerm) {
		return ofNullable(noticeTerm)
			.map(object -> NoticeTermEmbeddable.builder()
				.withPeriodOfNotice(object.getPeriodOfNotice())
				.withParty(noticeTerm.getParty())
				.withUnit(ofNullable(noticeTerm.getUnit()).orElse(DAYS))
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
				.withAddress(toAddressEmbeddable(stakeholder.getAddress()))
				.withEmailAddress(stakeholder.getEmailAddress())
				.withFirstName(stakeholder.getFirstName())
				.withLastName(stakeholder.getLastName())
				.withOrganizationName(stakeholder.getOrganizationName())
				.withOrganizationNumber(stakeholder.getOrganizationNumber())
				.withPartyId(stakeholder.getPartyId())
				.withPhoneNumber(stakeholder.getPhoneNumber())
				.withRoles(ofNullable(stakeholder.getRoles()).orElse(emptyList()).stream().filter(Objects::nonNull).collect(toCollection(ArrayList::new)))
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

	static AddressEmbeddable toAddressEmbeddable(final Address fromAddress) {
		return ofNullable(fromAddress)
			.map(address -> AddressEmbeddable.builder()
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
	 * Converts an {@link Attachment} to an {@link AttachmentEntity}.
	 *
	 * @param  municipalityId the municipality ID to set on the entity
	 * @param  contractId     the contract ID to set on the entity
	 * @param  attachment     the attachment to convert
	 * @return                the converted entity
	 */
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

	/**
	 * Updates an existing {@link AttachmentEntity} with non-null values from the given {@link Attachment}.
	 *
	 * @param  entity     the entity to update
	 * @param  attachment the attachment containing updated values
	 * @return            the updated entity
	 */
	public static AttachmentEntity updateAttachmentEntity(final AttachmentEntity entity, final Attachment attachment) {
		ofNullable(attachment.getMetadata()).ifPresent(metadata -> {
			setPropertyUnlessNull(metadata.getCategory(), entity::setCategory);
			setPropertyUnlessNull(metadata.getFilename(), entity::setFilename);
			setPropertyUnlessNull(metadata.getMimeType(), entity::setMimeType);
			setPropertyUnlessNull(metadata.getNote(), entity::setNote);
		});
		ofNullable(attachment.getAttachmentData())
			.map(AttachmentData::getContent)
			.map(content -> content.getBytes(UTF_8))
			.ifPresent(entity::setContent);

		return entity;
	}

	/**
	 * Creates a new {@link ContractEntity} as a new version of an existing contract.
	 *
	 * @param  municipalityId the municipality ID to set on the entity
	 * @param  oldContract    the existing contract entity to carry over version and contract ID from
	 * @param  contract       the contract data for the new version
	 * @return                the new contract entity with preserved version and contract ID
	 */
	public static ContractEntity createNewContractEntity(final String municipalityId, final ContractEntity oldContract, final Contract contract) {
		final var contractEntity = toContractEntity(municipalityId, contract);

		// Set the version, the PrePersist will take care of upping the version by one.
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
			.orElse(new ArrayList<>());
	}

	private static PropertyDesignationEmbeddable toPropertyDesignationEmbeddable(PropertyDesignation fromPropertyDesignation) {
		return ofNullable(fromPropertyDesignation)
			.map(propertyDesignation -> PropertyDesignationEmbeddable.builder()
				.withName(propertyDesignation.getName())
				.withDistrict(propertyDesignation.getDistrict())
				.build())
			.orElse(null);
	}

	static FeesEmbeddable toFeesEmbeddable(final Fees fees) {
		return ofNullable(fees)
			.map(f -> FeesEmbeddable.builder()
				.withCurrency(f.getCurrency())
				.withYearly(f.getYearly())
				.withMonthly(f.getMonthly())
				.withTotal(f.getTotal())
				.withTotalAsText(f.getTotalAsText())
				.withIndexType(f.getIndexType())
				.withIndexYear(f.getIndexYear())
				.withIndexNumber(f.getIndexNumber())
				.withIndexationRate(f.getIndexationRate())
				.withAdditionalInformation(f.getAdditionalInformation())
				.build())
			.orElse(null);
	}

	static List<TermGroupEntity> toTermGroupEntities(final List<TermGroup> indexTerms, final List<TermGroup> additionalTerms) {
		final var indexEntities = ofNullable(indexTerms)
			.map(terms -> terms.stream().map(t -> toTermGroupEntity(t, TYPE_INDEX)))
			.orElse(Stream.empty());
		final var additionalEntities = ofNullable(additionalTerms)
			.map(terms -> terms.stream().map(t -> toTermGroupEntity(t, TYPE_ADDITIONAL)))
			.orElse(Stream.empty());

		return Stream.concat(indexEntities, additionalEntities)
			.collect(toCollection(ArrayList::new));
	}

	static TermGroupEntity toTermGroupEntity(final TermGroup termGroup, final String type) {
		return ofNullable(termGroup)
			.map(tg -> TermGroupEntity.builder()
				.withHeader(tg.getHeader())
				.withType(type)
				.withTerms(toTermEmbeddables(tg.getTerms()))
				.build())
			.orElse(null);
	}

	static List<TermEmbeddable> toTermEmbeddables(final List<Term> terms) {
		return ofNullable(terms)
			.map(t -> t.stream()
				.map(EntityMapper::toTermEmbeddable)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static TermEmbeddable toTermEmbeddable(final Term term) {
		return ofNullable(term)
			.map(t -> TermEmbeddable.builder()
				.withName(t.getName())
				.withDescription(t.getDescription())
				.build())
			.orElse(null);
	}

	static List<ExtraParameterGroupEntity> toExtraParameterGroupEntities(final List<ExtraParameterGroup> extraParameterGroups) {
		return ofNullable(extraParameterGroups)
			.map(groups -> groups.stream()
				.map(EntityMapper::toExtraParameterGroupEntity)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	static ExtraParameterGroupEntity toExtraParameterGroupEntity(final ExtraParameterGroup extraParameterGroup) {
		return ofNullable(extraParameterGroup)
			.map(epg -> ExtraParameterGroupEntity.builder()
				.withName(epg.getName())
				.withParameters(epg.getParameters())
				.build())
			.orElse(null);
	}
}
