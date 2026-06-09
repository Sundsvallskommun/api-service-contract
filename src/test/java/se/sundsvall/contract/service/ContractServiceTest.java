package se.sundsvall.contract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.PatchContract;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.OutboxEntity;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.service.businessrule.BusinessruleInterface;
import se.sundsvall.contract.service.businessrule.model.Action;
import se.sundsvall.contract.service.businessrule.model.BusinessruleParameters;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
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
	private ContractRepository contractRepositoryMock;

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@Mock
	private se.sundsvall.contract.integration.db.OutboxRepository outboxRepositoryMock;

	@Mock
	private Businessrule businessruleMock;

	@Mock
	private ContractValidator contractValidatorMock;

	@Captor
	private ArgumentCaptor<BusinessruleParameters> businessruleParametersCaptor;

	private ContractService contractService;

	@BeforeEach
	void initialize() {
		final var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		contractService = new ContractService(contractRepositoryMock, attachmentRepositoryMock, outboxRepositoryMock, List.of(businessruleMock), objectMapper, contractValidatorMock);
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
		verify(outboxRepositoryMock).save(any(OutboxEntity.class));
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock, outboxRepositoryMock);
	}

	@Test
	void getContract() {
		// Arrange
		final var landLeaseContractEntity = createContractEntity();
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(Optional.of(landLeaseContractEntity));

		// Act
		final var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID);

		// Assert
		assertThat(result).isNotNull();

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void getContractShouldThrow404WhenNoMatch() {
		// Arrange
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
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
		final var updatedContract = TestFactory.createContract();
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(any(String.class), any(String.class))).thenReturn(Optional.of(landLeaseContractEntity));
		when(businessruleMock.appliesTo(any(ContractEntity.class))).thenReturn(match);

		// Act
		contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, updatedContract);

		// Assert
		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(businessruleMock).appliesTo(any(ContractEntity.class));
		if (match) {
			verify(businessruleMock).apply(businessruleParametersCaptor.capture());
			assertThat(businessruleParametersCaptor.getValue()).satisfies(businessruleParameters -> {
				assertThat(businessruleParameters.contractEntity()).isSameAs(landLeaseContractEntity);
				assertThat(businessruleParameters.action()).isEqualTo(Action.UPDATE);
			});
		}
		verify(contractRepositoryMock).save(landLeaseContractEntity);
		verify(outboxRepositoryMock).save(any(OutboxEntity.class));
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock, outboxRepositoryMock);

		// The update is applied in place onto the managed entity (no new record)
		assertThat(landLeaseContractEntity.getDescription()).isEqualTo(updatedContract.getDescription());
		assertThat(landLeaseContractEntity.getType()).isEqualTo(updatedContract.getType());
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void patchContract(boolean match) {
		// Arrange
		final var existingEntity = createContractEntity();

		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(Optional.of(existingEntity));
		when(businessruleMock.appliesTo(any(ContractEntity.class))).thenReturn(match);

		final var patchPayload = PatchContract.builder()
			.withDescription("a patched description")
			.build();

		// Act
		contractService.patchContract(MUNICIPALITY_ID, CONTRACT_ID, patchPayload);

		// Assert
		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(businessruleMock).appliesTo(any(ContractEntity.class));
		if (match) {
			verify(businessruleMock).apply(businessruleParametersCaptor.capture());
			assertThat(businessruleParametersCaptor.getValue()).satisfies(businessruleParameters -> {
				assertThat(businessruleParameters.contractEntity()).isSameAs(existingEntity);
				assertThat(businessruleParameters.action()).isEqualTo(Action.UPDATE);
			});
		}
		verify(contractRepositoryMock).save(existingEntity);
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock);

		// Patched fields are applied in place
		assertThat(existingEntity.getDescription()).isEqualTo("a patched description");
	}

	@Test
	void patchContractShouldThrow404WhenNoMatch() {
		final var patchPayload = PatchContract.builder()
			.withDescription("a patched description")
			.build();
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(any(String.class), any(String.class)))
			.thenReturn(Optional.empty());

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.patchContract(MUNICIPALITY_ID, CONTRACT_ID, patchPayload))
			.satisfies(thrownProblem -> assertThat(thrownProblem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void updateContractShouldThrow404WhenNoMatch() {
		final var contract = TestFactory.createContract();
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(any(String.class), any(String.class)))
			.thenReturn(Optional.empty());

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, contract))
			.satisfies(thrownProblem -> assertThat(thrownProblem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void createContractAbortsWhenValidationFails() {
		// Arrange
		final var contract = TestFactory.createContract();
		Mockito.doThrow(new ConstraintViolationProblem(HttpStatus.BAD_REQUEST, List.of(new Violation("stakeholders", "boom"))))
			.when(contractValidatorMock).validate(any(ContractEntity.class), any());

		// Act & Assert
		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> contractService.createContract(MUNICIPALITY_ID, contract));

		verify(contractValidatorMock).validate(any(ContractEntity.class), any());
		verify(contractRepositoryMock, Mockito.never()).save(any(ContractEntity.class));
		verifyNoInteractions(contractRepositoryMock, outboxRepositoryMock, businessruleMock);
	}

	@Test
	void updateContractAbortsWhenValidationFails() {
		// Arrange
		final var existingEntity = createContractEntity();
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(Optional.of(existingEntity));
		Mockito.doThrow(new ConstraintViolationProblem(HttpStatus.BAD_REQUEST, List.of(new Violation("fees.indexNumber", "boom"))))
			.when(contractValidatorMock).validate(any(ContractEntity.class), any());

		// Act & Assert
		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, TestFactory.createContract()));

		verify(contractValidatorMock).validate(any(ContractEntity.class), any());
		verify(contractRepositoryMock, Mockito.never()).save(any(ContractEntity.class));
		verifyNoInteractions(outboxRepositoryMock, businessruleMock);
	}

	@Test
	void patchContractAbortsWhenValidationFails() {
		// Arrange
		final var existingEntity = createContractEntity();
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(Optional.of(existingEntity));
		Mockito.doThrow(new ConstraintViolationProblem(HttpStatus.BAD_REQUEST, List.of(new Violation("propertyDesignations", "boom"))))
			.when(contractValidatorMock).validate(any(ContractEntity.class), any());

		final var patchPayload = PatchContract.builder().withDescription("patched").build();

		// Act & Assert
		assertThatExceptionOfType(ConstraintViolationProblem.class)
			.isThrownBy(() -> contractService.patchContract(MUNICIPALITY_ID, CONTRACT_ID, patchPayload));

		verify(contractValidatorMock).validate(any(ContractEntity.class), any());
		verify(contractRepositoryMock, Mockito.never()).save(any(ContractEntity.class));
		verifyNoInteractions(outboxRepositoryMock, businessruleMock);
	}

	@ParameterizedTest
	@ValueSource(booleans = {
		true, false
	})
	void deleteContract(boolean match) {
		// Arrange
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.of(ContractEntity.builder()
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
		verify(outboxRepositoryMock).save(any(OutboxEntity.class));
		verify(contractRepositoryMock).deleteAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock, businessruleMock, outboxRepositoryMock);
	}

	@Test
	void deleteContractShouldThrow404WhenNoMatch() {
		// Arrange
		when(contractRepositoryMock.findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(contractRepositoryMock).findByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractRepositoryMock);
		verifyNoInteractions(businessruleMock);
	}

	@Test
	void createContractShouldThrowIllegalStateWhenSerializationFails() throws Exception {
		// Arrange
		final var failingObjectMapper = mock(ObjectMapper.class);
		when(failingObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});
		final var service = new ContractService(contractRepositoryMock, attachmentRepositoryMock, outboxRepositoryMock, List.of(), failingObjectMapper, contractValidatorMock);
		when(contractRepositoryMock.save(any(ContractEntity.class)))
			.thenReturn(ContractEntity.builder().withContractId(CONTRACT_ID).build());

		final var contract = Contract.builder()
			.withStatus(ACTIVE)
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		// Act & Assert
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> service.createContract(MUNICIPALITY_ID, contract))
			.withMessageContaining("Failed to serialize CREATED event for contract");

		verify(failingObjectMapper).writeValueAsString(any());
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
