package se.sundsvall.contract.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Notice;
import se.sundsvall.contract.api.model.NoticeTerm;
import se.sundsvall.contract.api.model.PatchContract;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.NoticeTermEmbeddable;
import se.sundsvall.contract.integration.db.model.PropertyDesignationEmbeddable;
import se.sundsvall.contract.integration.db.model.TermGroupEntity;
import se.sundsvall.contract.model.Term;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.model.enums.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.TestFactory.createAttachment;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContract;
import static se.sundsvall.contract.TestFactory.createContractEntity;

class EntityMapperTest {

	private static final String MUNICIPALITY_ID = "1984";

	@Test
	void toPropertyDesignationEmbeddables_dropsMissingAndEmptyNamesButKeepsWhitespace() {
		final var input = new ArrayList<PropertyDesignation>();
		input.add(PropertyDesignation.builder().withName("SUNDSVALL BALDER 5:1").withDistrict("Sundsvall").build());
		input.add(PropertyDesignation.builder().withName("").withDistrict("droppedEmpty").build()); // empty name -> dropped
		input.add(PropertyDesignation.builder().withDistrict("droppedNullName").build());           // null name -> dropped
		input.add(PropertyDesignation.builder().withName("   ").build());                            // whitespace -> kept for validator
		input.add(null);                                                                             // null element -> dropped

		final var result = EntityMapper.toPropertyDesignationEmbeddables(input);

		assertThat(result)
			.extracting(PropertyDesignationEmbeddable::getName)
			.containsExactly("SUNDSVALL BALDER 5:1", "   ");
	}

	@Test
	void toPropertyDesignationEmbeddables_nullListYieldsEmptyList() {
		assertThat(EntityMapper.toPropertyDesignationEmbeddables(null)).isEmpty();
	}

	@Test
	void testToContractEntity() {

		// Arrange
		final var dto = createContract();

		// Act
		final var entity = EntityMapper.toContractEntity(MUNICIPALITY_ID, dto);

		// Assert
		assertThat(entity.getTermGroups()).isNotNull(); // Mapped via toTermGroupEntities
		assertThat(entity.getArea()).isEqualTo(dto.getArea());
		assertThat(entity.getAreaData()).isEqualTo(dto.getAreaData());
		assertThat(entity.getContractId()).isEqualTo(dto.getContractId());
		assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
		assertThat(entity.getEndDate()).isEqualTo(dto.getEndDate());
		assertThat(entity.getExternalReferenceId()).isEqualTo(dto.getExternalReferenceId());
		assertThat(entity.getExtraParameters()).isNotNull(); // Mapped via toExtraParameterGroupEntities
		assertThat(entity.getFees()).isNotNull(); // Mapped via toFeesEmbeddable
		assertThat(entity.getInvoicing()).isNotNull();  // Is tested in its own method
		assertThat(entity.getLeaseType()).isEqualTo(dto.getLeaseType());
		assertThat(entity.getLeaseExtension()).isEqualTo(dto.getExtension().getLeaseExtension());
		assertThat(entity.getLeaseExtensionUnit()).isEqualTo(dto.getExtension().getUnit());
		assertThat(entity.getAutoExtend()).isEqualTo(dto.getExtension().getAutoExtend());
		assertThat(entity.getLeasehold()).isNotNull(); // Is tested in its own method
		assertThat(entity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entity.getObjectIdentity()).isEqualTo(dto.getObjectIdentity());
		assertThat(entity.getCurrentPeriodStartDate()).isEqualTo(dto.getCurrentPeriod().getStartDate());
		assertThat(entity.getCurrentPeriodEndDate()).isEqualTo(dto.getCurrentPeriod().getEndDate());
		assertThat(entity.getNoticeDate()).isEqualTo(dto.getNotice().getNoticeDate());
		assertThat(entity.getNoticeGivenBy()).isEqualTo(dto.getNotice().getNoticeGivenBy());
		assertThat(entity.getNoticeTerms()).isNotNull(); // Is tested in its own method
		assertThat(entity.getPropertyDesignations())
			.flatExtracting(PropertyDesignationEmbeddable::getName, PropertyDesignationEmbeddable::getDistrict)
			.containsAnyElementsOf(dto.getPropertyDesignations().stream()
				.flatMap(prop -> Stream.of(prop.getName(), prop.getDistrict()))
				.toList());
		assertThat(entity.isSignedByWitness()).isEqualTo(dto.isSignedByWitness());
		assertThat(entity.getStakeholders()).isNotNull(); // Is tested in its own method
		assertThat(entity.getStartDate()).isEqualTo(dto.getStartDate());
		assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
		assertThat(entity.getType()).isEqualTo(dto.getType());
		// Note: version is not mapped here; it is explicitly managed by the service layer
		// (createContract sets it to 1, createNewContractEntity bumps it).
	}

	@Test
	void testToInvoicingEntity() {

		// Arrange
		final var dto = createContract();

		// Act
		final var entity = EntityMapper.toInvoicingEntity(dto.getInvoicing());

		// Assert
		assertThat(entity.getInvoicedIn()).isEqualTo(dto.getInvoicing().getInvoicedIn());
		assertThat(entity.getInvoiceInterval()).isEqualTo(dto.getInvoicing().getInvoiceInterval());
	}

	@Test
	void testToStakeholderEntities() {

		// Arrange
		final var dto = createContract();

		// Act
		final var entities = EntityMapper.toStakeholderEntities(dto.getStakeholders());

		// Assert
		assertThat(entities).isNotNull().hasSize(dto.getStakeholders().size());
		final var firstEntity = entities.getFirst();
		final var firstDto = dto.getStakeholders().getFirst();
		assertThat(firstEntity.getFirstName()).isEqualTo(firstDto.getFirstName());
		assertThat(firstEntity.getLastName()).isEqualTo(firstDto.getLastName());
		assertThat(firstEntity.getOrganizationName()).isEqualTo(firstDto.getOrganizationName());
		assertThat(firstEntity.getOrganizationNumber()).isEqualTo(firstDto.getOrganizationNumber());
		assertThat(firstEntity.getPartyId()).isEqualTo(firstDto.getPartyId());
		assertThat(firstEntity.getEmailAddress()).isEqualTo(firstDto.getEmailAddress());
		assertThat(firstEntity.getPhoneNumber()).isEqualTo(firstDto.getPhoneNumber());
		assertThat(firstEntity.getRoles()).containsAll(firstDto.getRoles());
		assertThat(firstEntity.getType()).isEqualTo(firstDto.getType());
		assertThat(firstEntity.getAddress()).isNotNull();
	}

	@Test
	void testToStakeholderEntity() {

		// Arrange
		final var dto = createContract().getStakeholders().getFirst();

		// Act
		final var entity = EntityMapper.toStakeholderEntity(dto);

		// Assert
		assertThat(entity.getAddress()).isNotNull();    // Mapping is tested in another method
		assertThat(entity.getEmailAddress()).isEqualTo(dto.getEmailAddress());
		assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
		assertThat(entity.getLastName()).isEqualTo(dto.getLastName());
		assertThat(entity.getOrganizationName()).isEqualTo(dto.getOrganizationName());
		assertThat(entity.getOrganizationNumber()).isEqualTo(dto.getOrganizationNumber());
		assertThat(entity.getPartyId()).isEqualTo(dto.getPartyId());
		assertThat(entity.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
		// Assert that all roles are mapped
		var array = dto.getRoles().stream().toList();
		assertThat(entity.getRoles()).containsAll(array);
	}

	@Test
	void testToLeaseholdEntity() {

		// Arrange
		final var dto = createContract().getLeasehold();

		// Act
		final var entity = EntityMapper.toLeaseholdEntity(dto);

		// Assert
		assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
		assertThat(entity.getAdditionalInformation()).isEqualTo(dto.getAdditionalInformation());
		assertThat(entity.getPurpose()).isEqualTo(dto.getPurpose());
	}

	@Test
	void testToContractEntityNormalizesBlankExternalReferenceIdToNull() {
		final var dto = createContract();
		dto.setExternalReferenceId("   ");

		final var entity = EntityMapper.toContractEntity(MUNICIPALITY_ID, dto);

		assertThat(entity.getExternalReferenceId()).isNull();
	}

	@Test
	void testToFeesEmbeddableNormalizesBlankIndexTypeToNull() {
		final var fees = createContract().getFees();
		fees.setIndexType("  ");

		final var entity = EntityMapper.toFeesEmbeddable(fees);

		assertThat(entity.getIndexType()).isNull();
	}

	@Test
	void testToLeaseholdEntityFiltersBlankAdditionalInformation() {
		final var leasehold = createContract().getLeasehold();
		leasehold.setAdditionalInformation(new ArrayList<>(java.util.Arrays.asList("keep", "  ", "", null, "alsoKeep")));

		final var entity = EntityMapper.toLeaseholdEntity(leasehold);

		assertThat(entity.getAdditionalInformation()).containsExactly("keep", "alsoKeep");
	}

	@Test
	void testToAddressEmbeddable() {

		// Arrange
		final var dto = createContract().getStakeholders().getFirst().getAddress();

		// Act
		final var entity = EntityMapper.toAddressEmbeddable(dto);

		// Assert
		assertThat(entity.getAttention()).isEqualTo(dto.getAttention());
		assertThat(entity.getCareOf()).isEqualTo(dto.getCareOf());
		assertThat(entity.getCountry()).isEqualTo(dto.getCountry());
		assertThat(entity.getPostalCode()).isEqualTo(dto.getPostalCode());
		assertThat(entity.getStreetAddress()).isEqualTo(dto.getStreetAddress());
		assertThat(entity.getTown()).isEqualTo(dto.getTown());
		assertThat(entity.getType()).isEqualTo(dto.getType());
	}

	@Test
	void testToAttachmentEntity() {

		// Arrange
		final var dto = createAttachment();

		// Act
		final var entity = EntityMapper.toAttachmentEntity(MUNICIPALITY_ID, "2024-12345", dto);

		// Assert
		assertThat(entity.getCategory()).isEqualTo(dto.getMetadata().getCategory());
		assertThat(entity.getContent()).isEqualTo(dto.getAttachmentData().getContent().getBytes());
		assertThat(entity.getContractId()).isEqualTo("2024-12345");
		assertThat(entity.getFilename()).isEqualTo(dto.getMetadata().getFilename());
		assertThat(entity.getMimeType()).isEqualTo(dto.getMetadata().getMimeType());
		assertThat(entity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(entity.getNote()).isEqualTo(dto.getMetadata().getNote());
	}

	@Test
	void testUpdateAttachmentEntity() {

		// Arrange
		final var entity = createAttachmentEntity();
		final var dto = createAttachment();

		// Act
		final var updatedEntity = EntityMapper.updateAttachmentEntity(entity, dto);

		// Assert
		assertThat(updatedEntity.getCategory()).isEqualTo(dto.getMetadata().getCategory());
		assertThat(updatedEntity.getFilename()).isEqualTo(dto.getMetadata().getFilename());
		assertThat(updatedEntity.getMimeType()).isEqualTo(dto.getMetadata().getMimeType());
		assertThat(updatedEntity.getNote()).isEqualTo(dto.getMetadata().getNote());
		assertThat(updatedEntity.getContent()).isEqualTo(dto.getAttachmentData().getContent().getBytes());
	}

	@Test
	void testCreateNewContractEntity() {

		// Arrange
		final var oldContractEntity = createContractEntity();
		final var newContract = createContract();

		// Act
		final var updatedEntity = EntityMapper.createNewContractEntity(MUNICIPALITY_ID, oldContractEntity, newContract);

		assertThat(updatedEntity.getVersion()).isEqualTo(oldContractEntity.getVersion() + 1);
		assertThat(updatedEntity.getContractId()).isEqualTo(oldContractEntity.getContractId());
	}

	@Test
	void testPatchContractEntity_updatesOnlyProvidedFields() {

		// Arrange
		final var entity = createContractEntity();
		final var originalVersion = entity.getVersion();
		final var originalStatus = entity.getStatus();
		final var originalType = entity.getType();
		final var originalArea = entity.getArea();

		final var patch = PatchContract.builder()
			.withDescription("a new description")
			.build();

		// Act
		final var result = EntityMapper.patchContractEntity(entity, patch);

		// Assert
		assertThat(result).isSameAs(entity);
		assertThat(result.getDescription()).isEqualTo("a new description");
		assertThat(result.getVersion()).isEqualTo(originalVersion);
		assertThat(result.getStatus()).isEqualTo(originalStatus);
		assertThat(result.getType()).isEqualTo(originalType);
		assertThat(result.getArea()).isEqualTo(originalArea);
	}

	private static PatchContract buildFullPatchFrom(final Contract source) {
		return PatchContract.builder()
			.withDescription(source.getDescription())
			.withArea(source.getArea())
			.withAreaData(source.getAreaData())
			.withEndDate(source.getEndDate())
			.withStartDate(source.getStartDate())
			.withExternalReferenceId(source.getExternalReferenceId())
			.withObjectIdentity(source.getObjectIdentity())
			.withStatus(source.getStatus())
			.withType(source.getType())
			.withSignedByWitness(source.isSignedByWitness())
			.withExtension(source.getExtension())
			.withCurrentPeriod(source.getCurrentPeriod())
			.withNotice(source.getNotice())
			.withFees(source.getFees())
			.withInvoicing(source.getInvoicing())
			.withLeasehold(source.getLeasehold())
			.withPropertyDesignations(source.getPropertyDesignations())
			.withStakeholders(source.getStakeholders())
			.withExtraParameters(source.getExtraParameters())
			.withIndexTerms(source.getIndexTerms())
			.withAdditionalTerms(source.getAdditionalTerms())
			.build();
	}

	private static ContractEntity createMutableContractEntity() {
		final var entity = createContractEntity();
		entity.setPropertyDesignations(new ArrayList<>(entity.getPropertyDesignations()));
		entity.setStakeholders(new ArrayList<>(entity.getStakeholders()));
		entity.setExtraParameters(new ArrayList<>(entity.getExtraParameters()));
		entity.setNoticeTerms(new ArrayList<>(entity.getNoticeTerms()));
		entity.setTermGroups(new ArrayList<>(entity.getTermGroups()));
		return entity;
	}

	@Test
	void testPatchContractEntity_appliesScalarAndNestedScalarFields() {

		// Arrange
		final var entity = createMutableContractEntity();
		final var patch = buildFullPatchFrom(createContract());

		// Act
		EntityMapper.patchContractEntity(entity, patch);

		// Assert — scalar and scalar-inside-nested fields from the patch payload are applied onto the entity
		assertThat(entity.getDescription()).isEqualTo(patch.getDescription());
		assertThat(entity.getArea()).isEqualTo(patch.getArea());
		assertThat(entity.getAreaData()).isEqualTo(patch.getAreaData());
		assertThat(entity.getEndDate()).isEqualTo(patch.getEndDate());
		assertThat(entity.getStartDate()).isEqualTo(patch.getStartDate());
		assertThat(entity.getExternalReferenceId()).isEqualTo(patch.getExternalReferenceId());
		assertThat(entity.getObjectIdentity()).isEqualTo(patch.getObjectIdentity());
		assertThat(entity.getStatus()).isEqualTo(patch.getStatus());
		assertThat(entity.getType()).isEqualTo(patch.getType());
		assertThat(entity.isSignedByWitness()).isEqualTo(patch.getSignedByWitness());
		assertThat(entity.getLeaseExtension()).isEqualTo(patch.getExtension().getLeaseExtension());
		assertThat(entity.getLeaseExtensionUnit()).isEqualTo(patch.getExtension().getUnit());
		assertThat(entity.getAutoExtend()).isEqualTo(patch.getExtension().getAutoExtend());
		assertThat(entity.getCurrentPeriodStartDate()).isEqualTo(patch.getCurrentPeriod().getStartDate());
		assertThat(entity.getCurrentPeriodEndDate()).isEqualTo(patch.getCurrentPeriod().getEndDate());
		assertThat(entity.getNoticeDate()).isEqualTo(patch.getNotice().getNoticeDate());
		assertThat(entity.getNoticeGivenBy()).isEqualTo(patch.getNotice().getNoticeGivenBy());
	}

	@Test
	void testPatchContractEntity_replacesNestedCollectionsAndEmbeddables() {

		// Arrange
		final var entity = createMutableContractEntity();
		final var patch = buildFullPatchFrom(createContract());

		// Act
		EntityMapper.patchContractEntity(entity, patch);

		// Assert — collections and embedded value objects are replaced
		assertThat(entity.getNoticeTerms()).hasSameSizeAs(patch.getNotice().getTerms());
		assertThat(entity.getFees()).isNotNull();
		assertThat(entity.getInvoicing()).isNotNull();
		assertThat(entity.getLeasehold()).isNotNull();
		assertThat(entity.getPropertyDesignations()).hasSameSizeAs(patch.getPropertyDesignations());
		assertThat(entity.getStakeholders()).hasSameSizeAs(patch.getStakeholders());
		assertThat(entity.getExtraParameters()).hasSameSizeAs(patch.getExtraParameters());
		assertThat(entity.getTermGroups())
			.hasSize(patch.getIndexTerms().size() + patch.getAdditionalTerms().size());
	}

	@Test
	void testPatchContractEntity_replacesCollectionsOnImmutableExistingLists() {

		// Arrange — existing collections from TestFactory use List.of (immutable); exercises setter-fallback path
		final var entity = createContractEntity();

		final var patch = PatchContract.builder()
			.withPropertyDesignations(List.of(
				PropertyDesignation.builder()
					.withName("patchedName")
					.withDistrict("patchedDistrict")
					.build()))
			.build();

		// Act
		EntityMapper.patchContractEntity(entity, patch);

		// Assert
		assertThat(entity.getPropertyDesignations())
			.hasSize(1)
			.extracting(PropertyDesignationEmbeddable::getName, PropertyDesignationEmbeddable::getDistrict)
			.containsExactly(Tuple.tuple("patchedName", "patchedDistrict"));
	}

	@Test
	void testPatchContractEntity_replacesOnlyIndexTermsKeepsAdditional() {

		// Arrange
		final var entity = createContractEntity();
		entity.setTermGroups(new ArrayList<>(entity.getTermGroups()));
		final var originalAdditional = entity.getTermGroups().stream()
			.filter(tg -> TermGroupEntity.TYPE_ADDITIONAL.equals(tg.getType()))
			.toList();

		final var patch = PatchContract.builder()
			.withIndexTerms(List.of(
				TermGroup.builder()
					.withHeader("replaced index header")
					.withTerms(List.of(Term.builder()
						.withName("t1").withDescription("d1").build()))
					.build()))
			.build();

		// Act
		EntityMapper.patchContractEntity(entity, patch);

		// Assert
		assertThat(entity.getTermGroups())
			.filteredOn(tg -> TermGroupEntity.TYPE_INDEX.equals(tg.getType()))
			.hasSize(1)
			.extracting("header")
			.containsExactly("replaced index header");
		assertThat(entity.getTermGroups())
			.filteredOn(tg -> TermGroupEntity.TYPE_ADDITIONAL.equals(tg.getType()))
			.hasSameSizeAs(originalAdditional);
	}

	@Test
	void testMinimalToContractEntity() {

		// Arrange
		final var dto = Contract.builder()
			.withStatus(Status.DRAFT)
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		// Act
		final var entity = EntityMapper.toContractEntity(MUNICIPALITY_ID, dto);

		// Assert
		assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
		assertThat(entity.getType()).isEqualTo(dto.getType());
		assertThat(entity.getLeaseExtensionUnit()).isEqualTo(TimeUnit.DAYS);
	}

	@Test
	void testToNoticeTermEmbeddableWithNullUnitDefaultsToDays() {

		// Arrange
		final var notice = Notice.builder()
			.withTerms(List.of(
				NoticeTerm.builder()
					.withParty(Party.LESSEE)
					.withPeriodOfNotice(3)
					.build()))
			.build();

		// Act
		final var noticeEmbeddables = EntityMapper.toNoticeTermEmbeddables(notice);

		// Assert
		assertThat(noticeEmbeddables)
			.hasSize(1)
			.extracting(NoticeTermEmbeddable::getUnit)
			.containsExactly(TimeUnit.DAYS);
	}

	@Test
	void testUpdateAttachmentEntityWithNullMetadataFieldsPreservesOriginalValues() {
		// Arrange - entity with existing values
		final var entity = createAttachmentEntity();
		final var originalCategory = entity.getCategory();
		final var originalFilename = entity.getFilename();

		// Attachment metadata where all fields are null — setPropertyUnlessNull should skip the setters
		final var attachment = Attachment.builder()
			.withMetadata(AttachmentMetadata.builder().build())
			.build();

		// Act
		final var updated = EntityMapper.updateAttachmentEntity(entity, attachment);

		// Assert - original values preserved since null was not propagated
		assertThat(updated.getCategory()).isEqualTo(originalCategory);
		assertThat(updated.getFilename()).isEqualTo(originalFilename);
	}

	@Test
	void testToNoticeTermEmbeddables() {

		// Arrange
		final var entity = createContract();

		// Act
		final var noticeEmbeddables = EntityMapper.toNoticeTermEmbeddables(entity.getNotice());

		// Assert
		assertThat(noticeEmbeddables)
			.hasSize(2)
			.containsExactlyInAnyOrder(
				NoticeTermEmbeddable.builder()
					.withParty(Party.LESSEE)
					.withPeriodOfNotice(3)
					.withUnit(TimeUnit.MONTHS)
					.build(),
				NoticeTermEmbeddable.builder()
					.withParty(Party.LESSOR)
					.withPeriodOfNotice(1)
					.withUnit(TimeUnit.MONTHS)
					.build());
	}
}
