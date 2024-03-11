package se.sundsvall.contract.integration.db.model.generator;

import java.util.Properties;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class ContractIdGenerator implements IdentifierGenerator {

    private static final String CREATE_SEQUENCE_QUERY_TEMPLATE = """
        CREATE SEQUENCE IF NOT EXISTS `%s` START WITH 1 INCREMENT BY 1
        """;
    private static final String GENERATE_ID_QUERY_TEMPLATE = """
        SELECT CONCAT(YEAR(CURRENT_DATE), '-', LPAD(NEXT VALUE FOR `%s`, %d, 0))
        """;

    private String createSequenceQuery;
    private String generateIdQuery;

    @Override
    public Object generate(final SharedSessionContractImplementor session, final Object obj) {
        session.createNativeMutationQuery(createSequenceQuery).executeUpdate();

        return session.createNativeQuery(generateIdQuery, String.class).getSingleResult();
    }

    @Override
    public void configure(final Type type, final Properties properties,
            final ServiceRegistry serviceRegistry) {
        var sequenceName = properties.getProperty("sequenceName", "contract_id_seq");
        var length = Integer.parseInt(properties.getProperty("length", "5"));

        createSequenceQuery = CREATE_SEQUENCE_QUERY_TEMPLATE.formatted(sequenceName);
        generateIdQuery = GENERATE_ID_QUERY_TEMPLATE.formatted(sequenceName, length);
    }
}
