package se.sundsvall.contract.service;

import com.deblock.jsondiff.matcher.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.projection.ContractVersionProjection;
import se.sundsvall.contract.model.Change;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.model.Action;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;
import se.sundsvall.contract.service.diff.Differ;
import se.sundsvall.dept44.problem.ThrowableProblem;
import tools.jackson.databind.node.StringNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createContractEntity;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	@Mock
	private ContractVersionProjection contractVersionProjectionMock;

	@Mock
	private ContractRepository contractRepositoryMock;

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@Mock
	private Differ differMock;

	@Mock
	private Businessrule businessruleMock;

	@Captor
	private ArgumentCaptor<BusinessruleParameters> businessruleParametersCaptor;

	private ContractService contractService;

	@BeforeEach
	void initialize() {
		contractService = new ContractService(contractRepositoryMock, attachmentRepositoryMock, List.of(businessruleMock), differMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void createContract(boolean match) {

		// Arrange
		final var contract = Contract.builder()
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY)
				.withInvoicedIn(ARREARS)
				.build())
			.withStatus(ACTIVE)
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		when(businessruleMock.appliesTo(any(ContractEntity.class))).thenReturn(match);
		when(contractRepositoryMock.save(any(ContractEntity.class)))
			.thenReturn(ContractEntity.builder().withContractId(CONTRACT_ID).build());

		// ACt
		final var result = contractService.createContract(MUNICIPALITY_ID, contract);

		// Assert
		assertThat(result).isEqualTo(CONTRACT_ID);
		verify(businessruleMock).appliesTo(any(ContractEntity.class));
		if (match) {
			verify(businessruleMock).apply(businessruleParametersCaptor.capture());
			assertThat(businessruleParametersCaptor.getValue()).satisfies(businessruleParameters -> {
				assertThat(businessruleParameters.contractEntity()).isNotNull();
				assertThat(businessruleParameters.action()).isEqualTo(Action.CREATE);
			});
		}
		verify(contractRepositoryMock, times(2)).save(any(ContractEntity.class));
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock);
	}

	@Test
	void getContract() {
		// Arrange
		final var landLeaseContractEntity = createContractEntity();
		when(contractRepositoryMock.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(Optional.of(landLeaseContractEntity));

		// Act
		final var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID, null);

		// Assert
		assertThat(result).isNotNull();

		verify(contractRepositoryMock).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void getContractWithSpecificVersion() {
		final var landLeaseContractEntity = createContractEntity();
		when(contractRepositoryMock.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, 2))
			.thenReturn(Optional.of(landLeaseContractEntity));

		// Act
		final var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID, 2);

		// Assert
		assertThat(result).isNotNull();

		verify(contractRepositoryMock).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, 2);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void getContractShouldThrow404WhenNoMatch() {
		// Arrange
		when(contractRepositoryMock.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID, null));

		verify(contractRepositoryMock).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void getPaginatedContracts() {
		// Arrange
		final var landLeaseContractEntity = createContractEntity();
		final var pageable = PageRequest.of(0, 5);

		final Page<ContractEntity> page = new PageImpl<>(List.of(landLeaseContractEntity, landLeaseContractEntity), pageable, 2);

		when(contractRepositoryMock.findAll(Mockito.<Specification<ContractEntity>>any(), any(Pageable.class))).thenReturn(page);
		when(attachmentRepositoryMock.findAllByMunicipalityIdAndContractId(eq(MUNICIPALITY_ID), any(String.class)))
			.thenReturn(List.of(createAttachmentEntity()));

		// Act
		final var result = contractService.getContracts(MUNICIPALITY_ID, null, pageable);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getNumber()).isZero();
		assertThat(result.getSize()).isEqualTo(5);
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getTotalPages()).isOne();

		verify(contractRepositoryMock).findAll(Mockito.<Specification<ContractEntity>>any(), any(Pageable.class));
		verify(attachmentRepositoryMock, times(2)).findAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, "2024-98765");
		verifyNoMoreInteractions(contractRepositoryMock, attachmentRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void updateContract(boolean match) {
		// Arrange
		final var landLeaseContractEntity = createContractEntity();
		when(contractRepositoryMock.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(any(String.class), any(String.class))).thenReturn(Optional.of(landLeaseContractEntity));
		when(businessruleMock.appliesTo(any(ContractEntity.class))).thenReturn(match);

		// Act
		contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, TestFactory.createContract());

		// Assert
		verify(contractRepositoryMock).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verify(businessruleMock).appliesTo(any(ContractEntity.class));
		if (match) {
			verify(businessruleMock).apply(businessruleParametersCaptor.capture());
			assertThat(businessruleParametersCaptor.getValue()).satisfies(businessruleParameters -> {
				assertThat(businessruleParameters.contractEntity()).isNotNull();
				assertThat(businessruleParameters.action()).isEqualTo(Action.UPDATE);
			});
		}
		verify(contractRepositoryMock).save(any(ContractEntity.class));
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock);
	}

	@Test
	void updateContractShouldThrow404WhenNoMatch() {
		final var contract = TestFactory.createContract();
		when(contractRepositoryMock.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(any(String.class), any(String.class)))
			.thenReturn(Optional.empty());

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, contract))
			.satisfies(thrownProblem -> assertThat(thrownProblem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));

		verify(contractRepositoryMock).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void diffContract() {

		final var oldVersion = 2;
		final var newVersion = 3;
		final var landLeaseContractEntity = createContractEntity();

		final var p1 = mock(ContractVersionProjection.class);
		final var p2 = mock(ContractVersionProjection.class);
		final var p3 = mock(ContractVersionProjection.class);

		when(p1.getVersion()).thenReturn(1);
		when(p2.getVersion()).thenReturn(2);
		when(p3.getVersion()).thenReturn(3);

		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID, Sort.by("version").ascending()))
			.thenReturn(List.of(p1, p2, p3));
		when(contractRepositoryMock.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, oldVersion))
			.thenReturn(Optional.of(landLeaseContractEntity));
		when(contractRepositoryMock.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, newVersion))
			.thenReturn(Optional.of(landLeaseContractEntity));
		when(differMock.diff(any(Contract.class), any(Contract.class), anyList()))
			.thenReturn(List.of(Change.modification(new Path(), new StringNode("oldValue"), new StringNode("newValue"))));

		final var diff = contractService.diffContract(MUNICIPALITY_ID, CONTRACT_ID, oldVersion, newVersion);

		assertThat(diff.oldVersion()).isEqualTo(oldVersion);
		assertThat(diff.newVersion()).isEqualTo(newVersion);
		assertThat(diff.changes()).isNotNull().hasSize(1);
		assertThat(diff.availableVersions()).containsExactly(1, 2, 3);

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID, Sort.by("version").ascending());
		verify(contractRepositoryMock).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, oldVersion);
		verify(contractRepositoryMock).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, newVersion);
		verify(differMock).diff(any(Contract.class), any(Contract.class), anyList());
		verifyNoMoreInteractions(contractRepositoryMock, differMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void diffContractWithSameVersion() {

		final var version = 2;
		final var landLeaseContractEntity = createContractEntity();

		final var p1 = mock(ContractVersionProjection.class);
		final var p2 = mock(ContractVersionProjection.class);

		when(p1.getVersion()).thenReturn(1);
		when(p2.getVersion()).thenReturn(2);

		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID, Sort.by("version").ascending()))
			.thenReturn(List.of(p1, p2));
		when(contractRepositoryMock.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, version))
			.thenReturn(Optional.of(landLeaseContractEntity));
		when(differMock.diff(any(Contract.class), any(Contract.class), anyList()))
			.thenReturn(List.of());

		final var diff = contractService.diffContract(MUNICIPALITY_ID, CONTRACT_ID, version, version);

		assertThat(diff.oldVersion()).isEqualTo(version);
		assertThat(diff.newVersion()).isEqualTo(version);
		assertThat(diff.changes()).isEmpty();

		verify(contractRepositoryMock, times(2)).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, version);
	}

	@Test
	void diffContractWithReversedVersionOrder() {

		final var oldVersion = 3;
		final var newVersion = 1;
		final var landLeaseContractEntity = createContractEntity();

		final var p1 = mock(ContractVersionProjection.class);
		final var p2 = mock(ContractVersionProjection.class);
		final var p3 = mock(ContractVersionProjection.class);

		when(p1.getVersion()).thenReturn(1);
		when(p2.getVersion()).thenReturn(2);
		when(p3.getVersion()).thenReturn(3);

		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID, Sort.by("version").ascending()))
			.thenReturn(List.of(p1, p2, p3));
		when(contractRepositoryMock.findByMunicipalityIdAndContractIdAndVersion(eq(MUNICIPALITY_ID), eq(CONTRACT_ID), any(Integer.class)))
			.thenReturn(Optional.of(landLeaseContractEntity));
		when(differMock.diff(any(Contract.class), any(Contract.class), anyList()))
			.thenReturn(List.of());

		final var diff = contractService.diffContract(MUNICIPALITY_ID, CONTRACT_ID, oldVersion, newVersion);

		assertThat(diff.oldVersion()).isEqualTo(oldVersion);
		assertThat(diff.newVersion()).isEqualTo(newVersion);

		verify(contractRepositoryMock).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, oldVersion);
		verify(contractRepositoryMock).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, newVersion);
	}

	@Test
	void diffContractWithSingleVersionThrowsBadRequest() {

		final var p1 = mock(ContractVersionProjection.class);
		when(p1.getVersion()).thenReturn(1);

		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID, Sort.by("version").ascending()))
			.thenReturn(List.of(p1));

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.diffContract(MUNICIPALITY_ID, CONTRACT_ID, null, null))
			.satisfies(thrownProblem -> assertThat(thrownProblem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID, Sort.by("version").ascending());
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(differMock, businessruleMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void deleteContract(boolean match) {
		// Arrange
		when(contractRepositoryMock.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.of(ContractEntity.builder()
			.withContractId(CONTRACT_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.build()));
		when(businessruleMock.appliesTo(any(ContractEntity.class))).thenReturn(match);

		// Act
		contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID);

		// Assert and verify
		verify(businessruleMock).appliesTo(any(ContractEntity.class));
		if (match) {
			verify(businessruleMock).apply(businessruleParametersCaptor.capture());
			assertThat(businessruleParametersCaptor.getValue()).satisfies(businessruleParameters -> {
				assertThat(businessruleParameters.contractEntity()).isNotNull();
				assertThat(businessruleParameters.action()).isEqualTo(Action.DELETE);
			});
		}
		verify(contractRepositoryMock).deleteAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock);
	}

	@Test
	void deleteContractShouldThrow404WhenNoMatch() {
		// Arrange
		when(contractRepositoryMock.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(contractRepositoryMock).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	/**
	 * Test class used to mock the ContractTypeRuleInterface
	 */
	private static class Businessrule implements BusinessruleInterface {
		@Override
		public boolean appliesTo(ContractEntity contractEntity) {
			return false;
		}

		@Override
		public void apply(BusinessruleParameters parameters) {
			// Empty method as this class only is used as a mock in this test class
		}
	}
}
