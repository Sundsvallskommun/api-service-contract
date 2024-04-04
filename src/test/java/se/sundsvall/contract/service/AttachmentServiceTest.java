package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
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

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

	@Mock
	private ContractRepository mockContractRepository;

	@Mock
	private AttachmentRepository mockAttachmentRepository;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private ContractMapper mockContractMapper;

	@InjectMocks
	private AttachmentService attachmentService;

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";
	private static final Long ENTITY_ID = 1L;



	@Test
	void testCreateAttachment() {
		//Arrange
		var attachment = TestFactory.createAttachment();

		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);
		when(mockAttachmentRepository.save(any(AttachmentEntity.class))).thenReturn(AttachmentEntity.builder()
				.withId(ENTITY_ID)
			.build());

		//Act
		var createdAttachment = attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, attachment);

		//Assert
		assertThat(createdAttachment).isEqualTo(ENTITY_ID);
		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockContractMapper).toAttachmentEntity(CONTRACT_ID, attachment);
		verify(mockAttachmentRepository).save(any(AttachmentEntity.class));

		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockContractMapper);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testCreateAttachment_shouldThrow404_whenNotFound() {
		//Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.createAttachment(MUNICIPALITY_ID, CONTRACT_ID, Attachment.builder().build()))
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
			.withMessage("Not Found");

		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockContractMapper);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testGetAttachmentData() {
		//Arrange
		when(mockAttachmentRepository.findById(ENTITY_ID)).thenReturn(Optional.of(AttachmentEntity.builder()
				.withContent("someContent".getBytes())
			.build()));

		//Act
		var attachment = attachmentService.getAttachmentData(ENTITY_ID);

		//Assert
		assertThat(attachment).isNotNull();
		verify(mockAttachmentRepository).findById(ENTITY_ID);
		verify(mockContractMapper).toAttachmentDataDto(any(AttachmentEntity.class));
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockContractMapper);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testGetAttachmentData_shouldThrow404_whenNotFound() {
		//Arrange
		when(mockAttachmentRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.getAttachmentData(ENTITY_ID))
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
			.withMessage("Not Found");

		verify(mockAttachmentRepository).findById(ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockContractMapper);
		verifyNoMoreInteractions(mockAttachmentRepository);

	}

	@Test
	void testUpdateAttachment() {
		//Arrange
		//Set up a captor since we want to verify what's being saved, not what comes back.
		ArgumentCaptor<AttachmentEntity> argumentCaptor = ArgumentCaptor.forClass(AttachmentEntity.class);

		var incomingAttachment = TestFactory.createAttachment();
		var oldAttachmentEntity = TestFactory.createAttachmentEntity();
		when(mockAttachmentRepository.findById(ENTITY_ID)).thenReturn(Optional.of(oldAttachmentEntity));
		when(mockAttachmentRepository.save(any(AttachmentEntity.class))).thenReturn(AttachmentEntity.builder()
				.withContractId("2024-12345")
			.build());

		//Act
		var updatedEntity = attachmentService.updateAttachment(ENTITY_ID, incomingAttachment);

		//Assert
		verify(mockAttachmentRepository).findById(ENTITY_ID);
		verify(mockContractMapper).updateAttachmentEntity(oldAttachmentEntity, incomingAttachment);
		verify(mockAttachmentRepository).save(argumentCaptor.capture());
		verify(mockContractMapper).toAttachmentMetaDataDto(any(AttachmentEntity.class));

		var savedEntity = argumentCaptor.getValue();
		assertThat(savedEntity.getCategory()).isEqualTo(AttachmentCategory.CONTRACT);
		assertThat(savedEntity.getId()).isEqualTo(123L);
		assertThat(savedEntity.getFilename()).isEqualTo("file.pdf");
		assertThat(savedEntity.getMimeType()).isEqualTo("mimeType");
		assertThat(savedEntity.getNote()).isEqualTo("aNote");
		assertThat(savedEntity.getContent()).isEqualTo("someContent".getBytes());
		assertThat(savedEntity.getContractId()).isEqualTo("2024-12345");

		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockContractMapper);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testUpdateAttachment_shouldThrow404_whenNotFound() {
		//Arrange
		when(mockAttachmentRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> attachmentService.updateAttachment(ENTITY_ID, Attachment.builder().build()))
			.matches(problem -> problem.getStatus() == Status.NOT_FOUND)
			.withMessage("Not Found");

		verify(mockAttachmentRepository).findById(ENTITY_ID);
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockContractMapper);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void testDeleteAttachment() {
		//Arrange
		doNothing().when(mockAttachmentRepository).deleteById(ENTITY_ID);

		//Act
		attachmentService.deleteAttachment(ENTITY_ID);

		//Assert
		verify(mockAttachmentRepository).deleteById(ENTITY_ID);

		verifyNoMoreInteractions(mockAttachmentRepository);
	}
}