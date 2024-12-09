package se.sundsvall.contract;

import static java.nio.charset.StandardCharsets.UTF_8;
import static se.sundsvall.contract.model.enums.AttachmentCategory.CONTRACT;
import static se.sundsvall.contract.model.enums.ContractType.LAND_LEASE;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.model.enums.LandLeaseType.LEASEHOLD;
import static se.sundsvall.contract.model.enums.LeaseholdType.APARTMENT;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.UsufructType.FISHING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.geojson.FeatureCollection;
import se.sundsvall.contract.api.model.Address;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.Leasehold;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEntity;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.AddressType;
import se.sundsvall.contract.model.enums.StakeholderRole;
import se.sundsvall.contract.model.enums.StakeholderType;

public final class TestFactory {

	public static AttachmentEntity createAttachmentEntity() {
		return AttachmentEntity.builder()
			.withId(123L)
			.withContractId("2024-12345")
			.withCategory(CONTRACT)
			.withFilename("mycontract.pdf")
			.withMimeType("application/pdf")
			.withNote("some info goes here")
			.withContent("data".getBytes(UTF_8))
			.build();
	}

	public static ContractEntity createContractEntity() {
		return ContractEntity.builder()
			.withContractId("2024-98765")
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
			.withFees(Fees.builder()
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
				.withPartyId("somePartyId")
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
			.withSignedByWitness(true)
			.withExtraParameters(List.of(
				ExtraParameterGroup.builder()
					.withName("someExtraParameterGroup")
					.withParameters(Map.of("someParameter", "someValue"))
					.build()))
			.withType(LAND_LEASE)
			.build();
	}

	public static Contract createContract() {
		return Contract.builder()
			.withType(LAND_LEASE.name())
			.withVersion(1)
			.withStatus(ACTIVE.name())
			.withMunicipalityId("1984")
			.withContractId("2024-12345")
			.withLandLeaseType(LEASEHOLD.name())
			.withLeasehold(Leasehold.builder().withPurpose(APARTMENT.name()).withDescription("someDescription").build())
			.withUsufructType(FISHING.name())
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignations(List.of("somePropertyDesignation", "someOtherPropertyDesignation"))
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(30)
			.withFees(Fees.builder()
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
					.withPartyId("somePartyId")
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
			.withSignedByWitness(true)
			.withExtraParameters(List.of(
				ExtraParameterGroup.builder()
					.withName("someExtraParameterGroup")
					.withParameters(Map.of("someParameter", "someValue"))
					.build()))
			.build();
	}

	public static Attachment createAttachment() {
		return Attachment.builder()
			.withAttachmentData(AttachmentData.builder()
				.withContent("someContent")
				.build())
			.withMetaData(AttachmentMetaData.builder()
				.withNote("aNote")
				.withCategory("CONTRACT")
				.withMimeType("mimeType")
				.withFilename("file.pdf")
				.build())
			.build();
	}

	public static AddressEntity createAddressEntity() {
		return AddressEntity.builder()
			.withTown("someTown")
			.withStreetAddress("someStreetAddress")
			.withPostalCode("somePostalCode")
			.withCountry("someCountry")
			.withAttention("someAttention")
			.withType(AddressType.VISITING_ADDRESS)
			.build();
	}
}
