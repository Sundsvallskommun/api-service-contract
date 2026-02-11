package se.sundsvall.contract.service.businessrule.impl;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.model.enums.IntervalType.MONTHLY;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.service.businessrule.model.Action.CREATE;
import static se.sundsvall.contract.service.businessrule.model.Action.DELETE;
import static se.sundsvall.contract.service.businessrule.model.Action.UPDATE;

import generated.se.sundsvall.billingdatacollector.ScheduledBilling;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.billingdatacollector.BillingDataCollectorIntegration;
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

	@Mock
	private BillingDataCollectorIntegration bdlIntegrationMock;

	@InjectMocks
	private BillableAgreementRule rule;

	@AfterEach
	void verifyNoMoreMockInteractions() {
		verifyNoMoreInteractions(
			contractEntityMock,
			invoicingEmbeddableMock,
			billableAgreementRuleConfigurationMock,
			bdlIntegrationMock);
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
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Action can not be null");
		verify(contractEntityMock).getContractId();
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void applyBusinessrulesOnCreateSuccessful(boolean isBillable) {
		// Arrange
		final var municipalityId = "municipalityId";
		final var contractId = "contractId";
		if (isBillable) {
			when(contractEntityMock.getMunicipalityId()).thenReturn(municipalityId);
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
			verify(contractEntityMock, times(3)).getContractId();
			verify(contractEntityMock, times(2)).getInvoicing();
			verify(invoicingEmbeddableMock, times(2)).getInvoiceInterval();
			verify(contractEntityMock).getMunicipalityId();
			verify(bdlIntegrationMock).addBillingCycle(eq(municipalityId), eq(contractId), any(ScheduledBilling.class));
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void applyBusinessrulesOnUpdateSuccessful(boolean isBillable) {
		// Arrange
		final var municipalityId = "municipalityId";
		final var contractId = "contractId";
		when(contractEntityMock.getMunicipalityId()).thenReturn(municipalityId);
		when(contractEntityMock.getContractId()).thenReturn(contractId);
		if (isBillable) {
			when(contractEntityMock.getStatus()).thenReturn(ACTIVE);
			when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);
			when(invoicingEmbeddableMock.getInvoiceInterval()).thenReturn(QUARTERLY);
		}

		// Act
		rule.apply(new BusinessruleParameters(contractEntityMock, UPDATE));

		// Assert and verify
		verify(contractEntityMock).getStatus();
		verify(contractEntityMock).getMunicipalityId();
		if (isBillable) {
			verify(contractEntityMock, times(3)).getContractId();
			verify(contractEntityMock, times(2)).getInvoicing();
			verify(invoicingEmbeddableMock, times(2)).getInvoiceInterval();
			verify(bdlIntegrationMock).addBillingCycle(eq(municipalityId), eq(contractId), any(ScheduledBilling.class));
		} else {
			verify(contractEntityMock, times(2)).getContractId();
			verify(bdlIntegrationMock).removeBillingCycle(municipalityId, contractId);
		}
	}

	@Test
	void applyBusinessrulesOnDeleteSuccessful() {
		// Arrange
		final var municipalityId = "municipalityId";
		final var contractId = "contractId";

		when(contractEntityMock.getMunicipalityId()).thenReturn(municipalityId);
		when(contractEntityMock.getContractId()).thenReturn(contractId);

		// Act
		rule.apply(new BusinessruleParameters(contractEntityMock, DELETE));

		// Assert and verify
		verify(contractEntityMock).getMunicipalityId();
		verify(contractEntityMock, times(2)).getContractId();
		verify(bdlIntegrationMock).removeBillingCycle(municipalityId, contractId);
	}

	@Test
	void applyBusinessrulesFailWithNoAction() {
		// Arrange
		final var businessParameters = new BusinessruleParameters(contractEntityMock, null);
		final var contractId = "contractId";
		when(contractEntityMock.getContractId()).thenReturn(contractId);

		// Act
		final var e = assertThrows(BusinessruleException.class, () -> rule.apply(businessParameters));

		// Assert
		assertThat(e.getCause().getClass()).isEqualTo(IllegalArgumentException.class);
		assertThat(e.getCause().getMessage()).isEqualTo("Action can not be null");
		assertThat(e.getMessage()).isEqualTo("An exception occurred when applying billable agreement business rules for contract number %s".formatted(contractId));
	}

	@Test
	void applyBusinessrulesFail() {
		// Arrange
		final var municipalityId = "municipalityId";
		final var contractId = "contractId";
		final var thrownException = new NullPointerException("I am a teapot");
		final var businessParameters = new BusinessruleParameters(contractEntityMock, CREATE);
		when(contractEntityMock.getMunicipalityId()).thenReturn(municipalityId);
		when(contractEntityMock.getContractId()).thenReturn(contractId);
		when(contractEntityMock.getStatus()).thenReturn(ACTIVE);
		when(contractEntityMock.getInvoicing()).thenReturn(invoicingEmbeddableMock);
		when(invoicingEmbeddableMock.getInvoiceInterval()).thenReturn(MONTHLY);
		doThrow(thrownException).when(bdlIntegrationMock).addBillingCycle(eq(municipalityId), eq(contractId), any(ScheduledBilling.class));

		// Act
		final var e = assertThrows(BusinessruleException.class, () -> rule.apply(businessParameters));

		assertThat(e.getCause()).isSameAs(thrownException);
		assertThat(e.getMessage()).isEqualTo("An exception occurred when applying billable agreement business rules for contract number %s".formatted(contractId));
		verify(contractEntityMock, times(4)).getContractId();
		verify(contractEntityMock, times(2)).getInvoicing();
		verify(invoicingEmbeddableMock, times(2)).getInvoiceInterval();
		verify(contractEntityMock).getMunicipalityId();
		verify(bdlIntegrationMock).addBillingCycle(eq(municipalityId), eq(contractId), any(ScheduledBilling.class));
	}
}
