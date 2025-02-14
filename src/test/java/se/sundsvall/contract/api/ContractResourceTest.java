package se.sundsvall.contract.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static se.sundsvall.contract.TestFactory.createContract;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.LandLeaseType.LEASEHOLD;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.UsufructType.HUNTING;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.ContractPaginatedResponse;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Diff;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.model.enums.ContractType;
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
			.uri("/contracts/{municipalityId}/{contractId}", MUNICIPALITY_ID, CONTRACT_ID)
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
			.uri(uriBuilder -> uriBuilder.path("/contracts/{municipalityId}/{contractId}")
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
	void getAllContractsOnAMunicipalityId() {
		when(contractServiceMock.getContracts(eq(MUNICIPALITY_ID), any(ContractRequest.class))).thenReturn(ContractPaginatedResponse.builder().build());

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts/{municipalityId}")
				.queryParam("page", 1)
				.queryParam("size", 10)
				.queryParam("partyId", UUID.randomUUID().toString())
				.build(MUNICIPALITY_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContractPaginatedResponse.class)
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock).getContracts(eq(MUNICIPALITY_ID), any(ContractRequest.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getAllContractsByMunicipalityAndTerm() {
		when(contractServiceMock.getContracts(eq(MUNICIPALITY_ID), any(ContractRequest.class))).thenReturn(ContractPaginatedResponse.builder().build());

		var responseBody = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts/{municipalityId}")
				.queryParam("page", 1)
				.queryParam("size", 10)
				.queryParam("term", "ey")
				.build(MUNICIPALITY_ID))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContractPaginatedResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(responseBody).isNotNull();
		verify(contractServiceMock).getContracts(eq(MUNICIPALITY_ID), any(ContractRequest.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void testLimitWhenFetchingContracts_shouldGenerateBadRequest() {
		when(contractServiceMock.getContracts(eq("1984"), any(ContractRequest.class))).thenReturn(ContractPaginatedResponse.builder().build());

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts/1984")
				.queryParam("page", 1)
				.queryParam("limit", 101)
				.build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ThrowableProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");

		verifyNoInteractions(contractServiceMock);
	}

	@Test
	void postContract() {
		final var contract = Contract.builder()
			.withArea(0)
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY.name())
				.withInvoicedIn(ARREARS.name())
				.build())
			.withLandLeaseType(LEASEHOLD.name())
			.withStatus(ACTIVE.name())
			.withUsufructType(HUNTING.name())
			.withPropertyDesignations(List.of("SUNDSVALL NORRMALM 1:1", "SUNDSVALL NORRMALM 1:2"))
			.withType(ContractType.LAND_LEASE.name())
			.build();

		when(contractServiceMock.createContract(MUNICIPALITY_ID, contract)).thenReturn(CONTRACT_ID);

		webTestClient.post()
			.uri("/contracts/{municipalityId}", MUNICIPALITY_ID)
			.bodyValue(contract)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL_VALUE)
			.expectHeader().location("/contracts/" + MUNICIPALITY_ID + "/" + CONTRACT_ID)
			.expectBody().isEmpty();

		verify(contractServiceMock).createContract(MUNICIPALITY_ID, contract);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void diffContract() {
		final var diff = new Diff(2, 3, List.of(), List.of(1, 2, 3));

		when(contractServiceMock.diffContract(MUNICIPALITY_ID, CONTRACT_ID, 2, 3)).thenReturn(diff);

		final var result = webTestClient.post()
			.uri(uriBuilder -> uriBuilder.path("/contracts/{municipalityId}/{contractId}/diff")
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
			.uri("/contracts/{municipalityId}/{contractId}", MUNICIPALITY_ID, CONTRACT_ID)
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
			.uri("/contracts/{municipalityId}/{contractId}", MUNICIPALITY_ID, CONTRACT_ID)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		verify(contractServiceMock).deleteContract(MUNICIPALITY_ID, CONTRACT_ID);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@ParameterizedTest
	@MethodSource("invalidTermProvider")
	void testGetAllContractsByMunicipalityAndTooShortTerm_shouldGenerateBadRequest(String term) {
		when(contractServiceMock.getContracts(eq("1984"), any(ContractRequest.class))).thenReturn(ContractPaginatedResponse.builder().build());

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts/1984")
				.queryParam("page", 1)
				.queryParam("term", term)
				.build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(tuple("term", "Term must be at least 2 characters long if provided"));

		verifyNoInteractions(contractServiceMock);
	}

	private static Stream<String> invalidTermProvider() {
		return Stream.of("a", "", " ", null);
	}
}
