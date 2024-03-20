package se.sundsvall.contract.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.bouncycastle.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.LeaseFees;
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
class ContractMapper {

	Contract toContractDto(final ContractEntity contractEntity, final List<AttachmentEntity> attachmentEntities) {
		Contract contract;

		if (contractEntity instanceof final LandLeaseContractEntity landLeaseContractEntity) {
			contract = toLandLeaseContractDto(landLeaseContractEntity);
		} else {
			throw new IllegalArgumentException("Unknown contract type: " + contractEntity.getClass());
		}

		contract.setContractId(contractEntity.getContractId());
		contract.setVersion(contractEntity.getVersion());
		contract.setType(ofNullable(contractEntity.getType()).map(ContractType::name).orElse(null));
		contract.setStatus(ofNullable(contractEntity.getStatus()).map(Status::name).orElse(null));
		contract.setMunicipalityId(contractEntity.getMunicipalityId());
		contract.setCaseId(contractEntity.getCaseId());
		contract.setIndexTerms(contractEntity.getIndexTerms());
		contract.setDescription(contractEntity.getDescription());
		contract.setAdditionalTerms(contractEntity.getAdditionalTerms());
		contract.setStakeholders(ofNullable(contractEntity.getStakeholders())
			.map(stakeholders -> stakeholders.stream()
				.map(this::toStakeholderDto)
				.toList())
			.orElse(null));
		contract.setSignedByWitness(contractEntity.isSignedByWitness());
		contract.setExtraParameters(contractEntity.getExtraParameters());
		contract.setAttachments(ofNullable(attachmentEntities)
			.map(attachments -> attachments.stream()
				.map(this::toAttachmentDto)
				.toList())
			.orElse(null));

		return contract;
	}

	Contract toLandLeaseContractDto(final LandLeaseContractEntity landLeaseContractEntity) {
		return LandLeaseContract.builder()
			.withLandLeaseType(ofNullable(landLeaseContractEntity.getLandLeaseType()).map(LandLeaseType::name).orElse(null))
			.withLeasehold(toLeaseholdDto(landLeaseContractEntity.getLeasehold()))
			.withUsufructType(ofNullable(landLeaseContractEntity.getUsufructType()).map(UsufructType::name).orElse(null))
			.withExternalReferenceId(landLeaseContractEntity.getExternalReferenceId())
			.withPropertyDesignations(landLeaseContractEntity.getPropertyDesignations())
			.withObjectIdentity(landLeaseContractEntity.getObjectIdentity())
			.withLeaseDuration(landLeaseContractEntity.getLeaseDuration())
			.withLeaseFees(ofNullable(landLeaseContractEntity.getLeaseFees())
				.map(leaseFeesEntity -> LeaseFees.builder()
					.withCurrency(leaseFeesEntity.getCurrency())
					.withYearly(leaseFeesEntity.getYearly())
					.withMonthly(leaseFeesEntity.getMonthly())
					.withTotal(leaseFeesEntity.getTotal())
					.withTotalAsText(leaseFeesEntity.getTotalAsText())
					.withIndexYear(leaseFeesEntity.getIndexYear())
					.withIndexNumber(leaseFeesEntity.getIndexNumber())
					.withAdditionalInformation(leaseFeesEntity.getAdditionalInformation())
					.build())
				.orElse(null))
			.withInvoicing(ofNullable(landLeaseContractEntity.getInvoicing())
				.map(invoicing -> Invoicing.builder()
					.withInvoiceInterval(ofNullable(invoicing.getInvoiceInterval()).map(IntervalType::name).orElse(null))
					.withInvoicedIn(ofNullable(invoicing.getInvoicedIn()).map(InvoicedIn::name).orElse(null))
					.build())
				.orElse(null))
			.withStart(landLeaseContractEntity.getStart())
			.withEnd(landLeaseContractEntity.getEnd())
			.withAutoExtend(landLeaseContractEntity.getAutoExtend())
			.withLeaseExtension(landLeaseContractEntity.getLeaseExtension())
			.withPeriodOfNotice(landLeaseContractEntity.getPeriodOfNotice())
			.withArea(landLeaseContractEntity.getArea())
			.withAreaData(landLeaseContractEntity.getAreaData())
			.build();
	}

	Leasehold toLeaseholdDto(final LeaseholdEntity leaseholdEntity) {
		if (isNull(leaseholdEntity)) {
			return null;
		}
		return Leasehold.builder()
			.withPurpose(ofNullable(leaseholdEntity.getPurpose()).map(LeaseholdType::name).orElse(null))
			.withAdditionalInformation(leaseholdEntity.getAdditionalInformation())
			.withDescription(leaseholdEntity.getDescription())
			.build();
	}

	Stakeholder toStakeholderDto(final StakeholderEntity stakeholderEntity) {
		return Stakeholder.builder()
			.withType(ofNullable(stakeholderEntity.getType()).map(StakeholderType::name).orElse(null))
			.withRoles(stakeholderEntity.getRoles().stream().filter(Objects::nonNull).map(StakeholderRole::name).toList())
			.withOrganizationName(stakeholderEntity.getOrganizationName())
			.withOrganizationNumber(stakeholderEntity.getOrganizationNumber())
			.withFirstName(stakeholderEntity.getFirstName())
			.withLastName(stakeholderEntity.getLastName())
			.withPersonId(stakeholderEntity.getPersonId())
			.withPhoneNumber(stakeholderEntity.getPhoneNumber())
			.withEmailAddress(stakeholderEntity.getEmailAddress())
			.withAddress(toAddressDto(stakeholderEntity.getAddress())).build();
	}

	Address toAddressDto(final AddressEntity addressEntity) {
		return Address.builder()
			.withStreetAddress(addressEntity.getStreetAddress())
			.withPostalCode(addressEntity.getPostalCode())
			.withCountry(addressEntity.getCountry())
			.withType(ofNullable(addressEntity.getType()).map(AddressType::name).orElse(null))
			.withAttention(addressEntity.getAttention())
			.withTown(addressEntity.getTown())
			.build();
	}

	Attachment toAttachmentDto(final AttachmentEntity attachmentEntity) {
		return Attachment.builder()
			.withCategory(ofNullable(attachmentEntity.getCategory()).map(AttachmentCategory::name).orElse(null))
			.withFilename(attachmentEntity.getFilename())
			.withMimeType(attachmentEntity.getMimeType())
			.withNote(attachmentEntity.getNote())
			.build();
	}

	ContractEntity toContractEntity(final String municipalityId, final Contract contract) {
		final LandLeaseContractEntity contractEntity;

		if (contract instanceof final LandLeaseContract landLeaseContract) {
			contractEntity = toLandLeaseContractEntity(landLeaseContract);
		} else {
			throw new IllegalArgumentException("Unknown contract type: " + contract.getClass());
		}
		contractEntity.setContractId(contract.getContractId());
		contractEntity.setVersion(of(contract.getVersion()).orElse(1));
		contractEntity.setType(ofNullable(contract.getType()).map(ContractType::valueOf).orElse(null));
		contractEntity.setStatus(ofNullable(contract.getStatus()).map(Status::valueOf).orElse(null));
		contractEntity.setMunicipalityId(municipalityId);
		contractEntity.setCaseId(contract.getCaseId());
		contractEntity.setIndexTerms(contract.getIndexTerms());
		contractEntity.setDescription(contract.getDescription());
		contractEntity.setAdditionalTerms(contract.getAdditionalTerms());
		contractEntity.setStakeholders(ofNullable(contract.getStakeholders())
			.map(stakeholders -> stakeholders.stream()
				.map(this::toStakeholderEntity)
				.toList())
			.orElse(null));
		contractEntity.setSignedByWitness(contract.isSignedByWitness());
		contractEntity.setExtraParameters(contract.getExtraParameters());

		return contractEntity;
	}

	private LandLeaseContractEntity toLandLeaseContractEntity(final LandLeaseContract landLeaseContract) {
		return LandLeaseContractEntity.builder()
			.withLandLeaseType(ofNullable(landLeaseContract.getLandLeaseType()).map(LandLeaseType::valueOf).orElse(null))
			.withLeasehold(toLeaseholdEntity(landLeaseContract.getLeasehold()))
			.withUsufructType(ofNullable(landLeaseContract.getUsufructType()).map(UsufructType::valueOf).orElse(null))
			.withExternalReferenceId(landLeaseContract.getExternalReferenceId())
			.withPropertyDesignations(landLeaseContract.getPropertyDesignations())
			.withObjectIdentity(landLeaseContract.getObjectIdentity())
			.withLeaseDuration(landLeaseContract.getLeaseDuration())
			.withLeaseFees(landLeaseContract.getLeaseFees())
			.withInvoicing(ofNullable(landLeaseContract.getInvoicing())
				.map(invoicing -> InvoicingEntity.builder()
					.withInvoiceInterval(ofNullable(invoicing.getInvoiceInterval()).map(IntervalType::valueOf).orElse(null))
					.withInvoicedIn(ofNullable(invoicing.getInvoicedIn()).map(InvoicedIn::valueOf).orElse(null))
					.build())
				.orElse(null))
			.withStart(landLeaseContract.getStart())
			.withEnd(landLeaseContract.getEnd())
			.withAutoExtend(landLeaseContract.getAutoExtend())
			.withLeaseExtension(landLeaseContract.getLeaseExtension())
			.withPeriodOfNotice(landLeaseContract.getPeriodOfNotice())
			.withArea(landLeaseContract.getArea())
			.withAreaData(landLeaseContract.getAreaData())
			.build();
	}

	LeaseholdEntity toLeaseholdEntity(final Leasehold leasehold) {
		if (isNull(leasehold)) {
			return null;
		}
		return LeaseholdEntity.builder()
			.withPurpose(ofNullable(leasehold.getPurpose()).map(LeaseholdType::valueOf).orElse(null))
			.withDescription(leasehold.getDescription())
			.withAdditionalInformation(leasehold.getAdditionalInformation())
			.build();
	}

	StakeholderEntity toStakeholderEntity(final Stakeholder stakeholder) {
		return StakeholderEntity.builder()
			.withType(ofNullable(stakeholder.getType()).map(StakeholderType::valueOf).orElse(null))
			.withRoles(stakeholder.getRoles().stream().filter(Objects::nonNull).map(StakeholderRole::valueOf).toList())
			.withOrganizationName(stakeholder.getOrganizationName())
			.withOrganizationNumber(stakeholder.getOrganizationNumber())
			.withFirstName(stakeholder.getFirstName())
			.withLastName(stakeholder.getLastName())
			.withPersonId(stakeholder.getPersonId())
			.withPhoneNumber(stakeholder.getPhoneNumber())
			.withEmailAddress(stakeholder.getEmailAddress())
			.withAddress(toAddressEntity(stakeholder.getAddress())).build();
	}

	AddressEntity toAddressEntity(final Address address) {
		return AddressEntity.builder()
			.withStreetAddress(address.getStreetAddress())
			.withPostalCode(address.getPostalCode())
			.withCountry(address.getCountry())
			.withType(Optional.of(AddressType.valueOf(address.getType())).orElse(null))
			.withAttention(address.getAttention())
			.withTown(address.getTown())
			.build();
	}

	AttachmentEntity toAttachmentEntity(final String contractId, final Attachment attachment) {
		return AttachmentEntity.builder()
			.withContractId(contractId)
			.withCategory(Optional.of(AttachmentCategory.valueOf(attachment.getCategory())).orElse(null))
			.withFilename(attachment.getFilename())
			.withMimeType(attachment.getMimeType())
			.withNote(attachment.getNote())
			.withContent(Base64.getDecoder().decode(attachment.getContent()))
			.build();
	}

	ContractEntity updateContractEntity(final ContractEntity entity, final Contract contract) {
		entity.setVersion(entity.getVersion() + 1);
		setPropertyUnlessNull(contract.getStakeholders(), entities -> entity.setStakeholders(new ArrayList<>(entities.stream()
			.map(this::toStakeholderEntity)
			.toList())));
		setPropertyUnlessNull(contract.getIndexTerms(), entity::setIndexTerms);
		setPropertyUnlessNull(contract.getDescription(), entity::setDescription);
		setPropertyUnlessNull(contract.getAdditionalTerms(), entity::setAdditionalTerms);
		setPropertyUnlessNull(contract.getVersion(), entity::setVersion);
		setPropertyUnlessNull(ofNullable(contract.getStatus()).map(Status::valueOf).orElse(null), entity::setStatus);
		setPropertyUnlessNull(contract.getMunicipalityId(), entity::setMunicipalityId);
		setPropertyUnlessNull(contract.getCaseId(), entity::setCaseId);
		setPropertyUnlessNull(contract.isSignedByWitness(), entity::setSignedByWitness);
		setPropertyUnlessNull(contract.getMunicipalityId(), entity::setMunicipalityId);
		setPropertyUnlessNull(contract.getExtraParameters(), entity::setExtraParameters);

		if (entity instanceof final LandLeaseContractEntity landLeaseContractEntity &&
				contract instanceof final LandLeaseContract landLeaseContract) {
			updateLandLeaseContractEntity(landLeaseContractEntity, landLeaseContract);
		}
		return entity;
	}

	private void updateLeaseholdEntity(final LeaseholdEntity entity, final Leasehold leasehold) {
		if (nonNull(leasehold)) {
			setPropertyUnlessNull(LeaseholdType.valueOf(leasehold.getPurpose()), entity::setPurpose);
			setPropertyUnlessNull(leasehold.getDescription(), entity::setDescription);
		}
	}

	private void updateLandLeaseContractEntity(final LandLeaseContractEntity entity, final LandLeaseContract contract) {
		setPropertyUnlessNull(ofNullable(contract.getStatus()).map(Status::valueOf).orElse(null), entity::setStatus);
		setPropertyUnlessNull(ofNullable(contract.getLandLeaseType()).map(LandLeaseType::valueOf).orElse(null), entity::setLandLeaseType);
		setPropertyUnlessNull(ofNullable(contract.getUsufructType()).map(UsufructType::valueOf).orElse(null), entity::setUsufructType);
		setPropertyUnlessNull(contract.getExternalReferenceId(), entity::setExternalReferenceId);
		setPropertyUnlessNull(contract.getPropertyDesignations(), entity::setPropertyDesignations);
		setPropertyUnlessNull(contract.getObjectIdentity(), entity::setObjectIdentity);
		setPropertyUnlessNull(contract.getLeaseDuration(), entity::setLeaseDuration);
		setPropertyUnlessNull(contract.getLeaseFees(), entity::setLeaseFees);
		setPropertyUnlessNull(contract.getInvoicing(), invoicing -> entity.setInvoicing(InvoicingEntity.builder()
			.withInvoiceInterval(ofNullable(invoicing.getInvoiceInterval()).map(IntervalType::valueOf).orElse(null))
			.withInvoicedIn(ofNullable(invoicing.getInvoicedIn()).map(InvoicedIn::valueOf).orElse(null))
			.build()));
		setPropertyUnlessNull(contract.getStart(), entity::setStart);
		setPropertyUnlessNull(contract.getEnd(), entity::setEnd);
		setPropertyUnlessNull(contract.getAutoExtend(), entity::setAutoExtend);
		setPropertyUnlessNull(contract.getLeaseExtension(), entity::setLeaseExtension);
		setPropertyUnlessNull(contract.getPeriodOfNotice(), entity::setPeriodOfNotice);
		setPropertyUnlessNull(contract.getArea(), entity::setArea);
		setPropertyUnlessNull(contract.getAreaData(), entity::setAreaData);

		if (contract.getLeasehold() != null && !CollectionUtils.isEmpty(contract.getLeasehold().getAdditionalInformation())) {
			setPropertyUnlessNull(contract.getLeasehold().getAdditionalInformation(), entity.getLeasehold()::setAdditionalInformation);
		}

		updateLeaseholdEntity(entity.getLeasehold(), contract.getLeasehold());
	}

	AttachmentEntity updateAttachmentEntity(final AttachmentEntity entity, final Attachment attachment) {
		setPropertyUnlessNull(ofNullable(attachment.getCategory()).map(AttachmentCategory::valueOf).orElse(null), entity::setCategory);
		setPropertyUnlessNull(attachment.getFilename(), entity::setFilename);
		setPropertyUnlessNull(attachment.getMimeType(), entity::setMimeType);
		setPropertyUnlessNull(attachment.getNote(), entity::setNote);
		setPropertyUnlessNull(ofNullable(attachment.getContent())
			.map(Base64.getDecoder()::decode)
			.filter(not(Arrays::isNullOrEmpty))
			.orElse(null), entity::setContent);

		return entity;
	}

	private static <T> void setPropertyUnlessNull(final T sourceValue, final Consumer<T> setter) {
		if (nonNull(sourceValue)) {
			setter.accept(sourceValue);
		}
	}
}
