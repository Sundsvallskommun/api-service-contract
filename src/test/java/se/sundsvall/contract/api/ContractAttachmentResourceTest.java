package se.sundsvall.contract.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.contract.service.AttachmentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class ContractAttachmentResourceTest {

	private static final String CONTRACT_ID = "2024-12345";
	private static final String MUNICIPALITY_ID = "1984";
	private static final String BASE_URL = "/" + MUNICIPALITY_ID + "/contracts/" + CONTRACT_ID + "/attachments";

	@MockitoBean
	private AttachmentService attachmentService;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testGetAttachmentById() {
		// Arrange
		final var attachment = TestFactory.createAttachment();
		when(attachmentService.getAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L)).thenReturn(attachment);

		// Act
		final var response = webTestClient.get()
			.uri(BASE_URL + "/1")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(AttachmentData.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertNotNull(response);
		verify(attachmentService).getAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L);
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testCreateAttachment() {
		// Arrange
		final Attachment attachment = Attachment.builder()
			.withAttachmentData(AttachmentData.builder()
				.withContent("someContent")
				.build())
			.withMetadata(AttachmentMetadata.builder()
				.withCategory(AttachmentCategory.OTHER)
				.withFilename("aFilename")
				.withMimeType("aMimeType")
				.withNote("aNote")
				.build())
			.build();

		when(attachmentService.createAttachment(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), any())).thenReturn(1L);

		// Act
		webTestClient.post()
			.uri(BASE_URL)
			.bodyValue(attachment)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL_VALUE)
			.expectHeader().valueEquals("Location", BASE_URL + "/1");

		// Assert
		verify(attachmentService).createAttachment("1984", CONTRACT_ID, attachment);
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testUpdateAttachmentMetaData() {
		// Arrange
		final var attachmentMetaData = AttachmentMetadata.builder()
			.withFilename("aNewFilename")
			.withMimeType("aNewMimeType")
			.withNote("aNewNote")
			.withCategory(AttachmentCategory.OTHER)
			.build();

		final Attachment attachment = Attachment.builder()
			.withAttachmentData(AttachmentData.builder()
				.withContent("someNewContent")
				.build())
			.withMetadata(attachmentMetaData)
			.build();

		when(attachmentService.updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L, attachment)).thenReturn(attachmentMetaData);

		// Act
		final var response = webTestClient.put()
			.uri(BASE_URL + "/1")
			.bodyValue(attachment)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(AttachmentMetadata.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertNotNull(response);
		assertThat(response).isEqualTo(attachmentMetaData);
		verify(attachmentService).updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L, attachment);
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testDeleteAttachment() {
		// Arrange
		doNothing().when(attachmentService).deleteAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L);

		// Act
		webTestClient.delete()
			.uri(BASE_URL + "/1")
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(attachmentService).deleteAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L);
		verifyNoMoreInteractions(attachmentService);
	}
}
