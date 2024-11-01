package se.sundsvall.contract.api;

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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.contract.Application;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.api.model.AttachmentData;
import se.sundsvall.contract.api.model.AttachmentMetaData;
import se.sundsvall.contract.service.AttachmentService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class ContractAttachmentResourceTest {

	@MockBean
	private AttachmentService attachmentService;

	@Autowired
	private WebTestClient webTestClient;

	private static final String CONTRACT_ID = "2024-12345";
	private static final String MUNICIPALITY_ID = "1984";

	@Test
	void testGetAttachmentById() {
		// Arrange
		var attachment = TestFactory.createAttachment();
		when(attachmentService.getAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L)).thenReturn(attachment);

		// Act
		var response = webTestClient.get()
			.uri("/contracts/1984/2024-12345/attachments/1")
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
		Attachment attachment = Attachment.builder()
			.withAttachmentData(AttachmentData.builder()
				.withContent("someContent")
				.build())
			.withMetaData(AttachmentMetaData.builder()
				.withCategory("aCategory")
				.withFilename("aFilename")
				.withMimeType("aMimeType")
				.withNote("aNote")
				.withCategory("CONTRACT")
				.build())
			.build();

		when(attachmentService.createAttachment(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), any())).thenReturn(1L);

		// Act
		webTestClient.post()
			.uri("/contracts/1984/2024-12345/attachments")
			.bodyValue(attachment)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL_VALUE)
			.expectHeader().valueEquals("Location", "/contracts/1984/2024-12345/attachments/1");

		// Assert
		verify(attachmentService).createAttachment("1984", CONTRACT_ID, attachment);
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testUpdateAttachmentMetaData() {
		// Arrange
		var attachmentMetaData = AttachmentMetaData.builder()
			.withCategory("aNewCategory")
			.withFilename("aNewFilename")
			.withMimeType("aNewMimeType")
			.withNote("aNewNote")
			.withCategory("OTHER")
			.build();

		Attachment attachment = Attachment.builder()
			.withAttachmentData(AttachmentData.builder()
				.withContent("someNewContent")
				.build())
			.withMetaData(attachmentMetaData)
			.build();

		when(attachmentService.updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L, attachment)).thenReturn(attachmentMetaData);

		// Act
		var response = webTestClient.put()
			.uri("/contracts/1984/2024-12345/attachments/1")
			.bodyValue(attachment)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(AttachmentMetaData.class)
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
			.uri("/contracts/1984/2024-12345/attachments/1")
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(attachmentService).deleteAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L);
		verifyNoMoreInteractions(attachmentService);
	}
}
