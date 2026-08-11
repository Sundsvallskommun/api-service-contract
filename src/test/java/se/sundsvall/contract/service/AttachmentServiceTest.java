package se.sundsvall.contract.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.AttachmentMetadata;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.model.enums.AttachmentCategory;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";
	private static final Long ENTITY_ID = 1L;
	private static final byte[] FILE_CONTENT = "someContent".getBytes(UTF_8);

	@Mock
	private ContractRepository mockContractRepository;

	@Mock
	private AttachmentRepository mockAttachmentRepository;

	@InjectMocks
	private AttachmentService attachmentService;

	private static MockMultipartFile file() {
		return new MockMultipartFile("file", "file.pdf", "application/pdf", FILE_CONTENT);
	}

	@Test
	void testCreateAttachment() {
		// Arrange
		final var argumentCaptor = ArgumentCaptor.forClass(AttachmentEntity.class);
		final var metadata = TestFactory.createAttachmentMetadata();

		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);
		when(mockAttachmentRepository.save(any(AttachmentEntity.class))).thenReturn(AttachmentEntity.builder()
			.withId(ENTITY_ID)
			.build());

		// Act
		final var createdAttachmentId = attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, metadata, file());

		// Assert
		assertThat(createdAttachmentId).isEqualTo(ENTITY_ID);
		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockAttachmentRepository).save(argumentCaptor.capture());

		final var savedEntity = argumentCaptor.getValue();
		assertThat(savedEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(savedEntity.getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(savedEntity.getCategory()).isEqualTo(AttachmentCategory.CONTRACT);
		assertThat(savedEntity.getFilename()).isEqualTo("file.pdf");
		assertThat(savedEntity.getMimeType()).isEqualTo("mimeType");
		assertThat(savedEntity.getNote()).isEqualTo("aNote");
		// The raw upload bytes are stored, not a base64 rendering of them
		assertThat(savedEntity.getContent()).isEqualTo(FILE_CONTENT);

		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testCreateAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		final var metadata = AttachmentMetadata.builder().build();
		final var file = file();
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, metadata, file))
			.matches(problem -> problem.getStatus() == HttpStatus.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' is not present within municipality '1984'.");

		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testCreateAttachmentShouldThrow400WhenFileCannotBeRead() throws IOException {
		// Arrange
		final var metadata = TestFactory.createAttachmentMetadata();
		final var file = mock(MultipartFile.class);
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);
		doThrow(new IOException("disk gone")).when(file).getBytes();

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, metadata, file))
			.matches(problem -> problem.getStatus() == HttpStatus.BAD_REQUEST)
			.withMessageContaining("IOException occurred when reading the uploaded file: disk gone");

		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testGetAttachments() {
		// Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);
		when(mockAttachmentRepository.findAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(List.of(TestFactory.createAttachmentEntity()));

		// Act
		final var attachments = attachmentService.getAttachments(MUNICIPALITY_ID, CONTRACT_ID);

		// Assert
		assertThat(attachments).hasSize(1);
		assertThat(attachments.getFirst().getFilename()).isEqualTo("mycontract.pdf");
		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockAttachmentRepository).findAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testGetAttachmentsShouldThrow404WhenContractNotFound() {
		// Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.getAttachments(MUNICIPALITY_ID, CONTRACT_ID))
			.matches(problem -> problem.getStatus() == HttpStatus.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' is not present within municipality '1984'.");

		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testStreamAttachment() {
		// Arrange
		final var response = new MockHttpServletResponse();
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID))
			.thenReturn(Optional.of(TestFactory.createAttachmentEntity()));

		// Act
		attachmentService.streamAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, response);

		// Assert
		assertThat(response.getHeader(CONTENT_TYPE)).isEqualTo("application/pdf");
		assertThat(response.getHeader(CONTENT_DISPOSITION)).isEqualTo("attachment; filename=\"mycontract.pdf\"");
		// A client-supplied mime type must never be sniffed and rendered in the API's origin
		assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
		assertThat(response.getContentAsByteArray()).isEqualTo("data".getBytes(UTF_8));

		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testStreamAttachmentFallsBackToOctetStreamWhenMimeTypeIsMissing() {
		// Arrange
		final var response = new MockHttpServletResponse();
		final var entity = TestFactory.createAttachmentEntity();
		entity.setMimeType(null);
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID))
			.thenReturn(Optional.of(entity));

		// Act
		attachmentService.streamAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, response);

		// Assert
		assertThat(response.getHeader(CONTENT_TYPE)).isEqualTo("application/octet-stream");
	}

	@Test
	void testStreamAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		final var response = new MockHttpServletResponse();
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.streamAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, response))
			.matches(problem -> problem.getStatus() == HttpStatus.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' and attachmentId '1' is not present within municipality '1984'.");

		// Nothing may have been written, since status and headers cannot be changed after the response is committed
		assertThat(response.getContentAsByteArray()).isEmpty();

		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testStreamAttachmentShouldThrow500WhenResponseCannotBeWritten() throws IOException {
		// Arrange
		final var response = mock(jakarta.servlet.http.HttpServletResponse.class);
		when(response.getOutputStream()).thenThrow(new IOException("connection reset"));
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID))
			.thenReturn(Optional.of(TestFactory.createAttachmentEntity()));

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.streamAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, response))
			.matches(problem -> problem.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR)
			.withMessageContaining("IOException occurred when copying file with attachment id '1' to response: connection reset");
	}

	@Test
	void testUpdateAttachment() {
		// Arrange
		// Set up a captor since we want to verify what's being saved, not what comes back.
		final var argumentCaptor = ArgumentCaptor.forClass(AttachmentEntity.class);

		final var incomingMetadata = TestFactory.createAttachmentMetadata();
		final var oldAttachmentEntity = TestFactory.createAttachmentEntity();
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.of(oldAttachmentEntity));
		when(mockAttachmentRepository.save(any(AttachmentEntity.class))).thenReturn(oldAttachmentEntity);

		// Act
		attachmentService.updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, incomingMetadata);

		// Assert
		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verify(mockAttachmentRepository).save(argumentCaptor.capture());

		final var savedEntity = argumentCaptor.getValue();
		assertThat(savedEntity.getCategory()).isEqualTo(AttachmentCategory.CONTRACT);
		assertThat(savedEntity.getId()).isEqualTo(123L);
		assertThat(savedEntity.getFilename()).isEqualTo("file.pdf");
		assertThat(savedEntity.getMimeType()).isEqualTo("mimeType");
		assertThat(savedEntity.getNote()).isEqualTo("aNote");
		assertThat(savedEntity.getContractId()).isEqualTo("2024-12345");
		// The binary content is immutable - a metadata patch must leave it alone
		assertThat(savedEntity.getContent()).isEqualTo("data".getBytes(UTF_8));

		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testUpdateAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		final var metadata = AttachmentMetadata.builder().build();
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, metadata))
			.matches(problem -> problem.getStatus() == HttpStatus.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' and attachmentId '1' is not present within municipality '1984'.");

		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testDeleteAttachment() {
		// Arrange
		when(mockAttachmentRepository.existsByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(true);
		doNothing().when(mockAttachmentRepository).deleteByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);

		// Act
		attachmentService.deleteAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);

		// Assert
		verify(mockAttachmentRepository).existsByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verify(mockAttachmentRepository).deleteByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);

		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testDeleteAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		when(mockAttachmentRepository.existsByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(false);

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.deleteAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID))
			.matches(problem -> problem.getStatus() == HttpStatus.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' and attachmentId '1' is not present within municipality '1984'.");

		verifyNoMoreInteractions(mockAttachmentRepository);
	}
}
