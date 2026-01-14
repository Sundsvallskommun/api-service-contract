package se.sundsvall.contract.service.businessrule.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.service.businessrule.model.BusinessruleException;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;

@ExtendWith(MockitoExtension.class)
class PurchaseAgreementRuleTest {

	@Mock
	private ContractEntity contractEntityMock;

	@InjectMocks
	private PurchaseAgreementRule rule;

	@ParameterizedTest
	@EnumSource(value = ContractType.class, names = {
		"PURCHASE_AGREEMENT"
	}, mode = INCLUDE)
	void appliesToMatch(ContractType contractType) {
		when(contractEntityMock.getType()).thenReturn(contractType);

		final var result = rule.appliesTo(contractEntityMock);

		assertThat(result).isTrue();
		verify(contractEntityMock).getType();
		verifyNoMoreInteractions(contractEntityMock);
	}

	@ParameterizedTest
	@EnumSource(value = ContractType.class, names = {
		"PURCHASE_AGREEMENT"
	}, mode = EXCLUDE)
	@NullSource
	void appliesToNonMatch(ContractType contractType) {
		when(contractEntityMock.getType()).thenReturn(contractType);

		final var result = rule.appliesTo(contractEntityMock);

		assertThat(result).isFalse();
		verify(contractEntityMock).getType();
		verifyNoMoreInteractions(contractEntityMock);
	}

	@Test
	void applyBusinessrulesSuccessful() {
		final var contractId = "contractId";

		when(contractEntityMock.getContractId()).thenReturn(contractId);

		rule.apply(new BusinessruleParameters(contractEntityMock, null));

		verify(contractEntityMock).getContractId();
		verify(contractEntityMock).setLeaseDuration(null);
		verify(contractEntityMock).setLeaseDurationUnit(null);
		verify(contractEntityMock).setLeaseExtension(null);
		verify(contractEntityMock).setLeaseExtensionUnit(null);
		verify(contractEntityMock).setAutoExtend(null);
		verifyNoMoreInteractions(contractEntityMock);
	}

	@Test
	void applyBusinessrulesFail() {
		final var contractId = "contractId";
		final var thrownException = new NullPointerException("I am a teapot");
		final var businessParameters = new BusinessruleParameters(contractEntityMock, null);
		when(contractEntityMock.getContractId()).thenReturn(contractId);
		doThrow(thrownException).when(contractEntityMock).setLeaseDuration(null);

		final var e = assertThrows(BusinessruleException.class, () -> rule.apply(businessParameters));

		assertThat(e.getCause()).isSameAs(thrownException);
		assertThat(e.getMessage()).isEqualTo("An exception occurred when applying purchase agreement business rules for contract number %s".formatted(contractId));
		verify(contractEntityMock, times(2)).getContractId();
		verify(contractEntityMock).setLeaseDuration(null);
		verifyNoMoreInteractions(contractEntityMock);
	}
}
