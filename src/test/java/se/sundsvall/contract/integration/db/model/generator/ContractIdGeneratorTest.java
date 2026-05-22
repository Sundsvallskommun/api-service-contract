package se.sundsvall.contract.integration.db.model.generator;

import jakarta.persistence.PersistenceException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.StatementPreparer;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
class ContractIdGeneratorTest {

	@Mock
	private SharedSessionContractImplementor mockSession;

	@Mock
	private Object mockCurrentValue;

	@Mock
	private StatementPreparer mockStatementPreparer;

	@Mock
	private JdbcCoordinator mockJdbcCoordinator;

	@Mock
	private ResultSet mockGeneratedKeys;

	@Mock
	private PreparedStatement mockPreparedStatement;

	private static final String UPSERT_COUNTER_QUERY = """
		INSERT INTO contract_id_counter (`year`, last_value)
		VALUES (?, LAST_INSERT_ID(1))
		ON DUPLICATE KEY UPDATE last_value = LAST_INSERT_ID(last_value + 1)
		""";

	private final int currentYear = Year.now().getValue();

	@InjectMocks
	private ContractIdGenerator contractIdGenerator;

	@BeforeEach
	void setUp() throws SQLException {
		when(mockSession.getJdbcCoordinator()).thenReturn(mockJdbcCoordinator);
		when(mockJdbcCoordinator.getStatementPreparer()).thenReturn(mockStatementPreparer);
		when(mockStatementPreparer.prepareStatement(UPSERT_COUNTER_QUERY, Statement.RETURN_GENERATED_KEYS)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
		when(mockGeneratedKeys.next()).thenReturn(true);
		when(mockGeneratedKeys.getLong(1)).thenReturn(1L);
	}

	@Test
	void testGenerate() throws SQLException {
		String generatedString = contractIdGenerator.generate(mockSession, null, null, EventType.INSERT).toString();
		assertThat(generatedString).isEqualTo("%d-000001".formatted(currentYear));

		verify(mockSession).getJdbcCoordinator();
		verify(mockJdbcCoordinator).getStatementPreparer();
		verify(mockStatementPreparer).prepareStatement(UPSERT_COUNTER_QUERY, Statement.RETURN_GENERATED_KEYS);
		verify(mockPreparedStatement).setInt(1, currentYear);
		verify(mockPreparedStatement).executeUpdate();
		verify(mockPreparedStatement).getGeneratedKeys();
		verify(mockGeneratedKeys).next();
		verify(mockGeneratedKeys).getLong(1);
	}

	@Test
	void testGenerate_throwsException() throws SQLException {
		when(mockGeneratedKeys.next()).thenReturn(false);
		assertThatExceptionOfType(PersistenceException.class)
			.isThrownBy(() -> contractIdGenerator.generate(mockSession, null, null, EventType.INSERT))
			.withMessage("Contract id generation failed");
	}

	@Test
	void testGenerate_currentValueNotNull() {
		when(mockCurrentValue.toString()).thenReturn("2024-00001");
		String generatedString = contractIdGenerator.generate(mockSession, null, mockCurrentValue, EventType.INSERT).toString();
		assertThat(generatedString).isEqualTo("2024-00001");
	}

	@Test
	void testGenerate_currentValueBlank() {
		when(mockCurrentValue.toString()).thenReturn("   "); // blank — should fall through to the counter table
		String generatedString = contractIdGenerator.generate(mockSession, null, mockCurrentValue, EventType.INSERT).toString();
		assertThat(generatedString).isEqualTo("%d-000001".formatted(currentYear));
	}
}
