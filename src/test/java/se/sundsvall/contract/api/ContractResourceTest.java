package se.sundsvall.contract.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.contract.TestFactory.createContract;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Diff;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.PropertyDesignation;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.service.ContractService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContractResourceTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private ContractService contractServiceMock;

	@Test
	void getContractByMunicipalityAndContractId() {
		when(contractServiceMock.getContract(MUNICIPALITY_ID, CONTRACT_ID, null)).thenReturn(Contract.builder().build());

		final var response = webTestClient.get()
			.uri("/{municipalityId}/contracts/{contractId}", MUNICIPALITY_ID, CONTRACT_ID)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Contract.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();

		verify(contractServiceMock).getContract(MUNICIPALITY_ID, CONTRACT_ID, null);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getContractByMunicipalityAndContractIdAndVersion() {
		when(contractServiceMock.getContract(MUNICIPALITY_ID, CONTRACT_ID, 2)).thenReturn(Contract.builder().build());

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/contracts/{contractId}")
				.queryParam("version", 2)
				.build(MUNICIPALITY_ID, CONTRACT_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Contract.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();

		verify(contractServiceMock).getContract(MUNICIPALITY_ID, CONTRACT_ID, 2);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getContractsWithoutFilter() {
		final var page = new PageImpl<>(
			List.of(Contract.builder().build()),
			PageRequest.of(0, 10),
			1);

		when(contractServiceMock.getContracts(eq(MUNICIPALITY_ID), any(), any(Pageable.class))).thenReturn(page);

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/contracts")
				.build(MUNICIPALITY_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Page.class)
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock).getContracts(eq(MUNICIPALITY_ID), any(), any(Pageable.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	void getContractsWithFilter() {
		final var page = new PageImpl<>(
			List.of(Contract.builder().build()),
			PageRequest.of(0, 10),
			1);

		when(contractServiceMock.getContracts(eq(MUNICIPALITY_ID), any(Specification.class), any(Pageable.class))).thenReturn(page);

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/contracts")
				.queryParam("filter", "status:'ACTIVE'")
				.build(MUNICIPALITY_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Page.class)
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock).getContracts(eq(MUNICIPALITY_ID), any(Specification.class), any(Pageable.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getContractsWithPaginationAndSorting() {
		final var page = new PageImpl<>(
			List.of(Contract.builder().build()),
			PageRequest.of(1, 20),
			25);

		when(contractServiceMock.getContracts(eq(MUNICIPALITY_ID), any(), any(Pageable.class))).thenReturn(page);

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/contracts")
				.queryParam("page", 1)
				.queryParam("size", 20)
				.queryParam("sort", "start,desc")
				.build(MUNICIPALITY_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Page.class)
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock).getContracts(eq(MUNICIPALITY_ID), any(), any(Pageable.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void postContract() {
		final var contract = Contract.builder()
			.withArea(0)
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY)
				.withInvoicedIn(ARREARS)
				.build())
			.withLeaseType(LeaseType.LAND_LEASE_RESIDENTIAL)
			.withStatus(ACTIVE)
			.withPropertyDesignations(List.of(
				PropertyDesignation.builder()
					.withName("SUNDSVALL NORRMALM 1:1")
					.withDistrict("Sundsvall")
					.build(),
				PropertyDesignation.builder()
					.withName("SUNDSVALL NORRMALM 1:2")
					.withDistrict("Sundsvall")
					.build()))
			.withType(ContractType.LEASE_AGREEMENT)
			.build();

		when(contractServiceMock.createContract(MUNICIPALITY_ID, contract)).thenReturn(CONTRACT_ID);

		webTestClient.post()
			.uri("/{municipalityId}/contracts", MUNICIPALITY_ID)
			.bodyValue(contract)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL_VALUE)
			.expectHeader().location("/" + MUNICIPALITY_ID + "/contracts/" + CONTRACT_ID)
			.expectBody().isEmpty();

		verify(contractServiceMock).createContract(MUNICIPALITY_ID, contract);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void diffContract() {
		final var diff = new Diff(2, 3, List.of(), List.of(1, 2, 3));

		when(contractServiceMock.diffContract(MUNICIPALITY_ID, CONTRACT_ID, 2, 3)).thenReturn(diff);

		final var result = webTestClient.post()
			.uri(uriBuilder -> uriBuilder.path("/{municipalityId}/contracts/{contractId}/diff")
				.queryParam("oldVersion", 2)
				.queryParam("newVersion", 3)
				.build(MUNICIPALITY_ID, CONTRACT_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Diff.class)
			.returnResult()
			.getResponseBody();

		assertThat(result).isNotNull();
		assertThat(result.oldVersion()).isEqualTo(diff.oldVersion());
		assertThat(result.newVersion()).isEqualTo(diff.newVersion());
		assertThat(result.availableVersions()).isEqualTo(diff.availableVersions());
		assertThat(result.changes()).isEqualTo(diff.changes());

		verify(contractServiceMock).diffContract(MUNICIPALITY_ID, CONTRACT_ID, 2, 3);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void updateContract() {
		final var landLeaseContract = createContract();

		doNothing().when(contractServiceMock).updateContract(MUNICIPALITY_ID, CONTRACT_ID, landLeaseContract);

		webTestClient.put()
			.uri("/{municipalityId}/contracts/{contractId}", MUNICIPALITY_ID, CONTRACT_ID)
			.bodyValue(landLeaseContract)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		verify(contractServiceMock).updateContract(MUNICIPALITY_ID, CONTRACT_ID, landLeaseContract);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void deleteContract() {
		doNothing().when(contractServiceMock).deleteContract(MUNICIPALITY_ID, CONTRACT_ID);

		webTestClient.delete()
			.uri("/{municipalityId}/contracts/{contractId}", MUNICIPALITY_ID, CONTRACT_ID)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		verify(contractServiceMock).deleteContract(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractServiceMock);
	}

}
