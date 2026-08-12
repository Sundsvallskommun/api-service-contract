package se.sundsvall.contract.integration.db.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static se.sundsvall.contract.TestFactory.bytesOf;
import static se.sundsvall.contract.TestFactory.toBlob;

class AttachmentDataEntityTest {

	/**
	 * The file is excluded from equals/hashCode on purpose - a blob is a locator with no meaningful equality. It is
	 * excluded from toString as well, since rendering a blob yields an identity hash that is noise in a log line.
	 */
	@Test
	void testBean() {
		assertThat(AttachmentDataEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCodeExcluding("file"),
			hasValidBeanEqualsExcluding("file"),
			hasValidBeanToStringExcluding("file")));
	}

	@Test
	void testBuilderMethods() {
		// Arrange
		final var id = 1L;
		final var content = "fileContent".getBytes(UTF_8);

		// Act
		final var attachmentData = AttachmentDataEntity.builder()
			.withId(id)
			.withFile(toBlob(content))
			.build();

		// Assert
		assertThat(attachmentData).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(attachmentData.getId()).isEqualTo(id);
		assertThat(bytesOf(attachmentData.getFile())).isEqualTo(content);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(AttachmentDataEntity.builder().build()).hasAllNullFieldsOrProperties();
	}
}
