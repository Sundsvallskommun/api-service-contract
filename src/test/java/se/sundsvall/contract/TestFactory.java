package se.sundsvall.contract;

import static se.sundsvall.contract.api.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.api.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.AGRICULTURE;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.APARTMENT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
			.withCaseId(1L)
			.withId(1L)
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
				.withRoles(List.of(StakeholderRole.FULLMAKTSROLL))
				.withEmailAddress("someEmailAddress")
				.withPhoneNumber("somePhoneNumber")
				.withType(StakeholderType.ASSOCIATION)
				.build()))
			.withAttachments(List.of(AttachmentEntity.builder()
				.withId(2L)
				.withNote("someNote")
				.withExtension("someExtension")
				.withName("someName")
				.withCategory(AttachmentCategory.KONTRAKT)
				.withMimeType("someMimeType")
				.withFile("someFile")
				.build()))
			.build();
	}

	public static LandLeaseContract getLandLeaseContract() {

		return LandLeaseContract.builder()
			.withLandLeaseType(LandLeaseType.LEASEHOLD)
			.withLeaseholdType(Leasehold.builder().withType(APARTMENT).withDescription("someDescription").build())
			.withUsufructType(UsufructType.FISHING)
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignation("somePropertyDesignation")
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(30)
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
						.withType(AddressType.VISITING_ADDRESS)
						.build())
					.withRoles(List.of(StakeholderRole.FULLMAKTSROLL))
					.withEmailAddress("someEmailAddress")
					.withPhoneNumber("somePhoneNumber")
					.withType(StakeholderType.ASSOCIATION)
					.build()

			))
			.withAttachments(List.of(Attachment.builder()
				.withNote("someNote")
				.withExtension("someExtension")
				.withName("someName")
				.withCategory(AttachmentCategory.KONTRAKT)
				.withMimeType("someMimeType")
				.withFile("someFile")
				.build()))
			.build();
	}

	public static LandLeaseContract getUpdatedLandLeaseContract() {

		return LandLeaseContract.builder()
			.withLandLeaseType(LandLeaseType.USUFRUCT)
			.withLeaseholdType(Leasehold.builder().withType(AGRICULTURE).withDescription("someUpdatedDescription").build())
			.withUsufructType(UsufructType.HUNTING)
			.withExternalReferenceId("someUpdatedExternalReferenceId")
			.withPropertyDesignation("someUpdatedPropertyDesignation")
			.withObjectIdentity("someUpdatedObjectIdentity")
			.withLeaseDuration(20)
			.withRental(BigDecimal.valueOf(4310))
			.withInvoiceInterval(QUARTERLY)
			.withStart(LocalDate.now().minusMonths(3))
			.withEnd(LocalDate.now().plusMonths(5))
			.withAutoExtend(false)
			.withLeaseExtension(3)
			.withPeriodOfNotice(1)
			.withArea(1243)
			.withAreaData(new FeatureCollection())
			.withVersion(2)
			.withStatus(Status.TERMINATED)
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
						.withType(AddressType.POSTAL_ADDRESS)
						.build())
					.withRoles(List.of(StakeholderRole.FIRMATECKNARE))
					.withEmailAddress("someUpdatedEmailAddress")
					.withPhoneNumber("someUpdatedPhoneNumber")
					.withType(StakeholderType.COMPANY)
					.build()

			))
			.withAttachments(List.of(Attachment.builder()
				.withNote("someUpdatedNote")
				.withExtension("someUpdatedExtension")
				.withName("someUpdatedName")
				.withCategory(AttachmentCategory.OTHER)
				.withMimeType("someUpdatedMimeType")
				.withFile("someUpdatedFile")
				.build()))
			.build();
	}

}
