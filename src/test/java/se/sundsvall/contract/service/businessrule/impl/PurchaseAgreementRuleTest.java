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
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.I_AM_A_TEAPOT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.model.enums.ContractType;

@ExtendWith(MockitoExtension.class)
class PurchaseAgreementRuleTest {

	@Mock
	private ContractEntity contractEntityMock;

	private final PurchaseAgreementRule rule = new PurchaseAgreementRule();

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

		final var result = rule.apply(contractEntityMock);

		assertThat(result).isTrue();
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

		when(contractEntityMock.getContractId()).thenReturn(contractId);
		doThrow(Problem.valueOf(I_AM_A_TEAPOT)).when(contractEntityMock).setLeaseDuration(null);

		final var e = assertThrows(ThrowableProblem.class, () -> rule.apply(contractEntityMock));

		assertThat(e.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);
		assertThat(e.getDetail()).isEqualTo("An exception occurred when applying purchase agreement business rules for contract number %s".formatted(contractId));
		verify(contractEntityMock, times(3)).getContractId();
		verify(contractEntityMock).setLeaseDuration(null);
		verifyNoMoreInteractions(contractEntityMock);
	}
}
