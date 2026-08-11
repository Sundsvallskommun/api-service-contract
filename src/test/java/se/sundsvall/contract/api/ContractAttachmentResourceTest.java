package se.sundsvall.contract.api;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.contract.service.AttachmentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@ActiveProfiles("junit")
@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class ContractAttachmentResourceTest {

	private static final String CONTRACT_ID = "2024-12345";
	private static final String MUNICIPALITY_ID = "1984";
	private static final String BASE_URL = "/" + MUNICIPALITY_ID + "/contracts/" + CONTRACT_ID + "/attachments";
	private static final byte[] FILE_CONTENT = "someContent".getBytes(StandardCharsets.UTF_8);

	@MockitoBean
	private AttachmentService attachmentService;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testGetAttachments() {
		// Arrange
		final var metadata = AttachmentMetadata.builder()
			.withId(1L)
			.withCategory(AttachmentCategory.CONTRACT)
			.withFilename("aFilename")
			.withMimeType("aMimeType")
			.withHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
			.build();
		when(attachmentService.getAttachments(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(List.of(metadata));

		// Act
		final var response = webTestClient.get()
			.uri(BASE_URL)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(AttachmentMetadata.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).containsExactly(metadata);
		verify(attachmentService).getAttachments(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testGetAttachmentByIdReturnsBinaryContent() {
		// Arrange - the service writes straight to the response, so the stub has to do the same
		doAnswer(invocation -> {
			final var response = invocation.getArgument(3, HttpServletResponse.class);
			response.setContentType(APPLICATION_PDF.toString());
			response.getOutputStream().write(FILE_CONTENT);
			return null;
		}).when(attachmentService).streamAttachment(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), eq(1L), any());

		// Act
		final var response = webTestClient.get()
			.uri(BASE_URL + "/1")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_PDF)
			.expectBody(byte[].class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isEqualTo(FILE_CONTENT);
		verify(attachmentService).streamAttachment(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), eq(1L), any());
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testCreateAttachment() {
		// Arrange
		when(attachmentService.createAttachment(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), any(), any())).thenReturn(1L);

		// Act
		webTestClient.post()
			.uri(BASE_URL)
			.contentType(MULTIPART_FORM_DATA)
			.bodyValue(multipartBody("""
				{"category":"OTHER","filename":"aFilename","mimeType":"aMimeType","note":"aNote"}""", FILE_CONTENT))
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().valueEquals("Location", BASE_URL + "/1");

		// Assert
		final var expectedMetadata = AttachmentMetadata.builder()
			.withCategory(AttachmentCategory.OTHER)
			.withFilename("aFilename")
			.withMimeType("aMimeType")
			.withNote("aNote")
			.build();
		verify(attachmentService).createAttachment(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), eq(expectedMetadata), any(MultipartFile.class));
		verifyNoMoreInteractions(attachmentService);
	}

	@Test
	void testCreateAttachmentWithEmptyFileIsRejected() {
		// Act
		webTestClient.post()
			.uri(BASE_URL)
			.contentType(MULTIPART_FORM_DATA)
			.bodyValue(multipartBody("""
				{"filename":"aFilename","mimeType":"aMimeType"}""", new byte[0]))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody()
			.jsonPath("$.detail").isEqualTo("The 'file' part must not be empty");

		// Assert
		verifyNoInteractions(attachmentService);
	}

	@Test
	void testCreateAttachmentWithMalformedMetadataIsRejected() {
		// Act - the parser message must not be echoed back, so only the generic detail is expected
		webTestClient.post()
			.uri(BASE_URL)
			.contentType(MULTIPART_FORM_DATA)
			.bodyValue(multipartBody("{ this is not json", FILE_CONTENT))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody()
			.jsonPath("$.detail").isEqualTo("The 'attachment' part must be valid JSON");

		// Assert
		verifyNoInteractions(attachmentService);
	}

	@Test
	void testCreateAttachmentWithoutRequiredMetadataIsRejected() {
		// Act - bean validation is not applied to a @RequestPart String, so this proves the explicit validation runs
		webTestClient.post()
			.uri(BASE_URL)
			.contentType(MULTIPART_FORM_DATA)
			.bodyValue(multipartBody("""
				{"note":"aNote"}""", FILE_CONTENT))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody()
			.jsonPath("$.violations[*].field").value((List<String> fields) -> assertThat(fields).containsExactlyInAnyOrder("filename", "mimeType"));

		// Assert
		verifyNoInteractions(attachmentService);
	}

	@Test
	void testPatchAttachment() {
		// Arrange
		final var metadata = AttachmentMetadata.builder()
			.withNote("aNewNote")
			.withCategory(AttachmentCategory.OTHER)
			.build();
		doNothing().when(attachmentService).updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L, metadata);

		// Act
		webTestClient.patch()
			.uri(BASE_URL + "/1")
			.contentType(APPLICATION_JSON)
			.bodyValue(metadata)
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(attachmentService).updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, 1L, metadata);
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

	private static MultiValueMap<String, HttpEntity<?>> multipartBody(final String metadata, final byte[] content) {
		final var builder = new MultipartBodyBuilder();
		builder.part("attachment", metadata).contentType(APPLICATION_JSON);
		builder.part("file", new ByteArrayResource(content) {

			@Override
			public String getFilename() {
				return "aFilename";
			}
		});
		return builder.build();
	}
}
