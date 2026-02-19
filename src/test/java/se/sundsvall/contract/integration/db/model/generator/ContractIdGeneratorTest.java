package se.sundsvall.contract.integration.db.model.generator;

import jakarta.persistence.PersistenceException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.StatementPreparer;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
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
	private ResultSet mockResultSet;

	@Mock
	private PreparedStatement mockPreparedStatement;

	private static final String GENERATE_ID_QUERY = "SELECT CONCAT(YEAR(CURRENT_DATE), '-', LPAD(NEXT VALUE FOR `contract_id_seq`, 5, 0))";

	@InjectMocks
	private ContractIdGenerator contractIdGenerator;

	@BeforeEach
	void setUp() throws SQLException {
		when(mockSession.getJdbcCoordinator()).thenReturn(mockJdbcCoordinator);
		when(mockJdbcCoordinator.getStatementPreparer()).thenReturn(mockStatementPreparer);
		when(mockStatementPreparer.prepareStatement(GENERATE_ID_QUERY)).thenReturn(mockPreparedStatement);
		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(true);
		when(mockResultSet.getString(1)).thenReturn("2024-00001");
	}

	@Test
	void testGenerate() throws SQLException {
		String generatedString = contractIdGenerator.generate(mockSession, null, null, EventType.INSERT).toString();
		assertThat(generatedString).isEqualTo("2024-00001");

		verify(mockSession).getJdbcCoordinator();
		verify(mockJdbcCoordinator).getStatementPreparer();
		verify(mockStatementPreparer).prepareStatement(GENERATE_ID_QUERY);
		verify(mockPreparedStatement).executeQuery();
		verify(mockResultSet).next();
		verify(mockResultSet).getString(1);
	}

	@Test
	void testGenerate_throwsException() throws SQLException {
		when(mockResultSet.next()).thenReturn(false);
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
}
