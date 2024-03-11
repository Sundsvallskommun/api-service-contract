package se.sundsvall.contract;

import static se.sundsvall.contract.api.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.api.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.api.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.api.model.enums.LandLeaseType.LEASEHOLD;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.AGRICULTURE;
import static se.sundsvall.contract.api.model.enums.LeaseholdType.APARTMENT;
import static se.sundsvall.contract.api.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.api.model.enums.UsufructType.FISHING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.geojson.FeatureCollection;

import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.Invoicing;
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
import se.sundsvall.contract.integration.db.model.InvoicingEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.LeaseFees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;

public final class TestFactory {

	public static LandLeaseContractEntity getLandLeaseContractEntity() {
		return LandLeaseContractEntity.builder()
			.withId("2024-12345")
			.withLandLeaseType(LEASEHOLD)
			.withLeasehold(LeaseholdEntity.builder()
				.withPurpose(APARTMENT)
				.withDescription("someDescription")
				.withAdditionalInformation(List.of("info1", "info2"))
				.build())
			.withUsufructType(FISHING)
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignations(List.of("somePropertyDesignation", "someOtherPropertyDesignation"))
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(20)
			.withLeaseFees(LeaseFees.builder()
				.withCurrency("SEK")
				.withYearly(BigDecimal.valueOf(4350))
				.withMonthly(BigDecimal.valueOf(375))
				.withTotal(BigDecimal.valueOf(52200))
				.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
				.withIndexYear(2023)
				.withIndexNumber(2)
				.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
				.build())
			.withInvoicing(InvoicingEntity.builder()
				.withInvoiceInterval(YEARLY)
				.withInvoicedIn(ADVANCE)
				.build())
			.withStart(LocalDate.now().minusMonths(2))
			.withEnd(LocalDate.now().plusMonths(3))
			.withAutoExtend(true)
			.withLeaseExtension(2)
			.withPeriodOfNotice(2)
			.withArea(123)
			.withAreaData(new FeatureCollection())
			.withVersion(1)
			.withStatus(ACTIVE)
			.withMunicipalityId("1984")
			.withCaseId(1L)
			.withIndexTerms(List.of(
				TermGroup.builder()
					.withHeader("Some index terms")
					.withTerms(List.of(
						Term.builder()
							.withName("Some index term")
							.withDescription("Some description")
							.build()))
					.build()))
			.withDescription("someDescription")
			.withAdditionalTerms(List.of(
				TermGroup.builder()
					.withHeader("Some additional terms")
					.withTerms(List.of(
						Term.builder()
							.withName("Some additional term")
							.withDescription("Some description")
							.build()))
					.build()))
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
			.withLandLeaseType(LEASEHOLD.name())
			.withLeasehold(Leasehold.builder().withPurpose(APARTMENT.name()).withDescription("someDescription").build())
			.withUsufructType(FISHING.name())
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignations(List.of("somePropertyDesignation", "someOtherPropertyDesignation"))
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(30)
			.withLeaseFees(LeaseFees.builder()
				.withCurrency("SEK")
				.withYearly(BigDecimal.valueOf(4350))
				.withMonthly(BigDecimal.valueOf(375))
				.withTotal(BigDecimal.valueOf(52200))
				.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
				.withIndexYear(2023)
				.withIndexNumber(2)
				.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
				.build())
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(YEARLY.name())
				.withInvoicedIn(ADVANCE.name())
				.build())
			.withStart(LocalDate.now().minusMonths(2))
			.withEnd(LocalDate.now().plusMonths(3))
			.withAutoExtend(true)
			.withLeaseExtension(2)
			.withPeriodOfNotice(2)
			.withArea(123)
			.withAreaData(new FeatureCollection())
			.withVersion(1)
			.withStatus(ACTIVE.name())
			.withMunicipalityId("1984")
			.withCaseId(1L)
			.withIndexTerms(List.of(
				TermGroup.builder()
					.withHeader("Some index terms")
					.withTerms(List.of(
						Term.builder()
							.withName("Some index term")
							.withDescription("Some description")
							.build()))
					.build()))
			.withDescription("someDescription")
			.withAdditionalTerms(List.of(
				TermGroup.builder()
					.withHeader("Some additional terms")
					.withTerms(List.of(
						Term.builder()
							.withName("Some additional term")
							.withDescription("Some description")
							.build()))
					.build()))
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
			.withLeasehold(Leasehold.builder()
				.withAdditionalInformation(List.of("newInfo1", "newInfo2"))
				.withPurpose(AGRICULTURE.name())
				.withDescription("someUpdatedDescription")
				.build())
			.withUsufructType(UsufructType.HUNTING.name())
			.withExternalReferenceId("someUpdatedExternalReferenceId")
			.withPropertyDesignations(List.of("someUpdatedPropertyDesignation", "someOtherUpdatedPropertyDesignation"))
			.withObjectIdentity("someUpdatedObjectIdentity")
			.withLeaseDuration(20)
			.withLeaseFees(LeaseFees.builder()
				.withCurrency("SEK")
				.withYearly(BigDecimal.valueOf(4350))
				.withMonthly(BigDecimal.valueOf(375))
				.withTotal(BigDecimal.valueOf(52200))
				.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
				.withIndexYear(2023)
				.withIndexNumber(2)
				.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
				.build())
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY.name())
				.withInvoicedIn(ADVANCE.name())
				.build())
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
			.withIndexTerms(List.of(
				TermGroup.builder()
					.withHeader("Some index terms")
					.withTerms(List.of(
						Term.builder()
							.withName("Some index term")
							.withDescription("Some description")
							.build()))
					.build()))
			.withDescription("someUpdatedDescription")
			.withAdditionalTerms(List.of(
				TermGroup.builder()
					.withHeader("Some additional terms")
					.withTerms(List.of(
						Term.builder()
							.withName("Some additional term")
							.withDescription("Some description")
							.build()))
					.build()))
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
