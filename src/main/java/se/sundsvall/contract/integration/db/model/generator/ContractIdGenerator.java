package se.sundsvall.contract.integration.db.model.generator;

import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import java.util.EnumSet;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

/**
 * Hibernate id generator that produces contract ids in the format {@code YYYY-NNNNNN}
 * by upserting a per-year row in {@code contract_id_counter}. The year boundary is
 * crossed implicitly when the first allocation of a new year inserts a fresh row,
 * so no scheduled reset job is needed.
 */
public final class ContractIdGenerator implements BeforeExecutionGenerator {

	private static final long serialVersionUID = 7772903731355896113L;

	private static final String UPSERT_COUNTER_QUERY = """
		INSERT INTO contract_id_counter (`year`, last_value)
		VALUES (?, LAST_INSERT_ID(1))
		ON DUPLICATE KEY UPDATE last_value = LAST_INSERT_ID(last_value + 1)
		""";

	/**
	 * Generates a contract id from the per-year counter, or returns the current value if already set.
	 *
	 * @param  session      the Hibernate session
	 * @param  owner        the entity instance
	 * @param  currentValue the current id value, if any
	 * @param  eventType    the event type triggering generation
	 * @return              the generated or existing contract id
	 */
	@Override
	public Object generate(final SharedSessionContractImplementor session, final Object owner, final Object currentValue, final EventType eventType) {
		// Don't update if already set and non-blank
		if (currentValue != null && StringUtils.isNotBlank(currentValue.toString())) {
			return currentValue;
		}

		final var year = Year.now().getValue();
		final var statementPreparer = session.getJdbcCoordinator().getStatementPreparer();

		// Single-roundtrip allocation: the UPSERT publishes the new counter
		// via LAST_INSERT_ID, which Connector/J surfaces through
		// getGeneratedKeys without a follow-up SELECT.
		try (var ps = statementPreparer.prepareStatement(UPSERT_COUNTER_QUERY, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, year);
			ps.executeUpdate();
			try (var keys = ps.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to generate contract id");
				}
				return "%d-%06d".formatted(year, keys.getLong(1));
			}
		} catch (SQLException e) {
			throw new PersistenceException("Contract id generation failed", e);
		}
	}

	/**
	 * Returns the event types that trigger id generation.
	 *
	 * @return an enum set containing only the {@code INSERT} event type
	 */
	@Override
	public EnumSet<EventType> getEventTypes() {
		return INSERT_ONLY;
	}
}
