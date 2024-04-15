package se.sundsvall.contract.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static se.sundsvall.contract.TestFactory.createLandLeaseContract;
import static se.sundsvall.contract.model.enums.IntervalType.QUARTERLY;
import static se.sundsvall.contract.model.enums.InvoicedIn.ARREARS;
import static se.sundsvall.contract.model.enums.LandLeaseType.LEASEHOLD;
import static se.sundsvall.contract.model.enums.Status.ACTIVE;
import static se.sundsvall.contract.model.enums.UsufructType.HUNTING;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.contract.Application;
import se.sundsvall.contract.api.model.ContractPaginatedResponse;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.service.ContractService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContractResourceTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ContractService contractServiceMock;

	@Test
	void getContractsByMunicipalityAndContractId() {
		when(contractServiceMock.getContract("1984", "2024-12345")).thenReturn(LandLeaseContract.builder().build());

		var response = webTestClient.get()
			.uri("/contracts/1984/2024-12345")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(LandLeaseContract.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(contractServiceMock).getContract("1984", "2024-12345");
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getAllContractsOnAMunicipalityId() {
		when(contractServiceMock.getContracts(eq("1984"), any(ContractRequest.class))).thenReturn(ContractPaginatedResponse.builder().build());

		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts/1984")
				.queryParam("page", 1)
				.queryParam("size", 10)
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(ContractPaginatedResponse.class)
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock).getContracts(eq("1984"), any(ContractRequest.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void testLimitWhenFetchingContracts_shouldGenerateBadRequest() {
		when(contractServiceMock.getContracts(eq("1984"), any(ContractRequest.class))).thenReturn(ContractPaginatedResponse.builder().build());

		var response = webTestClient.get()
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

		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST);
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");

		verifyNoInteractions(contractServiceMock);
	}

	@Test
	void postContract() {
		var id = "2024-12345";
		var contract = LandLeaseContract.builder()
			.withArea(0)
			.withInvoicing(Invoicing.builder()
				.withInvoiceInterval(QUARTERLY.name())
				.withInvoicedIn(ARREARS.name())
				.build())
			.withLandLeaseType(LEASEHOLD.name())
			.withStatus(ACTIVE.name())
			.withUsufructType(HUNTING.name())
			.withPropertyDesignations(List.of("SUNDSVALL NORRMALM 1:1", "SUNDSVALL NORRMALM 1:2"))
			.build();

		when(contractServiceMock.createContract("1984", contract)).thenReturn(id);

		webTestClient.post()
			.uri("/contracts/1984")
			.bodyValue(contract)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL_VALUE)
			.expectHeader().location("/contracts/1984/" + id)
			.expectBody().isEmpty();

		verify(contractServiceMock).createContract("1984", contract);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void updateContract() {
		var landLeaseContract = createLandLeaseContract();

		doNothing().when(contractServiceMock).updateContract("1984", "2024-12345", landLeaseContract);

		webTestClient.put()
			.uri("/contracts/1984/2024-12345")
			.bodyValue(landLeaseContract)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		verify(contractServiceMock).updateContract("1984", "2024-12345", landLeaseContract);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void testDeleteContract() {
		doNothing().when(contractServiceMock).deleteContract("1984", "2024-12345");

		webTestClient.delete()
			.uri("/contracts/1984/2024-12345")
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		verify(contractServiceMock).deleteContract("1984", "2024-12345");
		verifyNoMoreInteractions(contractServiceMock);
	}

}
