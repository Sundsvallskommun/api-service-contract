package se.sundsvall.contract.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.model.enums.AttachmentCategory.CONTRACT;

import org.junit.jupiter.api.Test;

class AttachmentEntityTest {

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
		var id = 1L;
		var contractId = "contractId";
		var filename = "filename";
		var category = CONTRACT;
		var mimeType = "mimeType";
		var fileContent = "fileContent".getBytes(UTF_8);
		var note = "note";

		var attachment = AttachmentEntity.builder()
			.withId(id)
			.withContractId(contractId)
			.withFilename(filename)
			.withCategory(category)
			.withMimeType(mimeType)
			.withContent(fileContent)
			.withNote(note)
			.build();

		assertThat(attachment).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachment.getId()).isEqualTo(id);
		assertThat(attachment.getContractId()).isEqualTo(contractId);
		assertThat(attachment.getFilename()).isEqualTo(filename);
		assertThat(attachment.getCategory()).isEqualTo(category);
		assertThat(attachment.getMimeType()).isEqualTo(mimeType);
		assertThat(attachment.getContent()).isEqualTo(fileContent);
		assertThat(attachment.getNote()).isEqualTo(note);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
