package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.TestFactory.createAttachmentEntity;
import static se.sundsvall.contract.TestFactory.createLandLeaseContract;
import static se.sundsvall.contract.TestFactory.createLandLeaseContractEntity;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.LandLeaseType.SITELEASEHOLD;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.UsufructType.HUNTING;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	@Mock
	private ContractRepository mockContractRepository;

	@Mock
	private AttachmentRepository mockAttachmentRepository;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private ContractMapper mockContractMapper;

	@InjectMocks
	private ContractService contractService;

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	@Test
	void createContract() {
		//Arrange
		var contract = LandLeaseContract.builder()
			.withCaseId(1L)
			.withLandLeaseType(SITELEASEHOLD.name())
			.withUsufructType(HUNTING.name())
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY.name())
				.withInvoicedIn(ARREARS.name())
				.build())
			.withStatus(ACTIVE.name())
			.build();

		when(mockContractRepository.save(any(LandLeaseContractEntity.class)))
			.thenReturn(LandLeaseContractEntity.builder().withContractId(CONTRACT_ID).build());

		//ACt
		var result = contractService.createContract(MUNICIPALITY_ID, contract);

		//Assert
		assertThat(result).isEqualTo(CONTRACT_ID);
		verify(mockContractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void getContract() {
		//Arrange
		var landLeaseContractEntity = createLandLeaseContractEntity();
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(Optional.of(landLeaseContractEntity));

		//Act
  		var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID);

		  //Assert
		assertThat(result).isNotNull();
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("type", "attachments", "attachmentMetaData")
			.isEqualTo(landLeaseContractEntity);

		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void testGetContract_shouldThrow404_whenNoMatch() {
		//Arrange
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void getContracts() {
		//Arrange
		var landLeaseContractEntity = createLandLeaseContractEntity();
		when(mockContractRepository.findAll(Mockito.<Specification<ContractEntity>>any())).thenReturn(List.of(landLeaseContractEntity));

		when(mockContractRepository.findAll(Mockito.<Specification<ContractEntity>>any()))
			.thenReturn(List.of(landLeaseContractEntity));
		when(mockAttachmentRepository.findAllByMunicipalityIdAndContractId(eq(MUNICIPALITY_ID), any(String.class)))
			.thenReturn(List.of(createAttachmentEntity()));

		//Act
		var request = new ContractRequest("contractId", "propertyDesignation", "organizationNumber",
			List.of("propertyDesignation1", "propertyDesignation2"), "externalReferenceId",
			LocalDate.of(2023, 10, 10), SITELEASEHOLD.name());
		var result = contractService.getContracts(MUNICIPALITY_ID, request);

		//Assert
		assertThat(result).isNotNull().hasSize(1);
		assertThat(result.getFirst())
			.isNotNull()
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("type", "attachments", "attachmentMetaData")
			.isEqualTo(landLeaseContractEntity);

		verify(mockContractRepository).findAll(Mockito.<Specification<ContractEntity>>any());
		verifyNoMoreInteractions(mockContractRepository);
		verifyNoMoreInteractions(mockAttachmentRepository);
	}

	@Test
	void updateContract() {
		//Arrange
		var landLeaseContractEntity = createLandLeaseContractEntity();
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(any(String.class), any(String.class))).thenReturn(Optional.of(landLeaseContractEntity));
		var landLeaseContract = createLandLeaseContract();

		//Act
		contractService.updateContract("1984", "2024-12345", landLeaseContract);

		//Assert
		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockContractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void testDeleteContract() {
		//Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);

		//Act
		contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID);

		//Assert
		verify(mockContractRepository).deleteAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void testDeleteContract_shouldThrow404_whenNoMatch() {
		//Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}
}
