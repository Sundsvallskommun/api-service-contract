package se.sundsvall.contract;

import static java.nio.charset.StandardCharsets.UTF_8;
import static se.sundsvall.contract.model.enums.AddressType.VISITING_ADDRESS;
import static se.sundsvall.contract.model.enums.AttachmentCategory.CONTRACT;
import static se.sundsvall.contract.model.enums.ContractType.LEASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ADVANCE;
import static se.sundsvall.contract.model.enums.LeaseType.LEASEHOLD;
import static se.sundsvall.contract.model.enums.LeaseholdType.APARTMENT;
import static se.sundsvall.contract.model.enums.Party.LESSEE;
import static se.sundsvall.contract.model.enums.Party.LESSOR;
import static se.sundsvall.contract.model.enums.StakeholderRole.POWER_OF_ATTORNEY_ROLE;
import static se.sundsvall.contract.model.enums.StakeholderType.ASSOCIATION;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.TimeUnit.DAYS;
import static se.sundsvall.contract.model.enums.TimeUnit.MONTHS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.geojson.FeatureCollection;
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
import se.sundsvall.contract.integration.db.model.AddressEntity;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.integration.db.model.LeaseholdEmbeddable;
import se.sundsvall.contract.integration.db.model.NoticeEmbeddable;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.StakeholderEntity;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;

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
		return createContractEntityBuilder().build();
	}

	public static ContractEntity.ContractEntityBuilder createContractEntityBuilder() {
		return ContractEntity.builder()
			.withContractId("2024-98765")
			.withLeaseType(LEASEHOLD)
			.withLeasehold(LeaseholdEmbeddable.builder()
				.withPurpose(APARTMENT)
				.withDescription("someDescription")
				.withAdditionalInformation(List.of("info1", "info2"))
				.build())
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignations(List.of(
				PropertyDesignationEmbeddable.builder()
					.withName("somePropertyDesignationName")
					.withDistrict("somePropertyDesignationDistrict")
					.build(),
				PropertyDesignationEmbeddable.builder()
					.withName("someOtherPropertyDesignationName")
					.withDistrict("someOtherPropertyDesignationDistrict")
					.build()))
			.withObjectIdentity("someObjectIdentity")
			.withLeaseDuration(20)
			.withLeaseDurationUnit(MONTHS)
			.withFees(Fees.builder()
				.withCurrency("SEK")
				.withYearly(BigDecimal.valueOf(4350))
				.withMonthly(BigDecimal.valueOf(375))
				.withTotal(BigDecimal.valueOf(52200))
				.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
				.withIndexType("KPI 80")
				.withIndexationRate(BigDecimal.valueOf(0.3))
				.withIndexYear(2023)
				.withIndexNumber(2)
				.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
				.build())
			.withInvoicing(InvoicingEmbeddable.builder()
				.withInvoiceInterval(YEARLY)
				.withInvoicedIn(ADVANCE)
				.build())
			.withStart(LocalDate.now().minusMonths(2))
			.withEnd(LocalDate.now().plusMonths(3))
			.withAutoExtend(true)
			.withLeaseExtension(2)
			.withLeaseExtensionUnit(MONTHS)
			.withNotices(List.of(
				NoticeEmbeddable.builder()
					.withParty(LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(MONTHS)
					.build(),
				NoticeEmbeddable.builder()
					.withParty(LESSOR)
					.withPeriodOfNotice(1)
					.withUnit(MONTHS)
					.build()))
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
					.withType(VISITING_ADDRESS)
					.build())
				.withRoles(List.of(POWER_OF_ATTORNEY_ROLE))
				.withEmailAddress("someEmailAddress")
				.withPhoneNumber("somePhoneNumber")
				.withType(ASSOCIATION)
				.build()))
			.withSignedByWitness(true)
			.withExtraParameters(List.of(
				ExtraParameterGroup.builder()
					.withName("someExtraParameterGroup")
					.withParameters(Map.of("someParameter", "someValue"))
					.build()))
			.withType(LEASE_AGREEMENT);
	}

	public static Contract createContract() {
		return Contract.builder()
			.withType(LEASE_AGREEMENT)
			.withVersion(1)
			.withStatus(ACTIVE)
			.withMunicipalityId("1984")
			.withContractId("2024-12345")
			.withLeaseType(LEASEHOLD)
			.withLeasehold(Leasehold.builder().withPurpose(APARTMENT).withDescription("someDescription").build())
			.withExternalReferenceId("someExternalReferenceId")
			.withPropertyDesignations(List.of(
				PropertyDesignation.builder()
					.withName("somePropertyDesignationName")
					.withDistrict("somePropertyDesignationDistrict")
					.build(),
				PropertyDesignation.builder()
					.withName("someOtherPropertyDesignationName")
					.withDistrict("someOtherPropertyDesignationDistrict")
					.build()))
			.withObjectIdentity("someObjectIdentity")
			.withDuration(Duration.builder()
				.withLeaseDuration(30)
				.withUnit(DAYS)
				.build())
			.withExtension(Extension.builder()
				.withAutoExtend(true)
				.withLeaseExtension(2)
				.withUnit(DAYS)
				.build())
			.withFees(Fees.builder()
				.withCurrency("SEK")
				.withYearly(BigDecimal.valueOf(4350))
				.withMonthly(BigDecimal.valueOf(375))
				.withTotal(BigDecimal.valueOf(52200))
				.withTotalAsText("FEMTITVÅTUSENTVÅHUNDRAKRONOR")
				.withIndexType("KPI 80")
				.withIndexYear(2023)
				.withIndexNumber(2)
				.withAdditionalInformation(List.of("additionalInfo1", "additionalInfo2"))
				.build())
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(YEARLY)
				.withInvoicedIn(ADVANCE)
				.build())
			.withStart(LocalDate.now().minusMonths(2))
			.withEnd(LocalDate.now().plusMonths(3))
			.withNotices(List.of(
				Notice.builder()
					.withParty(LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(MONTHS)
					.build(),
				Notice.builder()
					.withParty(LESSOR)
					.withPeriodOfNotice(1)
					.withUnit(MONTHS)
					.build()))
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
						.withType(VISITING_ADDRESS)
						.build())
					.withRoles(List.of(POWER_OF_ATTORNEY_ROLE))
					.withEmailAddress("someEmailAddress")
					.withPhoneNumber("somePhoneNumber")
					.withType(ASSOCIATION)
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
			.withMetadata(AttachmentMetadata.builder()
				.withNote("aNote")
				.withCategory(CONTRACT)
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
			.withType(VISITING_ADDRESS)
			.build();
	}
}
