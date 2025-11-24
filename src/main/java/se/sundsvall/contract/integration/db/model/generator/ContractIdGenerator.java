package se.sundsvall.contract.integration.db.model.generator;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.EnumSet;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public class ContractIdGenerator implements BeforeExecutionGenerator {

	private static final long serialVersionUID = -1562799557423722146L;
	private static final String GENERATE_ID_QUERY = "SELECT CONCAT(YEAR(CURRENT_DATE), '-', LPAD(NEXT VALUE FOR `contract_id_seq`, 5, 0))";

	@Override
	public Object generate(final SharedSessionContractImplementor session, final Object owner, final Object currentValue, final EventType eventType) {
		// Don't update if already set and non-blank
		if (currentValue != null && StringUtils.isNotBlank(currentValue.toString())) {
			return currentValue;
		}

		final var statementPreparer = session.getJdbcCoordinator().getStatementPreparer();

		try (var ps = statementPreparer.prepareStatement(GENERATE_ID_QUERY)) {
			final var resultSet = ps.executeQuery();

			if (!resultSet.next()) {
				throw new SQLException("Unable to generate contract id");
			}

			return resultSet.getString(1);
		} catch (SQLException e) {
			throw new PersistenceException("Contract id generation failed", e);
		}
	}

	@Override
	public EnumSet<EventType> getEventTypes() {
		return INSERT_ONLY;
	}
}
