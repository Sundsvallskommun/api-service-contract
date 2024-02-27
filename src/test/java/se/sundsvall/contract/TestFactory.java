package se.sundsvall.contract;

import static se.sundsvall.contract.api.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.api.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.AGRICULTURE;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.APARTMENT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.geojson.FeatureCollection;

import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.api.model.enums.AddressType;
import se.sundsvall.contract.api.model.enums.AttachmentCategory;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.StakeholderRole;
import se.sundsvall.contract.api.model.enums.StakeholderType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;

public final class TestFactory {

	public static LandLeaseContractEntity getLandLeaseContractEntity() {
		return LandLeaseContractEntity.builder()
			.withLandLeaseType(LandLeaseType.LEASEHOLD)
			.withLeaseholdType(LeaseholdEntity.builder().withType(APARTMENT).withDescription("someDescription").build())
			.withUsufructType(UsufructType.FISHING)
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignation("somePropertyDesignation")
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(20)
			.withRental(BigDecimal.valueOf(4350))
			.withInvoiceInterval(YEARLY)
			.withStart(LocalDate.now().minusMonths(2))
			.withEnd(LocalDate.now().plusMonths(3))
			.withAutoExtend(true)
			.withLeaseExtension(2)
			.withPeriodOfNotice(2)
			.withArea(123)
			.withAreaData(new FeatureCollection())
			.withVersion(1)
			.withStatus(Status.ACTIVE)
			.withMunicipalityId("1984")
			.withCaseId(1L)
			.withId("2024-12345")
			.withIndexTerms("someIndexTerms")
			.withDescription("someDescription")
			.withAdditionalTerms("someAdditionalTerms")
			.withStakeholders(List.of(StakeholderEntity.builder()
				.withId(1L)
				.withFirstName("someFirstName")
				.withLastName("someLastName")
				.withOrganizationNumber("someOrganizationNumber")
				.withOrganizationName("someOrganizationName")
				.withPersonId("somePersonId")
				.withAddress(AddressEntity.builder()
					.withTown("someTown")
					.withStreetAddress("someStreetAddress")
					.withPostalCode("somePostalCode")
					.withCountry("someCountry")
					.withAttention("someAttention")
					.withType(AddressType.VISITING_ADDRESS)
					.build())
				.withRoles(List.of(StakeholderRole.POWER_OF_ATTORNEY_ROLE))
				.withEmailAddress("someEmailAddress")
				.withPhoneNumber("somePhoneNumber")
				.withType(StakeholderType.ASSOCIATION)
				.build()))
			.withAttachments(List.of(AttachmentEntity.builder()
				.withId(2L)
				.withNote("someNote")
				.withExtension("someExtension")
				.withName("someName")
				.withCategory(AttachmentCategory.CONTRACT)
				.withMimeType("someMimeType")
				.withFile("someFile")
				.build()))
			.withSignedByWitness(true)
			.withExtraParameters(Map.of("someParameter", "someValue"))
			.build();
	}

	public static LandLeaseContract getLandLeaseContract() {
		return LandLeaseContract.builder()
			.withLandLeaseType(LandLeaseType.LEASEHOLD.name())
			.withLeaseholdType(Leasehold.builder().withType(APARTMENT.name()).withDescription("someDescription").build())
			.withUsufructType(UsufructType.FISHING.name())
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignation("somePropertyDesignation")
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(30)
			.withRental(BigDecimal.valueOf(4350))
			.withInvoiceInterval(YEARLY.name())
			.withStart(LocalDate.now().minusMonths(2))
			.withEnd(LocalDate.now().plusMonths(3))
			.withAutoExtend(true)
			.withLeaseExtension(2)
			.withPeriodOfNotice(2)
			.withArea(123)
			.withAreaData(new FeatureCollection())
			.withVersion(1)
			.withStatus(Status.ACTIVE.name())
			.withMunicipalityId("1984")
			.withCaseId(1L)
			.withIndexTerms("someIndexTerms")
			.withDescription("someDescription")
			.withAdditionalTerms("someAdditionalTerms")
			.withStakeholders(List.of(
				Stakeholder.builder()
					.withFirstName("someFirstName")
					.withLastName("someLastName")
					.withOrganizationNumber("someOrganizationNumber")
					.withOrganizationName("someOrganizationName")
					.withPersonId("somePersonId")
					.withAddress(Address.builder()
						.withTown("someTown")
						.withStreetAddress("someStreetAddress")
						.withPostalCode("somePostalCode")
						.withCountry("someCountry")
						.withAttention("someAttention")
						.withType(AddressType.VISITING_ADDRESS.name())
						.build())
					.withRoles(List.of(StakeholderRole.POWER_OF_ATTORNEY_ROLE.name()))
					.withEmailAddress("someEmailAddress")
					.withPhoneNumber("somePhoneNumber")
					.withType(StakeholderType.ASSOCIATION.name())
					.build()))
			.withAttachments(List.of(Attachment.builder()
				.withNote("someNote")
				.withExtension("someExtension")
				.withName("someName")
				.withCategory(AttachmentCategory.CONTRACT.name())
				.withMimeType("someMimeType")
				.withFile("someFile")
				.build()))
			.withSignedByWitness(true)
			.withExtraParameters(Map.of("someParameter", "someValue"))
			.build();
	}

	public static LandLeaseContract getUpdatedLandLeaseContract() {
		return LandLeaseContract.builder()
			.withLandLeaseType(LandLeaseType.USUFRUCT.name())
			.withLeaseholdType(Leasehold.builder().withType(AGRICULTURE.name()).withDescription("someUpdatedDescription").build())
			.withUsufructType(UsufructType.HUNTING.name())
			.withExternalReferenceId("someUpdatedExternalReferenceId")
			.withPropertyDesignation("someUpdatedPropertyDesignation")
			.withObjectIdentity("someUpdatedObjectIdentity")
			.withLeaseDuration(20)
			.withRental(BigDecimal.valueOf(4310))
			.withInvoiceInterval(QUARTERLY.name())
			.withStart(LocalDate.now().minusMonths(3))
			.withEnd(LocalDate.now().plusMonths(5))
			.withAutoExtend(false)
			.withLeaseExtension(3)
			.withPeriodOfNotice(1)
			.withArea(1243)
			.withAreaData(new FeatureCollection())
			.withVersion(2)
			.withStatus(Status.TERMINATED.name())
			.withMunicipalityId("1984")
			.withCaseId(1L)
			.withIndexTerms("someUpdatedIndexTerms")
			.withDescription("someUpdatedDescription")
			.withAdditionalTerms("someUpdatedAdditionalTerms")
			.withStakeholders(List.of(
				Stakeholder.builder()
					.withFirstName("someUpdatedFirstName")
					.withLastName("someUpdatedLastName")
					.withOrganizationNumber("someUpdatedOrganizationNumber")
					.withOrganizationName("someUpdatedOrganizationName")
					.withPersonId("someUpdatedPersonId")
					.withAddress(Address.builder()
						.withTown("someUpdatedTown")
						.withStreetAddress("someUpdatedStreetAddress")
						.withPostalCode("someUpdatedPostalCode")
						.withCountry("someUpdatedCountry")
						.withAttention("someUpdatedAttention")
						.withType(AddressType.POSTAL_ADDRESS.name())
						.build())
					.withRoles(List.of(StakeholderRole.SIGNATORY.name()))
					.withEmailAddress("someUpdatedEmailAddress")
					.withPhoneNumber("someUpdatedPhoneNumber")
					.withType(StakeholderType.COMPANY.name())
					.build()))
			.withAttachments(List.of(Attachment.builder()
				.withNote("someUpdatedNote")
				.withExtension("someUpdatedExtension")
				.withName("someUpdatedName")
				.withCategory(AttachmentCategory.OTHER.name())
				.withMimeType("someUpdatedMimeType")
				.withFile("someUpdatedFile")
				.build()))
			.withSignedByWitness(true)
			.withExtraParameters(Map.of("someParameter", "someValue"))
			.build();
	}
}
