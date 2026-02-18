package se.sundsvall.contract.service.businessrule;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.contract.model.enums.IntervalType.YEARLY;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.Status.DRAFT;
import static se.sundsvall.contract.model.enums.Status.TERMINATED;

class ContractUtilityTest {

	@ParameterizedTest(name = "{0}")
	@MethodSource("isBillableArgumentProvider")
	void isBillable(String description, ContractEntity contractEntity, boolean expectedResult) {
		assertThat(ContractUtility.isBillable(contractEntity)).isEqualTo(expectedResult);
	}

	private static Stream<Arguments> isBillableArgumentProvider() {
		return Stream.of(
			Arguments.of("Contract is draft",
				ContractEntity.builder().withStatus(DRAFT).build(), false),
			Arguments.of("Contract is terminated",
				ContractEntity.builder().withStatus(TERMINATED).build(), false),
			Arguments.of("Contract is active but has no invoicing object",
				ContractEntity.builder().withStatus(ACTIVE).build(), false),
			Arguments.of("Contract is active with invoicing object but has no invoicing interval",
				ContractEntity.builder().withStatus(ACTIVE).withInvoicing(InvoicingEmbeddable.builder().build()).build(), false),
			Arguments.of("Contract is active with invoicing object and has invoicing interval",
				ContractEntity.builder().withStatus(ACTIVE).withInvoicing(InvoicingEmbeddable.builder().withInvoiceInterval(YEARLY).build()).build(), true));

	}
}
