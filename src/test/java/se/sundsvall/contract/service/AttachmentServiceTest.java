package se.sundsvall.contract.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.Attachment;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.AttachmentEntity;
import se.sundsvall.contract.model.enums.AttachmentCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";
	private static final Long ENTITY_ID = 1L;

	@Mock
	private ContractRepository mockContractRepository;

	@Mock
	private AttachmentRepository mockAttachmentRepository;

	@InjectMocks
	private AttachmentService attachmentService;

	@Test
	void testCreateAttachment() {
		// Arrange
		var attachment = TestFactory.createAttachment();

		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);
		when(mockAttachmentRepository.save(any(AttachmentEntity.class))).thenReturn(AttachmentEntity.builder()
			.withId(ENTITY_ID)
			.build());

		// Act
		var createdAttachment = attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, attachment);

		// Assert
		assertThat(createdAttachment).isEqualTo(ENTITY_ID);
		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockAttachmentRepository).save(any(AttachmentEntity.class));

		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testCreateAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		final var attachment = Attachment.builder().build();
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, attachment))
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' is not present within municipality '1984'.");

		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testGetAttachment() {
		// Arrange
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.of(TestFactory.createAttachmentEntity()));

		// Act
		var attachment = attachmentService.getAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);

		// Assert
		assertThat(attachment).isNotNull();
		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testGetAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.getAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID))
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' and attachmentId '1' is not present within municipality '1984'.");

		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);

	}

	@Test
	void testUpdateAttachment() {
		// Arrange
		// Set up a captor since we want to verify what's being saved, not what comes back.
		var argumentCaptor = ArgumentCaptor.forClass(AttachmentEntity.class);

		var incomingAttachment = TestFactory.createAttachment();
		var oldAttachmentEntity = TestFactory.createAttachmentEntity();
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.of(oldAttachmentEntity));
		when(mockAttachmentRepository.save(any(AttachmentEntity.class))).thenReturn(AttachmentEntity.builder()
			.withContractId("2024-12345")
			.build());

		// Act
		attachmentService.updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, incomingAttachment);

		// Assert
		verify(mockAttachmentRepository).findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID);
		verify(mockAttachmentRepository).save(argumentCaptor.capture());

		var savedEntity = argumentCaptor.getValue();
		assertThat(savedEntity.getCategory()).isEqualTo(AttachmentCategory.CONTRACT);
		assertThat(savedEntity.getId()).isEqualTo(123L);
		assertThat(savedEntity.getFilename()).isEqualTo("file.pdf");
		assertThat(savedEntity.getMimeType()).isEqualTo("mimeType");
		assertThat(savedEntity.getNote()).isEqualTo("aNote");
		assertThat(savedEntity.getContent()).isEqualTo("someContent".getBytes());
		assertThat(savedEntity.getContractId()).isEqualTo("2024-12345");

		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testUpdateAttachmentShouldThrow404WhenNotFound() {
		// Arrange
		final var attachment = Attachment.builder().build();
		when(mockAttachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.updateAttachment(MUNICIPALITY_ID, CONTRACT_ID, ENTITY_ID, attachment))
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
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
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
			.withMessage("Contract with contractId '2024-12345' and attachmentId '1' is not present within municipality '1984'.");

		verifyNoMoreInteractions(mockAttachmentRepository);
	}
}
