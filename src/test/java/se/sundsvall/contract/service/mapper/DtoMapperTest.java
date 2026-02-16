package se.sundsvall.contract.service.mapper;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;
import static org.mockito.Mockito.verifyNoInteractions;
import static se.sundsvall.contract.TestFactory.createAddressEmbeddable;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContractEntity;
import static se.sundsvall.contract.TestFactory.createContractEntityBuilder;
import static se.sundsvall.contract.model.enums.ContractType.PURCHASE_AGREEMENT;
import static se.sundsvall.contract.model.enums.LeaseholdType.APARTMENT;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.NoticeTerm;
import se.sundsvall.contract.api.model.Period;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LeaseholdEmbeddable;
import se.sundsvall.contract.integration.db.model.NoticeTermEmbeddable;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;
import se.sundsvall.contract.service.businessrule.model.Action;

class DtoMapperTest {

	@ParameterizedTest
	@EnumSource(value = ContractType.class, names = {
		"LEASE_AGREEMENT"
	}, mode = INCLUDE)
	void isLeaseAgreement(ContractType contractType) {
		final var contractEntity = ContractEntity.builder().withType(contractType).build();

		assertThat(DtoMapper.isLeaseAgreement(contractEntity)).isTrue();
	}

	@ParameterizedTest
	@EnumSource(value = ContractType.class, names = {
		"LEASE_AGREEMENT"
	}, mode = EXCLUDE)
	void isNotLeaseAgreement(ContractType contractType) {
		final var contractEntity = ContractEntity.builder().withType(contractType).build();

		assertThat(DtoMapper.isLeaseAgreement(contractEntity)).isFalse();
	}

	@Test
	void testToContractDtoForLeaseAgreement() {
		// Arrange
		final var contractEntity = createContractEntity();
		final var attachments = List.of(createAttachmentEntity());

		// Act
		final var dto = DtoMapper.toContractDto(contractEntity, attachments);

		// Assert
		assertThat(dto.getAdditionalTerms()).isNotNull(); // Mapped via toTermGroupDtos
		assertThat(dto.getArea()).isEqualTo(contractEntity.getArea());
		assertThat(dto.getAreaData()).isEqualTo(contractEntity.getAreaData());
		assertThat(dto.getContractId()).isEqualTo(contractEntity.getContractId());
		assertThat(dto.getDescription()).isEqualTo(contractEntity.getDescription());
		assertThat(dto.getEndDate()).isEqualTo(contractEntity.getEndDate());
		assertThat(dto.getExternalReferenceId()).isEqualTo(contractEntity.getExternalReferenceId());
		assertThat(dto.getExtraParameters()).isNotNull(); // Mapped via toExtraParameterGroupDtos
		assertThat(dto.getLeaseType()).isEqualTo(contractEntity.getLeaseType());
		assertThat(dto.getMunicipalityId()).isEqualTo(contractEntity.getMunicipalityId());
		assertThat(dto.getObjectIdentity()).isEqualTo(contractEntity.getObjectIdentity());
		assertThat(dto.getIndexTerms()).isNotNull(); // Mapped via toTermGroupDtos
		assertThat(dto.isSignedByWitness()).isEqualTo(contractEntity.isSignedByWitness());
		assertThat(dto.getStakeholders()).isNotNull(); // Is tested in its own method
		assertThat(dto.getStartDate()).isEqualTo(contractEntity.getStartDate());
		assertThat(dto.getStatus()).isEqualTo(contractEntity.getStatus());
		assertThat(dto.getType()).isEqualTo(contractEntity.getType());
		assertThat(dto.getVersion()).isEqualTo(contractEntity.getVersion());
		assertThat(dto.getPropertyDesignations())
			.flatExtracting(PropertyDesignation::getName, PropertyDesignation::getDistrict)
			.containsExactlyElementsOf(
				contractEntity.getPropertyDesignations().stream()
					.flatMap(prop -> Stream.of(prop.getName(), prop.getDistrict()))
					.toList());
		assertThat(dto.getCurrentPeriod()).isNotNull()
			.extracting(Period::getStartDate, Period::getEndDate)
			.containsExactly(contractEntity.getCurrentPeriodStartDate(), contractEntity.getCurrentPeriodEndDate());
		assertThat(dto.getNotice()).isNotNull();
		assertThat(dto.getNotice().getNoticeDate()).isEqualTo(contractEntity.getNoticeDate());
		assertThat(dto.getNotice().getNoticeGivenBy()).isEqualTo(contractEntity.getNoticeGivenBy());
		assertThat(dto).extracting(Contract::getAttachmentMetaData,
			Contract::getFees,
			Contract::getInvoicing,
			Contract::getDuration,
			Contract::getExtension,
			Contract::getLeasehold).isNotNull(); // These attributes are tested in their own test methods
	}

	@Test
	void testToContractDtoForPurchaseAgreement() {
		// Arrange
		final var contractEntity = createContractEntityBuilder().withType(PURCHASE_AGREEMENT).build();

		// Act
		final var dto = DtoMapper.toContractDto(contractEntity, null);

		// Assert
		assertThat(dto.getDuration()).isNull();
		assertThat(dto.getExtension()).isNull();
	}

	@Test
	void testToFeesDto() {
		// Arrange
		final var entity = createContractEntity();

		// Act
		final var fees = DtoMapper.toFeesDto(entity);

		// Assert
		assertThat(fees.getMonthly()).isEqualTo(entity.getFees().getMonthly());
		assertThat(fees.getYearly()).isEqualTo(entity.getFees().getYearly());
		assertThat(fees.getMonthly()).isEqualTo(entity.getFees().getMonthly());
		assertThat(fees.getTotal()).isEqualTo(entity.getFees().getTotal());
		assertThat(fees.getTotalAsText()).isEqualTo(entity.getFees().getTotalAsText());
		assertThat(fees.getCurrency()).isEqualTo(entity.getFees().getCurrency());
		assertThat(fees.getIndexationRate()).isEqualTo(entity.getFees().getIndexationRate());
		assertThat(fees.getIndexYear()).isEqualTo(entity.getFees().getIndexYear());
		assertThat(fees.getIndexNumber()).isEqualTo(entity.getFees().getIndexNumber());
		assertThat(fees.getAdditionalInformation()).isEqualTo(entity.getFees().getAdditionalInformation());
		assertThat(fees.getIndexType()).isEqualTo(entity.getFees().getIndexType());
	}

	@Test
	void testToInvoicingDto() {
		// Arrange
		final var entity = createContractEntity();

		// Act
		final var invoicing = DtoMapper.toInvoicingDto(entity);

		// Assert
		assertThat(invoicing.getInvoicedIn()).isEqualTo(entity.getInvoicing().getInvoicedIn());
		assertThat(invoicing.getInvoiceInterval()).isEqualTo(entity.getInvoicing().getInvoiceInterval());
	}

	@Test
	void testToAttachmentMetaDataDto() {
		// Arrange
		final var attachmentEntity = createAttachmentEntity();

		// Act
		final var metadata = DtoMapper.toAttachmentMetaDataDto(attachmentEntity);

		// Assert
		final var attachmentMetaData = AttachmentMetadata.builder()
			.withId(attachmentEntity.getId())
			.withCategory(attachmentEntity.getCategory())
			.withCreated(attachmentEntity.getCreated())
			.withFilename(attachmentEntity.getFilename())
			.withMimeType(attachmentEntity.getMimeType())
			.withNote(attachmentEntity.getNote())
			.build();

		assertThat(metadata).isEqualTo(attachmentMetaData);
	}

	@Test
	void testToAttachmentMetaDataDtos() {
		// Arrange
		final var attachmentEntities = List.of(createAttachmentEntity(), createAttachmentEntity());

		// Act
		final var metadata = DtoMapper.toAttachmentMetadataDtos(attachmentEntities);

		// Assert
		// We only check that the size is correct, since the actual content is tested in the previous test
		assertThat(metadata).hasSize(2);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("toLeaseholdDtoWhenEntityContainsNoValuesArgumentProvider")
	void testToLeaseholdDtoWhenEntityContainsNoValues(String description, ContractEntity entity) {
		// Act
		final var leasehold = DtoMapper.toLeaseholdDto(entity.getLeasehold());

		// Assert
		assertThat(leasehold).isNull();

	}

	private static Stream<Arguments> toLeaseholdDtoWhenEntityContainsNoValuesArgumentProvider() {
		return Stream.of(
			Arguments.of("All attributes are null", ContractEntity.builder().build()),
			Arguments.of("All attributes are null or empty list", ContractEntity.builder().withTermGroups(emptyList()).build()));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("toLeaseholdDtoWhenEntityContainsValuesArgumentProvider")
	void testToLeaseholdDtoWhenEntityContainsValues(String description, ContractEntity entity) {
		// Act
		final var leasehold = DtoMapper.toLeaseholdDto(entity.getLeasehold());

		// Assert
		assertThat(leasehold).isNotNull();
		assertThat(leasehold.getPurpose()).isEqualTo(entity.getLeasehold().getPurpose());
		assertThat(leasehold.getDescription()).isEqualTo(entity.getLeasehold().getDescription());
		assertThat(leasehold.getAdditionalInformation()).isEqualTo(entity.getLeasehold().getAdditionalInformation());
	}

	private static Stream<Arguments> toLeaseholdDtoWhenEntityContainsValuesArgumentProvider() {
		final var contractEntityBuilder = ContractEntity.builder()
			.withLeasehold(LeaseholdEmbeddable.builder()
				.withPurpose(APARTMENT)
				.withDescription("someDescription")
				.withAdditionalInformation(List.of("info1", "info2"))
				.build());

		return Stream.of(
			Arguments.of("All attributes propagated", contractEntityBuilder.build()),
			Arguments.of("Only description attribute propagated, all other null", contractEntityBuilder.withLeasehold(LeaseholdEmbeddable.builder().withDescription("someDescription").build()).build()),
			Arguments.of("Only description attribute propagated, all other null or empty list", contractEntityBuilder.withLeasehold(LeaseholdEmbeddable.builder().withDescription("someDescription")
				.withAdditionalInformation(emptyList()).build()).build()),
			Arguments.of("Only purpose attribute propagated, all other null", contractEntityBuilder.withLeasehold(LeaseholdEmbeddable.builder().withPurpose(APARTMENT).build()).build()),
			Arguments.of("Only purpose attribute propagated, all other null or empty list", contractEntityBuilder.withLeasehold(LeaseholdEmbeddable.builder().withPurpose(APARTMENT)
				.withAdditionalInformation(emptyList()).build()).build()),
			Arguments.of("Only additional information attribute propagated, all other null", contractEntityBuilder.withLeasehold(LeaseholdEmbeddable.builder().withAdditionalInformation(List.of("info1")).build()).build()));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("toDurationAndExtensionDtoArgumentProvider")
	void testToDurationDto(String description, ContractEntity entity, boolean shouldHaveDuration) {
		// Act
		final var duration = DtoMapper.toDurationDto(entity);

		// Assert
		if (shouldHaveDuration) {
			assertThat(duration).isNotNull();
			assertThat(duration.getLeaseDuration()).isEqualTo(20);
			assertThat(duration.getUnit()).isEqualTo(TimeUnit.MONTHS);
		} else {
			assertThat(duration).isNull();
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("toDurationAndExtensionDtoArgumentProvider")
	void testToExtensionDto(String description, ContractEntity entity, boolean shouldHaveDuration) {
		// Act
		final var extension = DtoMapper.toExtensionDto(entity);

		// Assert
		if (shouldHaveDuration) {
			assertThat(extension).isNotNull();
			assertThat(extension.getAutoExtend()).isTrue();
			assertThat(extension.getLeaseExtension()).isEqualTo(2);
			assertThat(extension.getUnit()).isEqualTo(TimeUnit.MONTHS);
		} else {
			assertThat(extension).isNull();
		}
	}

	private static Stream<Arguments> toDurationAndExtensionDtoArgumentProvider() {
		return Stream.of(
			Arguments.of("Lease agreement contract", createContractEntity(), true),
			Arguments.of("Purchase agreement contract", createContractEntityBuilder().withType(PURCHASE_AGREEMENT).build(), false));

	}

	@Test
	void testToStakeholderDtos() {
		// Arrange
		final var entity = createContractEntity();

		// Act
		final var stakeholders = DtoMapper.toStakeholderDtos(entity.getStakeholders());

		// Assert
		assertThat(stakeholders).isNotNull().hasSize(entity.getStakeholders().size());
		final var firstDto = stakeholders.getFirst();
		final var firstEntity = entity.getStakeholders().getFirst();
		assertThat(firstDto.getFirstName()).isEqualTo(firstEntity.getFirstName());
		assertThat(firstDto.getLastName()).isEqualTo(firstEntity.getLastName());
		assertThat(firstDto.getOrganizationName()).isEqualTo(firstEntity.getOrganizationName());
		assertThat(firstDto.getOrganizationNumber()).isEqualTo(firstEntity.getOrganizationNumber());
		assertThat(firstDto.getPartyId()).isEqualTo(firstEntity.getPartyId());
		assertThat(firstDto.getEmailAddress()).isEqualTo(firstEntity.getEmailAddress());
		assertThat(firstDto.getPhoneNumber()).isEqualTo(firstEntity.getPhoneNumber());
		assertThat(firstDto.getRoles()).containsAll(firstEntity.getRoles());
		assertThat(firstDto.getType()).isEqualTo(firstEntity.getType());
		assertThat(firstDto.getAddress()).isNotNull();
	}

	@Test
	void testToStakeholderDto() {
		// Arrange
		final var entity = createContractEntity().getStakeholders().getFirst();

		// Act
		final var stakeholder = DtoMapper.toStakeholderDto(entity);

		// Assert
		assertThat(stakeholder.getAddress()).isNotNull(); // Is tested in its own method
		assertThat(stakeholder.getEmailAddress()).isEqualTo(entity.getEmailAddress());
		assertThat(stakeholder.getFirstName()).isEqualTo(entity.getFirstName());
		assertThat(stakeholder.getLastName()).isEqualTo(entity.getLastName());
		assertThat(stakeholder.getOrganizationName()).isEqualTo(entity.getOrganizationName());
		assertThat(stakeholder.getOrganizationNumber()).isEqualTo(entity.getOrganizationNumber());
		assertThat(stakeholder.getPartyId()).isEqualTo(entity.getPartyId());
		assertThat(stakeholder.getPhoneNumber()).isEqualTo(entity.getPhoneNumber());
		assertThat(stakeholder.getRoles()).containsAll(entity.getRoles().stream().toList());
		assertThat(stakeholder.getType()).isEqualTo(entity.getType());
	}

	@Test
	void testToAddressDto() {
		// Arrange
		final var entity = createAddressEmbeddable();

		// Act
		final var address = DtoMapper.toAddressDto(entity);

		// Assert
		assertThat(address.getStreetAddress()).isEqualTo(entity.getStreetAddress());
		assertThat(address.getPostalCode()).isEqualTo(entity.getPostalCode());
		assertThat(address.getCareOf()).isEqualTo(entity.getCareOf());
		assertThat(address.getCountry()).isEqualTo(entity.getCountry());
		assertThat(address.getType()).isEqualTo(entity.getType());
		assertThat(address.getAttention()).isEqualTo(entity.getAttention());
		assertThat(address.getTown()).isEqualTo(entity.getTown());
	}

	@Test
	void testToAttachmentDto() {
		// Arrange
		final var entity = createAttachmentEntity();

		// Act
		final var attachment = DtoMapper.toAttachmentDto(entity);

		// Assert
		assertThat(attachment.getAttachmentData().getContent()).isEqualTo(new String(entity.getContent(), StandardCharsets.UTF_8));
		assertThat(attachment.getMetadata().getCategory()).isEqualTo(entity.getCategory());
		assertThat(attachment.getMetadata().getFilename()).isEqualTo(entity.getFilename());
		assertThat(attachment.getMetadata().getId()).isEqualTo(entity.getId());
		assertThat(attachment.getMetadata().getMimeType()).isEqualTo(entity.getMimeType());
		assertThat(attachment.getMetadata().getNote()).isEqualTo(entity.getNote());
		assertThat(attachment.getMetadata().getCreated()).isEqualTo(entity.getCreated());
	}

	@Test
	void testMinimalToContractDto() {
		// Arrange
		final var contract = ContractEntity.builder()
			.withStatus(Status.DRAFT)
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		// Act
		final var dto = DtoMapper.toContractDto(contract, List.of());

		// Assert
		assertThat(dto.getStatus()).isEqualTo(contract.getStatus());
		assertThat(dto.getType()).isEqualTo(contract.getType());
	}

	@Test
	void testToNoticeTermDtos() {
		// Arrange
		final var entity = createContractEntity().getNoticeTerms();

		// Act
		final var noticeTerms = DtoMapper.toNoticeTermDtos(entity);

		// Assert
		assertThat(noticeTerms)
			.hasSize(2)
			.containsExactlyInAnyOrder(
				NoticeTerm.builder()
					.withParty(Party.LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(TimeUnit.MONTHS)
					.build(),
				NoticeTerm.builder()
					.withParty(Party.LESSOR)
					.withPeriodOfNotice(1)
					.withUnit(TimeUnit.MONTHS)
					.build());
	}

	@Test
	void testToNoticeDtoReturnsNullWhenAllFieldsAreNull() {
		// Arrange
		final var entity = ContractEntity.builder().build();

		// Act
		final var notice = DtoMapper.toNoticeDto(entity);

		// Assert
		assertThat(notice).isNull();
	}

	@Test
	void testToNoticeDtoReturnsNullWhenEntityIsNull() {
		// Act
		final var notice = DtoMapper.toNoticeDto(null);

		// Assert
		assertThat(notice).isNull();
	}

	@Test
	void testToNoticeDtoReturnsNullWhenEmptyTermsAndNoDateOrGivenBy() {
		// Arrange
		final var entity = ContractEntity.builder()
			.withNoticeTerms(emptyList())
			.build();

		// Act
		final var notice = DtoMapper.toNoticeDto(entity);

		// Assert
		assertThat(notice).isNull();
	}

	@Test
	void testToNoticeDtoReturnsNoticeWhenOnlyTermsExist() {
		// Arrange
		final var entity = ContractEntity.builder()
			.withNoticeTerms(List.of(
				NoticeTermEmbeddable.builder()
					.withParty(Party.LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(TimeUnit.MONTHS)
					.build()))
			.build();

		// Act
		final var notice = DtoMapper.toNoticeDto(entity);

		// Assert
		assertThat(notice).isNotNull();
		assertThat(notice.getNoticeDate()).isNull();
		assertThat(notice.getNoticeGivenBy()).isNull();
		assertThat(notice.getTerms()).hasSize(1);
	}

	@Test
	void testToNoticeDtoReturnsNoticeWhenOnlyNoticeDateExists() {
		// Arrange
		final var entity = ContractEntity.builder()
			.withNoticeDate(LocalDate.now())
			.build();

		// Act
		final var notice = DtoMapper.toNoticeDto(entity);

		// Assert
		assertThat(notice).isNotNull();
		assertThat(notice.getNoticeDate()).isEqualTo(entity.getNoticeDate());
		assertThat(notice.getTerms()).isEmpty();
	}

	@ParameterizedTest
	@EnumSource(value = Action.class)
	void toBusinessruleParameters(Action action) {
		// Arrange
		final var entityMock = Mockito.mock(ContractEntity.class);

		// Act
		final var bean = DtoMapper.toBusinessruleParameters(entityMock, action);

		// Assert and verify
		assertThat(bean).isNotNull();
		assertThat(bean.action()).isEqualTo(action);
		assertThat(bean.contractEntity()).isSameAs(entityMock);
		verifyNoInteractions(entityMock);
	}
}
