package se.sundsvall.contract.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;

public final class ContractMapper {

	private ContractMapper() {}

	static Contract toDto(final ContractEntity contractEntity) {

		final Contract contract;

		if (contractEntity instanceof final LandLeaseContractEntity landLeaseContractEntity) {
			contract = toDto(landLeaseContractEntity);
		} else {
			throw new IllegalArgumentException("Unknown contract type: " + contractEntity.getClass());
		}

		contract.setVersion(contractEntity.getVersion());
		contract.setStatus(contractEntity.getStatus());
		contract.setCaseId(contractEntity.getCaseId());
		contract.setIndexTerms(contractEntity.getIndexTerms());
		contract.setDescription(contractEntity.getDescription());
		contract.setAdditionalTerms(contractEntity.getAdditionalTerms());
		contract.setStakeholders(Optional.ofNullable(contractEntity.getStakeholders())
			.map(stakeholders -> stakeholders.stream()
				.map(ContractMapper::toDto)
				.toList())
			.orElse(null));

		contract.setAttachments(Optional.ofNullable(contractEntity.getAttachments())
			.map(attachments -> attachments.stream()
				.map(ContractMapper::toDto)
				.toList())
			.orElse(null));


		return contract;
	}

	static Contract toDto(final LandLeaseContractEntity landLeaseContractEntity) {
		return LandLeaseContract.builder()
			.withLandLeaseType(landLeaseContractEntity.getLandLeaseType())
			.withLeaseholdType(toDto(landLeaseContractEntity.getLeaseholdType()))
			.withUsufructType(landLeaseContractEntity.getUsufructType())
			.withExternalReferenceId(landLeaseContractEntity.getExternalReferenceId())
			.withPropertyDesignation(landLeaseContractEntity.getPropertyDesignation())
			.withObjectIdentity(landLeaseContractEntity.getObjectIdentity())
			.withLeaseDuration(landLeaseContractEntity.getLeaseDuration())
			.withRental(landLeaseContractEntity.getRental())
			.withInvoiceInterval(landLeaseContractEntity.getInvoiceInterval())
			.withStart(landLeaseContractEntity.getStart())
			.withEnd(landLeaseContractEntity.getEnd())
			.withAutoExtend(landLeaseContractEntity.getAutoExtend())
			.withLeaseExtension(landLeaseContractEntity.getLeaseExtension())
			.withPeriodOfNotice(landLeaseContractEntity.getPeriodOfNotice())
			.withArea(landLeaseContractEntity.getArea())
			.withAreaData(landLeaseContractEntity.getAreaData())
			.withVersion(landLeaseContractEntity.getVersion())
			.withStatus(landLeaseContractEntity.getStatus())
			.withCaseId(landLeaseContractEntity.getCaseId())
			.withIndexTerms(landLeaseContractEntity.getIndexTerms())
			.withDescription(landLeaseContractEntity.getDescription())
			.withAdditionalTerms(landLeaseContractEntity.getAdditionalTerms())
			.withStakeholders(Optional.ofNullable(landLeaseContractEntity.getStakeholders())
				.map(stakeholders -> stakeholders.stream()
					.map(ContractMapper::toDto)
					.toList())
				.orElse(null))
			.withAttachments(Optional.ofNullable(landLeaseContractEntity.getAttachments())
				.map(attachments -> attachments.stream()
					.map(ContractMapper::toDto)
					.toList())
				.orElse(null))
			.build();
	}

	private static Leasehold toDto(final LeaseholdEntity leaseholdEntity) {
		if (isNull(leaseholdEntity)) {
			return null;
		}
		return Leasehold.builder()
			.withType(leaseholdEntity.getType())
			.withDescription(leaseholdEntity.getDescription())
			.build();
	}

	private static Stakeholder toDto(final StakeholderEntity stakeholderEntity) {
		return Stakeholder.builder()
			.withType(stakeholderEntity.getType())
			.withRoles(stakeholderEntity.getRoles())
			.withOrganizationName(stakeholderEntity.getOrganizationName())
			.withOrganizationNumber(stakeholderEntity.getOrganizationNumber())
			.withFirstName(stakeholderEntity.getFirstName())
			.withLastName(stakeholderEntity.getLastName())
			.withPersonId(stakeholderEntity.getPersonId())
			.withPhoneNumber(stakeholderEntity.getPhoneNumber())
			.withEmailAddress(stakeholderEntity.getEmailAddress())
			.withAddress(toDto(stakeholderEntity.getAddress())).build();
	}

	private static Address toDto(final AddressEntity addressEntity) {
		return Address.builder()
			.withStreetAddress(addressEntity.getStreetAddress())
			.withPostalCode(addressEntity.getPostalCode())
			.withCountry(addressEntity.getCountry())
			.withType(addressEntity.getType())
			.withAttention(addressEntity.getAttention())
			.withTown(addressEntity.getTown())
			.build();
	}

	private static Attachment toDto(final AttachmentEntity attachmentEntity) {
		return Attachment.builder()
			.withCategory(attachmentEntity.getCategory())
			.withName(attachmentEntity.getName())
			.withExtension(attachmentEntity.getExtension())
			.withMimeType(attachmentEntity.getMimeType())
			.withNote(attachmentEntity.getNote())
			.withFile(attachmentEntity.getFile())
			.build();
	}

	static ContractEntity toEntity(final Contract contract) {

		final LandLeaseContractEntity contractEntity;

		if (contract instanceof final LandLeaseContract landLeaseContract) {
			contractEntity = toEntity(landLeaseContract);
		} else {
			throw new IllegalArgumentException("Unknown contract type: " + contract.getClass());
		}
		contractEntity.setVersion(contract.getVersion());
		contractEntity.setStatus(contract.getStatus());
		contractEntity.setCaseId(contract.getCaseId());
		contractEntity.setIndexTerms(contract.getIndexTerms());
		contractEntity.setDescription(contract.getDescription());
		contractEntity.setAdditionalTerms(contract.getAdditionalTerms());
		contractEntity.setStakeholders(Optional.ofNullable(contract.getStakeholders())
			.map(stakeholders -> stakeholders.stream()
				.map(ContractMapper::toEntity)
				.toList())
			.orElse(null));


		contractEntity.setAttachments(Optional.ofNullable(contract.getAttachments())
			.map(attachments -> attachments.stream()
				.map(ContractMapper::toEntity)
				.toList())
			.orElse(null));


		return contractEntity;
	}

	private static LandLeaseContractEntity toEntity(final LandLeaseContract landLeaseContract) {

		return LandLeaseContractEntity.builder()
			.withLandLeaseType(landLeaseContract.getLandLeaseType())
			.withLeaseholdType(toEntity(landLeaseContract.getLeaseholdType()))
			.withUsufructType(landLeaseContract.getUsufructType())
			.withExternalReferenceId(landLeaseContract.getExternalReferenceId())
			.withPropertyDesignation(landLeaseContract.getPropertyDesignation())
			.withObjectIdentity(landLeaseContract.getObjectIdentity())
			.withLeaseDuration(landLeaseContract.getLeaseDuration())
			.withRental(landLeaseContract.getRental())
			.withInvoiceInterval(landLeaseContract.getInvoiceInterval())
			.withStart(landLeaseContract.getStart())
			.withEnd(landLeaseContract.getEnd())
			.withAutoExtend(landLeaseContract.getAutoExtend())
			.withLeaseExtension(landLeaseContract.getLeaseExtension())
			.withPeriodOfNotice(landLeaseContract.getPeriodOfNotice())
			.withArea(landLeaseContract.getArea())
			.withAreaData(landLeaseContract.getAreaData())
			.build();
	}

	private static LeaseholdEntity toEntity(final Leasehold leasehold) {
		if (isNull(leasehold)) {
			return null;
		}
		return LeaseholdEntity.builder()
			.withType(leasehold.getType())
			.withDescription(leasehold.getDescription())
			.build();
	}

	private static StakeholderEntity toEntity(final Stakeholder stakeholder) {
		return StakeholderEntity.builder()
			.withType(stakeholder.getType())
			.withRoles(stakeholder.getRoles())
			.withOrganizationName(stakeholder.getOrganizationName())
			.withOrganizationNumber(stakeholder.getOrganizationNumber())
			.withFirstName(stakeholder.getFirstName())
			.withLastName(stakeholder.getLastName())
			.withPersonId(stakeholder.getPersonId())
			.withPhoneNumber(stakeholder.getPhoneNumber())
			.withEmailAddress(stakeholder.getEmailAddress())
			.withAddress(toEntity(stakeholder.getAddress())).build();
	}

	private static AddressEntity toEntity(final Address address) {
		return AddressEntity.builder()
			.withStreetAddress(address.getStreetAddress())
			.withPostalCode(address.getPostalCode())
			.withCountry(address.getCountry())
			.withType(address.getType())
			.withAttention(address.getAttention())
			.withTown(address.getTown())
			.build();
	}

	private static AttachmentEntity toEntity(final Attachment attachment) {
		return AttachmentEntity.builder()
			.withCategory(attachment.getCategory())
			.withName(attachment.getName())
			.withExtension(attachment.getExtension())
			.withMimeType(attachment.getMimeType())
			.withNote(attachment.getNote())
			.withFile(attachment.getFile())
			.build();
	}

	static ContractEntity updateEntity(final ContractEntity entity, final Contract contract) {

		setPropertyIfNonNull(contract.getStakeholders(), entities -> entity.setStakeholders(new ArrayList<>(entities.stream()
			.map(ContractMapper::toEntity)
			.toList())));

		setPropertyIfNonNull(contract.getAttachments(), entities -> entity.setAttachments(new ArrayList<>(entities.stream()
			.map(ContractMapper::toEntity)
			.toList())));

		setPropertyIfNonNull(contract.getIndexTerms(), entity::setIndexTerms);
		setPropertyIfNonNull(contract.getDescription(), entity::setDescription);
		setPropertyIfNonNull(contract.getAdditionalTerms(), entity::setAdditionalTerms);
		setPropertyIfNonNull(contract.getVersion(), entity::setVersion);
		setPropertyIfNonNull(contract.getStatus(), entity::setStatus);
		setPropertyIfNonNull(contract.getCaseId(), entity::setCaseId);

		if (entity instanceof final LandLeaseContractEntity landLeaseContractEntity &&
			contract instanceof final LandLeaseContract landLeaseContract) {
			updateEntity(landLeaseContractEntity, landLeaseContract);
		}
		return entity;
	}

	private static void updateEntity(final LeaseholdEntity entity, final Leasehold leasehold) {
		if (nonNull(leasehold)) {
			setPropertyIfNonNull(leasehold.getType(), entity::setType);
			setPropertyIfNonNull(leasehold.getDescription(), entity::setDescription);
		}
	}

	private static void updateEntity(final LandLeaseContractEntity entity, final LandLeaseContract contract) {

		setPropertyIfNonNull(contract.getStatus(), entity::setStatus);
		setPropertyIfNonNull(contract.getLandLeaseType(), entity::setLandLeaseType);
		setPropertyIfNonNull(contract.getUsufructType(), entity::setUsufructType);
		setPropertyIfNonNull(contract.getExternalReferenceId(), entity::setExternalReferenceId);
		setPropertyIfNonNull(contract.getPropertyDesignation(), entity::setPropertyDesignation);
		setPropertyIfNonNull(contract.getObjectIdentity(), entity::setObjectIdentity);
		setPropertyIfNonNull(contract.getLeaseDuration(), entity::setLeaseDuration);
		setPropertyIfNonNull(contract.getRental(), entity::setRental);
		setPropertyIfNonNull(contract.getInvoiceInterval(), entity::setInvoiceInterval);
		setPropertyIfNonNull(contract.getStart(), entity::setStart);
		setPropertyIfNonNull(contract.getEnd(), entity::setEnd);
		setPropertyIfNonNull(contract.getAutoExtend(), entity::setAutoExtend);
		setPropertyIfNonNull(contract.getLeaseExtension(), entity::setLeaseExtension);
		setPropertyIfNonNull(contract.getPeriodOfNotice(), entity::setPeriodOfNotice);
		setPropertyIfNonNull(contract.getArea(), entity::setArea);
		setPropertyIfNonNull(contract.getAreaData(), entity::setAreaData);

		updateEntity(entity.getLeaseholdType(), contract.getLeaseholdType());
	}

	private static <T> void setPropertyIfNonNull(final T sourceValue, final Consumer<T> setter) {
		if (nonNull(sourceValue)) {
			setter.accept(sourceValue);
		}
	}

}
