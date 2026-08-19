package se.sundsvall.contract.integration.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.contract.integration.db.model.AttachmentDataEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class AttachmentRepositoryTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	@Autowired
	private AttachmentRepository attachmentRepository;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * The foreign key points from attachment to attachment_data, so removing an attachment without cascading would leave
	 * the data row behind silently - no constraint would complain. This pins down that the cascade actually runs.
	 */
	@Test
	void deleteAttachmentRemovesItsData() {
		// Arrange
		assertThat(countAttachmentData()).isOne();

		// Act
		attachmentRepository.deleteByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, 1L);
		entityManager.flush();

		// Assert
		assertThat(attachmentRepository.findByMunicipalityIdAndContractIdAndId(MUNICIPALITY_ID, CONTRACT_ID, 1L)).isEmpty();
		assertThat(countAttachmentData()).isZero();
	}

	/**
	 * Same guarantee for the bulk path used when a contract is deleted.
	 */
	@Test
	void deleteAllAttachmentsRemovesTheirData() {
		// Arrange
		assertThat(countAttachmentData()).isOne();

		// Act
		attachmentRepository.deleteAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		entityManager.flush();

		// Assert
		assertThat(attachmentRepository.findAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).isEmpty();
		assertThat(countAttachmentData()).isZero();
	}

	/**
	 * Reading metadata must not drag the blob along - that is the whole point of holding it in a separate table behind a
	 * lazy association.
	 */
	@Test
	void readingMetadataDoesNotInitializeContent() {
		// Act
		final var attachments = attachmentRepository.findAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);

		// Assert
		assertThat(attachments).hasSize(1);
		assertThat(entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
			.isLoaded(attachments.getFirst(), "attachmentData")).isFalse();
	}

	private long countAttachmentData() {
		return entityManager.createQuery("select count(e) from %s e".formatted(AttachmentDataEntity.class.getSimpleName()), Long.class).getSingleResult();
	}
}
