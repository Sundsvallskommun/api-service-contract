package se.sundsvall.contract.service.businessrule.impl;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.model.enums.IntervalType.HALF_YEARLY;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.service.businessrule.model.Action.CREATE;
import static se.sundsvall.contract.service.businessrule.model.Action.DELETE;
import static se.sundsvall.contract.service.businessrule.model.Action.UPDATE;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.InvoicingEmbeddable;
import se.sundsvall.contract.service.businessrule.configuration.BillableAgreementRuleConfiguration;
import se.sundsvall.contract.service.businessrule.model.BusinessruleException;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;

@ExtendWith(MockitoExtension.class)
class BillableAgreementRuleTest {

	@Mock
	private ContractEntity contractEntityMock;

	@Mock
	private InvoicingEmbeddable invoicingEmbeddableMock;

	@Mock
	private BillableAgreementRuleConfiguration billableAgreementRuleConfigurationMock;

	private BillableAgreementRule rule;

	@BeforeEach
	void setup() {
		rule = new BillableAgreementRule(billableAgreementRuleConfigurationMock);
	}

	@Test
	void appliesToMatch() {
		// Arrange
		final var municipalityId = "municipalityId";
		when(billableAgreementRuleConfigurationMock.managedMunicipalityIds()).thenReturn(List.of(municipalityId));
		when(contractEntityMock.getMunicipalityId()).thenReturn(municipalityId);

		// Act
		final var result = rule.appliesTo(contractEntityMock);

		// Assert and verify
		assertThat(result).isTrue();
		verify(contractEntityMock).getMunicipalityId();
		verify(billableAgreementRuleConfigurationMock).managedMunicipalityIds();
		verifyNoMoreInteractions(contractEntityMock, billableAgreementRuleConfigurationMock);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("appliesToNonMatchArgumentProvider")
	void appliesToNonMatch(String description, List<String> managedMunicipalityIds) {
		// Arrange
		final var municipalityId = "nonMatchingMunicipalityId";
		when(billableAgreementRuleConfigurationMock.managedMunicipalityIds()).thenReturn(managedMunicipalityIds);
		when(contractEntityMock.getMunicipalityId()).thenReturn(municipalityId);

		// Act
		final var result = rule.appliesTo(contractEntityMock);

		// Assert and verify
		assertThat(result).isFalse();
		verify(contractEntityMock).getMunicipalityId();
		verify(billableAgreementRuleConfigurationMock).managedMunicipalityIds();
		verifyNoMoreInteractions(contractEntityMock, billableAgreementRuleConfigurationMock);
	}

	private static Stream<Arguments> appliesToNonMatchArgumentProvider() {
		return Stream.of(
			Arguments.of("List with managedMunicipalityIds is null", null),
			Arguments.of("List with managedMunicipalityIds is empty", emptyList()),
			Arguments.of("List with managedMunicipalityIds contains no match", List.of("municipalityId")));
	}

	@Test
	void applyBusinessrulesWithNullAction() {
		// Arrange
		final var contractId = "contractId";
		when(contractEntityMock.getContractId()).thenReturn(contractId);
		final var parameters = new BusinessruleParameters(contractEntityMock, null);

		// Act
		final var e = assertThrows(BusinessruleException.class, () -> rule.apply(parameters));

		// Assert and verify
		assertThat(e.getMessage()).isEqualTo("An exception occurred when applying billable agreement business rules for contract number %s".formatted(contractId));
		assertThat(e.getCause().getClass()).isEqualTo(NullPointerException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Action can not be null");
		verify(contractEntityMock).getContractId();
		verifyNoMoreInteractions(contractEntityMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void applyBusinessrulesOnCreateSuccessful(boolean isBillable) {
		// Arrange
		final var contractId = "contractId";
		if (isBillable) {
			when(contractEntityMock.getContractId()).thenReturn(contractId);
			when(contractEntityMock.getStatus()).thenReturn(ACTIVE);
			when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);
			when(invoicingEmbeddableMock.getInvoiceInterval()).thenReturn(MONTHLY);
		}

		// Act
		rule.apply(new BusinessruleParameters(contractEntityMock, CREATE));

		// Assert and verify
		verify(contractEntityMock).getStatus();
		if (isBillable) {
			verify(contractEntityMock).getInvoicing();
			verify(invoicingEmbeddableMock).getInvoiceInterval();
			verify(contractEntityMock).getContractId();
		}
		// TODO: Verify mock interactions in story DRAKEN-3066 when BDC-integration is in place
		verifyNoMoreInteractions(contractEntityMock, invoicingEmbeddableMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void applyBusinessrulesOnUpdateSuccessful(boolean isBillable) {
		// Arrange
		final var contractId = "contractId";
		if (isBillable) {
			when(contractEntityMock.getContractId()).thenReturn(contractId);
			when(contractEntityMock.getStatus()).thenReturn(ACTIVE);
			when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);
			when(invoicingEmbeddableMock.getInvoiceInterval()).thenReturn(QUARTERLY);
		}

		// Act
		rule.apply(new BusinessruleParameters(contractEntityMock, UPDATE));

		// Assert and verify
		verify(contractEntityMock).getStatus();
		verify(contractEntityMock).getContractId();
		if (isBillable) {
			verify(contractEntityMock).getInvoicing();
			verify(invoicingEmbeddableMock).getInvoiceInterval();
		}
		// TODO: Verify mock interactions in story DRAKEN-3066 when BDC-integration is in place
		verifyNoMoreInteractions(contractEntityMock, invoicingEmbeddableMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void applyBusinessrulesOnDeleteSuccessful(boolean isBillable) {
		// Arrange
		final var contractId = "contractId";
		if (isBillable) {
			when(contractEntityMock.getContractId()).thenReturn(contractId);
			when(contractEntityMock.getStatus()).thenReturn(ACTIVE);
			when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);
			when(invoicingEmbeddableMock.getInvoiceInterval()).thenReturn(HALF_YEARLY);
		}

		// Act
		rule.apply(new BusinessruleParameters(contractEntityMock, DELETE));

		// Assert and verify
		verify(contractEntityMock).getStatus();
		if (isBillable) {
			verify(contractEntityMock).getInvoicing();
			verify(invoicingEmbeddableMock).getInvoiceInterval();
			verify(contractEntityMock).getContractId();
		}
		// TODO: Verify mock interactions in story DRAKEN-3066 when BDC-integration is in place
		verifyNoMoreInteractions(contractEntityMock, invoicingEmbeddableMock);
	}

	@Test
	void applyBusinessrulesFail() {
		// TODO: Implement in story DRAKEN-3066 when BDC-integration is in place
	}
}
