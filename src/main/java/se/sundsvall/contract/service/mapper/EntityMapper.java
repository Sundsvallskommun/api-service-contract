package se.sundsvall.contract.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.openapitools.jackson.nullable.JsonNullable;
import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Extension;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.NoticeTerm;
import se.sundsvall.contract.api.model.PatchContract;
import se.sundsvall.contract.api.model.Period;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_ADDITIONAL;
import static se.sundsvall.contract.integration.db.model.TermGroupEntity.TYPE_INDEX;
import static se.sundsvall.contract.model.enums.TimeUnit.DAYS;
import static se.sundsvall.contract.service.mapper.StakeholderParameterMapper.toStakeholderParameterEntityList;

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
	 * Normalizes a blank (null, empty or whitespace-only) string to {@code null} so that blank values are not
	 * persisted as empty strings.
	 */
	private static String blankToNull(final String value) {
		return (value == null || value.isBlank()) ? null : value;
	}

	/**
	 * Filters out null and blank elements from a list of strings, preserving order. Callers that need to preserve a
	 * {@code null} input (rather than an empty list) should guard the call with {@link java.util.Optional}.
	 */
	private static List<String> filterBlanks(final List<String> values) {
		return values.stream()
			.filter(value -> value != null && !value.isBlank())
			.collect(toCollection(ArrayList::new));
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
			.withCurrentPeriodStartDate(ofNullable(contract.getCurrentPeriod()).map(Period::getStartDate).orElse(null))
			.withCurrentPeriodEndDate(ofNullable(contract.getCurrentPeriod()).map(Period::getEndDate).orElse(null))
			.withEndDate(contract.getEndDate())
			.withExternalReferenceId(blankToNull(contract.getExternalReferenceId()))
			.withExtraParameters(toExtraParameterGroupEntities(contract.getExtraParameters()))
			.withFees(toFeesEmbeddable(contract.getFees()))
			.withInvoicing(toInvoicingEntity(contract.getInvoicing()))
			.withLeaseType(contract.getLeaseType())
			.withLeaseExtension(ofNullable(contract.getExtension()).map(Extension::getLeaseExtension).orElse(null))
			.withLeaseExtensionUnit(ofNullable(contract.getExtension()).map(Extension::getUnit).orElse(DAYS))
			.withLeasehold(toLeaseholdEntity(contract.getLeasehold()))
			.withMunicipalityId(municipalityId)
			.withObjectIdentity(contract.getObjectIdentity())
			.withNoticeDate(ofNullable(contract.getNotice()).map(Notice::getNoticeDate).orElse(null))
			.withNoticeGivenBy(ofNullable(contract.getNotice()).map(Notice::getNoticeGivenBy).orElse(null))
			.withNoticeTerms(toNoticeTermEmbeddables(contract.getNotice()))
			.withPropertyDesignations(toPropertyDesignationEmbeddables(contract.getPropertyDesignations()))
			.withSignedByWitness(contract.isSignedByWitness())
			.withStakeholders(toStakeholderEntities(contract.getStakeholders()))
			.withStartDate(contract.getStartDate())
			.withStatus(contract.getStatus())   // Cannot / shouldn't be null
			.withType(contract.getType()) // Cannot / shouldn't be null
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
				.withParty(object.getParty())
				.withUnit(ofNullable(object.getUnit()).orElse(DAYS))
				.build())
			.orElse(null);
	}

	static InvoicingEmbeddable toInvoicingEntity(final Invoicing invoicing) {
		return ofNullable(invoicing)
			.map(source -> InvoicingEmbeddable.builder()
				.withInvoiceInterval(source.getInvoiceInterval())
				.withInvoicedIn(source.getInvoicedIn())
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
				.withAdditionalInformation(ofNullable(leasehold.getAdditionalInformation()).map(EntityMapper::filterBlanks).orElse(null))
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
	 * Applies the given {@link PatchContract} onto an existing {@link ContractEntity} in place using JSON Merge Patch
	 * semantics: a field that is absent from the payload is left unchanged, a field explicitly set to {@code null} is
	 * cleared, and a field set to a value is updated. The version is not modified.
	 *
	 * @param  entity the existing entity to update
	 * @param  patch  the patch payload
	 * @return        the updated entity
	 */
	public static ContractEntity patchContractEntity(final ContractEntity entity, final PatchContract patch) {
		// signedByWitness is a primitive boolean and cannot be cleared, so an explicit null is ignored
		applyIfPresentNonNull(patch.getSignedByWitness(), entity::setSignedByWitness);

		applyIfPresent(patch.getArea(), entity::setArea);
		applyIfPresent(patch.getAreaData(), entity::setAreaData);
		applyIfPresent(patch.getDescription(), entity::setDescription);
		applyIfPresent(patch.getEndDate(), entity::setEndDate);
		applyIfPresent(patch.getExternalReferenceId(), value -> entity.setExternalReferenceId(blankToNull(value)));
		applyIfPresent(patch.getLeaseType(), entity::setLeaseType);
		applyIfPresent(patch.getObjectIdentity(), entity::setObjectIdentity);
		applyIfPresent(patch.getStartDate(), entity::setStartDate);
		applyIfPresent(patch.getStatus(), entity::setStatus);
		applyIfPresent(patch.getType(), entity::setType);

		// Nested objects: absent keeps, null clears, a value merges/replaces
		applyIfPresent(patch.getExtension(), extension -> {
			if (extension == null) {
				entity.setAutoExtend(null);
				entity.setLeaseExtension(null);
				entity.setLeaseExtensionUnit(null);
			} else {
				setPropertyUnlessNull(extension.getAutoExtend(), entity::setAutoExtend);
				setPropertyUnlessNull(extension.getLeaseExtension(), entity::setLeaseExtension);
				setPropertyUnlessNull(extension.getUnit(), entity::setLeaseExtensionUnit);
			}
		});
		applyIfPresent(patch.getCurrentPeriod(), period -> {
			if (period == null) {
				entity.setCurrentPeriodStartDate(null);
				entity.setCurrentPeriodEndDate(null);
			} else {
				setPropertyUnlessNull(period.getStartDate(), entity::setCurrentPeriodStartDate);
				setPropertyUnlessNull(period.getEndDate(), entity::setCurrentPeriodEndDate);
			}
		});
		applyIfPresent(patch.getNotice(), notice -> {
			if (notice == null) {
				entity.setNoticeDate(null);
				entity.setNoticeGivenBy(null);
				replaceCollection(entity.getNoticeTerms(), entity::setNoticeTerms, new ArrayList<>());
			} else {
				setPropertyUnlessNull(notice.getNoticeDate(), entity::setNoticeDate);
				setPropertyUnlessNull(notice.getNoticeGivenBy(), entity::setNoticeGivenBy);
				setPropertyUnlessNull(notice.getTerms(), _ -> replaceCollection(entity.getNoticeTerms(), entity::setNoticeTerms, toNoticeTermEmbeddables(notice)));
			}
		});

		applyIfPresent(patch.getFees(), fees -> entity.setFees(fees == null ? null : toFeesEmbeddable(fees)));
		applyIfPresent(patch.getInvoicing(), invoicing -> entity.setInvoicing(invoicing == null ? null : toInvoicingEntity(invoicing)));
		applyIfPresent(patch.getLeasehold(), leasehold -> entity.setLeasehold(leasehold == null ? null : toLeaseholdEntity(leasehold)));
		applyIfPresent(patch.getPropertyDesignations(), list -> replaceCollection(entity.getPropertyDesignations(), entity::setPropertyDesignations, toPropertyDesignationEmbeddables(list)));
		applyIfPresent(patch.getStakeholders(), list -> replaceCollection(entity.getStakeholders(), entity::setStakeholders, toStakeholderEntities(list)));
		applyIfPresent(patch.getExtraParameters(), list -> replaceCollection(entity.getExtraParameters(), entity::setExtraParameters, toExtraParameterGroupEntities(list)));

		// Index and additional terms share one table. For each list: absent keeps that type's groups, otherwise the
		// type's groups are replaced (an explicit null clears them).
		if (isPresent(patch.getIndexTerms()) || isPresent(patch.getAdditionalTerms())) {
			final var indexTerms = isPresent(patch.getIndexTerms()) ? ofNullable(patch.getIndexTerms().get()).orElseGet(ArrayList::new) : null;
			final var additionalTerms = isPresent(patch.getAdditionalTerms()) ? ofNullable(patch.getAdditionalTerms().get()).orElseGet(ArrayList::new) : null;
			replaceCollection(entity.getTermGroups(), entity::setTermGroups,
				mergeTermGroups(entity.getTermGroups(), indexTerms, additionalTerms));
		}

		return entity;
	}

	private static <T> void applyIfPresent(final JsonNullable<T> value, final Consumer<T> setter) {
		if (value != null && value.isPresent()) {
			setter.accept(value.get());
		}
	}

	private static <T> void applyIfPresentNonNull(final JsonNullable<T> value, final Consumer<T> setter) {
		if (value != null && value.isPresent() && value.get() != null) {
			setter.accept(value.get());
		}
	}

	private static <T> boolean isPresent(final JsonNullable<T> value) {
		return value != null && value.isPresent();
	}

	private static <T> void replaceCollection(final List<T> existing, final Consumer<List<T>> setter, final List<T> replacement) {
		// Mutate the existing (Hibernate-managed) collection in place to preserve orphanRemoval semantics.
		// Fall back to replacing via setter for immutable/unmodifiable lists (e.g. test fixtures).
		if (existing == null) {
			setter.accept(replacement);
			return;
		}
		try {
			existing.clear();
			existing.addAll(replacement);
		} catch (UnsupportedOperationException e) {
			setter.accept(new ArrayList<>(replacement));
		}
	}

	private static List<TermGroupEntity> mergeTermGroups(final List<TermGroupEntity> existing,
		final List<TermGroup> indexTerms, final List<TermGroup> additionalTerms) {

		final var existingList = ofNullable(existing).orElseGet(ArrayList::new);

		final var indexEntities = indexTerms != null
			? indexTerms.stream().map(t -> toTermGroupEntity(t, TYPE_INDEX)).collect(toCollection(ArrayList::new))
			: existingList.stream().filter(tg -> TYPE_INDEX.equals(tg.getType())).collect(toCollection(ArrayList::new));

		final var additionalEntities = additionalTerms != null
			? additionalTerms.stream().map(t -> toTermGroupEntity(t, TYPE_ADDITIONAL)).collect(toCollection(ArrayList::new))
			: existingList.stream().filter(tg -> TYPE_ADDITIONAL.equals(tg.getType())).collect(toCollection(ArrayList::new));

		return Stream.concat(indexEntities.stream(), additionalEntities.stream()).collect(toCollection(ArrayList::new));
	}

	/**
	 * Maps property designations to embeddables, dropping any element whose name is missing (null) or the empty string so
	 * that no empty rows are persisted. A whitespace-only name is deliberately kept here so that
	 * {@link se.sundsvall.contract.service.ContractValidator} can reject it with a validation error rather than have it
	 * silently dropped.
	 */
	static List<PropertyDesignationEmbeddable> toPropertyDesignationEmbeddables(List<PropertyDesignation> propertyDesignationList) {
		return ofNullable(propertyDesignationList)
			.map(propertyDesignations -> propertyDesignations.stream()
				.filter(Objects::nonNull)
				.filter(propertyDesignation -> hasName(propertyDesignation.getName()))
				.map(EntityMapper::toPropertyDesignationEmbeddable)
				.collect(toCollection(ArrayList::new)))
			.orElse(new ArrayList<>());
	}

	/**
	 * A property-designation name carries data unless it is {@code null} or the empty string. A whitespace-only name is
	 * treated as carrying (invalid) data so that the validation layer can reject it instead of it being silently dropped.
	 */
	private static boolean hasName(final String name) {
		return name != null && !name.isEmpty();
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
				.withIndexType(blankToNull(f.getIndexType()))
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
