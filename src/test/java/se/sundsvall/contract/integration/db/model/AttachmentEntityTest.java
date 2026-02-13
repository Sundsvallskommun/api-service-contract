package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.model.enums.AttachmentCategory.CONTRACT;

import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AttachmentEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(AttachmentEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var id = 1L;
		final var contractId = "contractId";
		final var municipalityId = "1984";
		final var filename = "filename";
		final var category = CONTRACT;
		final var mimeType = "mimeType";
		final var fileContent = "fileContent".getBytes(UTF_8);
		final var note = "note";
		final var created = OffsetDateTime.now();

		final var attachment = AttachmentEntity.builder()
			.withId(id)
			.withContractId(contractId)
			.withMunicipalityId(municipalityId)
			.withFilename(filename)
			.withCategory(category)
			.withMimeType(mimeType)
			.withContent(fileContent)
			.withNote(note)
			.withCreated(created)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachment.getId()).isEqualTo(id);
		assertThat(attachment.getContractId()).isEqualTo(contractId);
		assertThat(attachment.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getContent()).isEqualTo(fileContent);
		assertThat(attachment.getNote()).isEqualTo(note);
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
