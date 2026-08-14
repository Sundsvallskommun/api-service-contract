package se.sundsvall.contract.integration.db.model;

import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.TestFactory.toBlob;
import static se.sundsvall.contract.model.enums.AttachmentCategory.CONTRACT;

class AttachmentEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
		registerValueGenerator(() -> AttachmentDataEntity.builder().withId(new Random().nextLong()).build(), AttachmentDataEntity.class);
	}

	/**
	 * The content is excluded from equals/hashCode because a blob locator has no meaningful equality - the hash stands in
	 * for it. It is excluded from toString because rendering it would initialize the lazy association, which is exactly
	 * what holding the content in a separate table is meant to avoid.
	 */
	@Test
	void testBean() {
		assertThat(AttachmentEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("attachmentData"),
			hasValidBeanEqualsExcluding("attachmentData"),
			hasValidBeanToStringExcluding("attachmentData")));
	}

	@Test
	void testBuilderMethods() {
		final var id = 1L;
		final var contractId = "contractId";
		final var municipalityId = "1984";
		final var filename = "filename";
		final var category = CONTRACT;
		final var mimeType = "mimeType";
		final var attachmentData = AttachmentDataEntity.builder().withFile(toBlob("fileContent".getBytes(UTF_8))).build();
		final var note = "note";
		final var hash = "hash";
		final var created = OffsetDateTime.now();

		final var attachment = AttachmentEntity.builder()
			.withId(id)
			.withContractId(contractId)
			.withMunicipalityId(municipalityId)
			.withFilename(filename)
			.withCategory(category)
			.withMimeType(mimeType)
			.withAttachmentData(attachmentData)
			.withNote(note)
			.withHash(hash)
			.withCreated(created)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachment.getId()).isEqualTo(id);
		assertThat(attachment.getContractId()).isEqualTo(contractId);
		assertThat(attachment.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getAttachmentData()).isSameAs(attachmentData);
		assertThat(attachment.getNote()).isEqualTo(note);
		assertThat(attachment.getHash()).isEqualTo(hash);
		assertThat(attachment.getCreated()).isEqualTo(created);
	}

	@Test
	void testPrePersist() {
		var entity = AttachmentEntity.builder().build();
		assertThat(entity.getCreated()).isNull();
		entity.prePersist();
		assertThat(entity.getCreated()).isNotNull();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
