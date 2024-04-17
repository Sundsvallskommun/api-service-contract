package se.sundsvall.contract.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
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

import java.util.List;
import java.util.Optional;

import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.integration.db.AttachmentRepository;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.contract.integration.db.model.ContractEntity;
import se.sundsvall.contract.integration.db.model.LandLeaseContractEntity;
import se.sundsvall.contract.model.Change;
import se.sundsvall.contract.service.diff.Differ;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

	@Mock
	private ContractRepository mockContractRepository;

	@Mock
	private AttachmentRepository mockAttachmentRepository;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private ContractMapper mockContractMapper;

	@Mock
	private Differ mockDiffer;

	@InjectMocks
	private ContractService contractService;

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	@Test
	void createContract() {
		//Arrange
		var contract = LandLeaseContract.builder()
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
  		var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID, null);

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
	void getContract_withSpecificVersion() {
		var landLeaseContractEntity = createLandLeaseContractEntity();
		when(mockContractRepository.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, 2))
			.thenReturn(Optional.of(landLeaseContractEntity));

		//Act
		var result = contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID, 2);

		//Assert
		assertThat(result).isNotNull();
		assertThat(result)
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("type", "attachments", "attachmentMetaData")
			.isEqualTo(landLeaseContractEntity);

		verify(mockContractRepository).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, 2);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void getContract_shouldThrow404_whenNoMatch() {
		//Arrange
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(Optional.empty());

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.getContract(MUNICIPALITY_ID, CONTRACT_ID, null));

		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void getPaginatedContracts() {
		//Arrange
		var landLeaseContractEntity = createLandLeaseContractEntity();

		Page<ContractEntity> page = new PageImpl<>(List.of(landLeaseContractEntity, landLeaseContractEntity));

		when(mockContractRepository.findAll(Mockito.<Specification<ContractEntity>>any(), any(Pageable.class))).thenReturn(page);
		when(mockAttachmentRepository.findAllByMunicipalityIdAndContractId(eq(MUNICIPALITY_ID), any(String.class)))
			.thenReturn(List.of(createAttachmentEntity()));

		var request = ContractRequest.builder().build();
		request.setPage(1);
		request.setLimit(5);

		//Act
		var result = contractService.getContracts(MUNICIPALITY_ID, request);

		//Assert
		assertThat(result).isNotNull();
		assertThat(result.getContracts().getFirst())
			.isNotNull()
			.usingRecursiveComparison()
			.withEnumStringComparison()
			.ignoringFields("type", "attachmentMetaData")
			.isEqualTo(landLeaseContractEntity);

		var metaData = result.getMetaData();
		assertThat(metaData).isNotNull();
		assertThat(metaData.getPage()).isOne();
		assertThat(metaData.getLimit()).isEqualTo(2);
		assertThat(metaData.getCount()).isEqualTo(2);
		assertThat(metaData.getTotalRecords()).isEqualTo(2);
		assertThat(metaData.getTotalPages()).isOne();

		verify(mockContractRepository).findAll(Mockito.<Specification<ContractEntity>>any(), any(Pageable.class));
		verify(mockAttachmentRepository, times(2)).findAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, "2024-98765");
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
		contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, landLeaseContract);

		//Assert
		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockContractRepository).save(any(LandLeaseContractEntity.class));
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void updateContract_shouldThrow404_whenNoMatch() {
		when(mockContractRepository.findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(any(String.class), any(String.class)))
			.thenReturn(Optional.empty());

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.updateContract(MUNICIPALITY_ID, CONTRACT_ID, createLandLeaseContract()))
			.satisfies(thrownProblem -> assertThat(thrownProblem.getStatus()).isEqualTo(Status.NOT_FOUND));

		verify(mockContractRepository).findFirstByMunicipalityIdAndContractIdOrderByVersionDesc(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void diffContract() {
		var availableVersions = List.of(1, 2, 3);
		var oldVersion = 2;
		var newVersion = 3;
		var landLeaseContractEntity = createLandLeaseContractEntity();

		when(mockContractRepository.findAllContractVersionsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID))
			.thenReturn(availableVersions);
		when(mockContractRepository.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, oldVersion))
			.thenReturn(Optional.of(landLeaseContractEntity));
		when(mockContractRepository.findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, newVersion))
			.thenReturn(Optional.of(landLeaseContractEntity));
		when(mockDiffer.diff(any(Contract.class), any(Contract.class), anyList()))
			.thenReturn(List.of(Change.modification(new Path(), new TextNode("oldValue"), new TextNode("newValue"))));

		var diff = contractService.diffContract(MUNICIPALITY_ID, CONTRACT_ID, oldVersion, newVersion);

		assertThat(diff.oldVersion()).isEqualTo(oldVersion);
		assertThat(diff.newVersion()).isEqualTo(newVersion);
		assertThat(diff.changes()).isNotNull().hasSize(1);
		assertThat(diff.availableVersions()).hasSameElementsAs(availableVersions);

		verify(mockContractRepository).findAllContractVersionsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verify(mockContractRepository).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, oldVersion);
		verify(mockContractRepository).findByMunicipalityIdAndContractIdAndVersion(MUNICIPALITY_ID, CONTRACT_ID, newVersion);
		verifyNoMoreInteractions(mockContractRepository);
		verify(mockDiffer).diff(any(Contract.class), any(Contract.class), anyList());
		verifyNoMoreInteractions(mockDiffer);
	}

	@Test
	void deleteContract() {
		//Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(true);

		//Act
		contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID);

		//Assert
		verify(mockContractRepository).deleteAllByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}

	@Test
	void deleteContract_shouldThrow404_whenNoMatch() {
		//Arrange
		when(mockContractRepository.existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID)).thenReturn(false);

		//Act & Assert
		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> contractService.deleteContract(MUNICIPALITY_ID, CONTRACT_ID));

		verify(mockContractRepository).existsByMunicipalityIdAndContractId(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(mockContractRepository);
	}
}
