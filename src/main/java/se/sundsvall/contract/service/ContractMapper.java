package se.sundsvall.contract.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.util.CollectionUtils;

import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.api.model.enums.AddressType;
import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.InvoicedIn;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.LeaseholdType;
import se.sundsvall.contract.api.model.enums.StakeholderRole;
import se.sundsvall.contract.api.model.enums.StakeholderType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.LeaseFees;

public final class ContractMapper {

	private ContractMapper() {}

	static Contract toDto(final ContractEntity contractEntity) {
		final Contract contract;

		if (contractEntity instanceof final LandLeaseContractEntity landLeaseContractEntity) {
			contract = toDto(landLeaseContractEntity);
		} else {
			throw new IllegalArgumentException("Unknown contract type: " + contractEntity.getClass());
		}

		contract.setId(contractEntity.getId());
		contract.setVersion(contractEntity.getVersion());
		contract.setStatus(ofNullable(contractEntity.getStatus()).map(Status::name).orElse(null));
		contract.setMunicipalityId(contractEntity.getMunicipalityId());
		contract.setCaseId(contractEntity.getCaseId());
		contract.setIndexTerms(contractEntity.getIndexTerms());
		contract.setDescription(contractEntity.getDescription());
		contract.setAdditionalTerms(contractEntity.getAdditionalTerms());
		contract.setStakeholders(ofNullable(contractEntity.getStakeholders())
			.map(stakeholders -> stakeholders.stream()
				.map(ContractMapper::toDto)
				.toList())
			.orElse(null));
		contract.setSignedByWitness(contractEntity.isSignedByWitness());
		contract.setExtraParameters(contractEntity.getExtraParameters());

		return contract;
	}

	private static Contract toDto(final LandLeaseContractEntity landLeaseContractEntity) {
		return LandLeaseContract.builder()
			.withLandLeaseType(ofNullable(landLeaseContractEntity.getLandLeaseType()).map(LandLeaseType::name).orElse(null))
			.withLeasehold(toDto(landLeaseContractEntity.getLeasehold()))
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
			.withVersion(landLeaseContractEntity.getVersion())
			.withStatus(ofNullable(landLeaseContractEntity.getStatus()).map(Status::name).orElse(null))
			.withCaseId(landLeaseContractEntity.getCaseId())
			.withIndexTerms(landLeaseContractEntity.getIndexTerms())
			.withDescription(landLeaseContractEntity.getDescription())
			.withAdditionalTerms(landLeaseContractEntity.getAdditionalTerms())
			.withStakeholders(ofNullable(landLeaseContractEntity.getStakeholders())
				.map(stakeholders -> stakeholders.stream()
					.map(ContractMapper::toDto)
					.toList())
				.orElse(null))
			.withSignedByWitness(landLeaseContractEntity.isSignedByWitness())
			.build();
	}

	private static Leasehold toDto(final LeaseholdEntity leaseholdEntity) {
		if (isNull(leaseholdEntity)) {
			return null;
		}
		return Leasehold.builder()
			.withPurpose(ofNullable(leaseholdEntity.getPurpose()).map(LeaseholdType::name).orElse(null))
			.withAdditionalInformation(leaseholdEntity.getAdditionalInformation())
			.withDescription(leaseholdEntity.getDescription())
			.build();
	}

	private static Stakeholder toDto(final StakeholderEntity stakeholderEntity) {
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
			.withAddress(toDto(stakeholderEntity.getAddress())).build();
	}

	private static Address toDto(final AddressEntity addressEntity) {
		return Address.builder()
			.withStreetAddress(addressEntity.getStreetAddress())
			.withPostalCode(addressEntity.getPostalCode())
			.withCountry(addressEntity.getCountry())
			.withType(ofNullable(addressEntity.getType()).map(AddressType::name).orElse(null))
			.withAttention(addressEntity.getAttention())
			.withTown(addressEntity.getTown())
			.build();
	}

	static ContractEntity toEntity(final String municipalityId, final Contract contract) {
		final LandLeaseContractEntity contractEntity;

		if (contract instanceof final LandLeaseContract landLeaseContract) {
			contractEntity = toEntity(landLeaseContract);
		} else {
			throw new IllegalArgumentException("Unknown contract type: " + contract.getClass());
		}
		contractEntity.setVersion(contract.getVersion());
		contractEntity.setStatus(ofNullable(contract.getStatus()).map(Status::valueOf).orElse(null));
		contractEntity.setMunicipalityId(municipalityId);
		contractEntity.setCaseId(contract.getCaseId());
		contractEntity.setIndexTerms(contract.getIndexTerms());
		contractEntity.setDescription(contract.getDescription());
		contractEntity.setAdditionalTerms(contract.getAdditionalTerms());
		contractEntity.setStakeholders(ofNullable(contract.getStakeholders())
			.map(stakeholders -> stakeholders.stream()
				.map(ContractMapper::toEntity)
				.toList())
			.orElse(null));
		contractEntity.setSignedByWitness(contract.isSignedByWitness());
		contractEntity.setExtraParameters(contract.getExtraParameters());

		return contractEntity;
	}

	private static LandLeaseContractEntity toEntity(final LandLeaseContract landLeaseContract) {
		return LandLeaseContractEntity.builder()
			.withLandLeaseType(ofNullable(landLeaseContract.getLandLeaseType()).map(LandLeaseType::valueOf).orElse(null))
			.withLeasehold(toEntity(landLeaseContract.getLeasehold()))
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

	private static LeaseholdEntity toEntity(final Leasehold leasehold) {
		if (isNull(leasehold)) {
			return null;
		}
		return LeaseholdEntity.builder()
			.withPurpose(ofNullable(leasehold.getPurpose()).map(LeaseholdType::valueOf).orElse(null))
			.withDescription(leasehold.getDescription())
			.withAdditionalInformation(leasehold.getAdditionalInformation())
			.build();
	}

	private static StakeholderEntity toEntity(final Stakeholder stakeholder) {
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
			.withAddress(toEntity(stakeholder.getAddress())).build();
	}

	private static AddressEntity toEntity(final Address address) {
		return AddressEntity.builder()
			.withStreetAddress(address.getStreetAddress())
			.withPostalCode(address.getPostalCode())
			.withCountry(address.getCountry())
			.withType(Optional.of(AddressType.valueOf(address.getType())).orElse(null))
			.withAttention(address.getAttention())
			.withTown(address.getTown())
			.build();
	}

	static ContractEntity updateEntity(final ContractEntity entity, final Contract contract) {
		setPropertyIfNonNull(contract.getStakeholders(), entities -> entity.setStakeholders(new ArrayList<>(entities.stream()
			.map(ContractMapper::toEntity)
			.toList())));

		setPropertyIfNonNull(contract.getIndexTerms(), entity::setIndexTerms);
		setPropertyIfNonNull(contract.getDescription(), entity::setDescription);
		setPropertyIfNonNull(contract.getAdditionalTerms(), entity::setAdditionalTerms);
		setPropertyIfNonNull(contract.getVersion(), entity::setVersion);
		setPropertyIfNonNull(ofNullable(contract.getStatus()).map(Status::valueOf).orElse(null), entity::setStatus);
		setPropertyIfNonNull(contract.getMunicipalityId(), entity::setMunicipalityId);
		setPropertyIfNonNull(contract.getCaseId(), entity::setCaseId);
		setPropertyIfNonNull(contract.isSignedByWitness(), entity::setSignedByWitness);
		setPropertyIfNonNull(contract.getMunicipalityId(), entity::setMunicipalityId);
		setPropertyIfNonNull(contract.getExtraParameters(), entity::setExtraParameters);

		if (entity instanceof final LandLeaseContractEntity landLeaseContractEntity &&
			contract instanceof final LandLeaseContract landLeaseContract) {
			updateEntity(landLeaseContractEntity, landLeaseContract);
		}
		return entity;
	}

	private static void updateEntity(final LeaseholdEntity entity, final Leasehold leasehold) {
		if (nonNull(leasehold)) {
			setPropertyIfNonNull(LeaseholdType.valueOf(leasehold.getPurpose()), entity::setPurpose);
			setPropertyIfNonNull(leasehold.getDescription(), entity::setDescription);
		}
	}

	private static void updateEntity(final LandLeaseContractEntity entity, final LandLeaseContract contract) {
		setPropertyIfNonNull(ofNullable(contract.getStatus()).map(Status::valueOf).orElse(null), entity::setStatus);
		setPropertyIfNonNull(ofNullable(contract.getLandLeaseType()).map(LandLeaseType::valueOf).orElse(null), entity::setLandLeaseType);
		setPropertyIfNonNull(ofNullable(contract.getUsufructType()).map(UsufructType::valueOf).orElse(null), entity::setUsufructType);
		setPropertyIfNonNull(contract.getExternalReferenceId(), entity::setExternalReferenceId);
		setPropertyIfNonNull(contract.getPropertyDesignations(), entity::setPropertyDesignations);
		setPropertyIfNonNull(contract.getObjectIdentity(), entity::setObjectIdentity);
		setPropertyIfNonNull(contract.getLeaseDuration(), entity::setLeaseDuration);
		setPropertyIfNonNull(contract.getLeaseFees(), entity::setLeaseFees);
		setPropertyIfNonNull(contract.getInvoicing(), invoicing -> entity.setInvoicing(InvoicingEntity.builder()
			.withInvoiceInterval(ofNullable(invoicing.getInvoiceInterval()).map(IntervalType::valueOf).orElse(null))
			.withInvoicedIn(ofNullable(invoicing.getInvoicedIn()).map(InvoicedIn::valueOf).orElse(null))
			.build()));
		setPropertyIfNonNull(contract.getStart(), entity::setStart);
		setPropertyIfNonNull(contract.getEnd(), entity::setEnd);
		setPropertyIfNonNull(contract.getAutoExtend(), entity::setAutoExtend);
		setPropertyIfNonNull(contract.getLeaseExtension(), entity::setLeaseExtension);
		setPropertyIfNonNull(contract.getPeriodOfNotice(), entity::setPeriodOfNotice);
		setPropertyIfNonNull(contract.getArea(), entity::setArea);
		setPropertyIfNonNull(contract.getAreaData(), entity::setAreaData);

		if(contract.getLeasehold() != null && !CollectionUtils.isEmpty(contract.getLeasehold().getAdditionalInformation())) {
			setPropertyIfNonNull(contract.getLeasehold().getAdditionalInformation(), entity.getLeasehold()::setAdditionalInformation);
		}

		updateEntity(entity.getLeasehold(), contract.getLeasehold());
	}

	private static <T> void setPropertyIfNonNull(final T sourceValue, final Consumer<T> setter) {
		if (nonNull(sourceValue)) {
			setter.accept(sourceValue);
		}
	}
}
