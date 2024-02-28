package se.sundsvall.contract.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.contract.Application;
import se.sundsvall.contract.TestFactory;
import se.sundsvall.contract.api.model.ContractRequest;
import se.sundsvall.contract.api.model.LandLeaseContract;
import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;
import se.sundsvall.contract.service.ContractService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContractResourceTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ContractService contractServiceMock;

	@Test
	void getContractsById() {
		when(contractServiceMock.getContract("1984", 1L)).thenReturn(LandLeaseContract.builder().build());

		final var response = webTestClient.get()
			.uri("/contracts/1984/1")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(LandLeaseContract.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(contractServiceMock).getContract("1984", 1L);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getContracts() {
		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts/1984")
				.queryParam("personId", "1")
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<List<LandLeaseContract>>() {

			})
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock).getContracts(eq("1984"), any(ContractRequest.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void postContracts() {

		final var id = 123L;
		final var contract = LandLeaseContract.builder()
			.withVersion(0)
			.withArea(0)
			.withInvoiceInterval(IntervalType.QUARTERLY.name())
			.withLandLeaseType(LandLeaseType.LEASEHOLD.name())
			.withStatus(Status.ACTIVE.name())
			.withUsufructType(UsufructType.HUNTING.name())
			.withPropertyDesignation("SUNDSVALL NORRMALM 1:1")
			.build();

		when(contractServiceMock.createContract(any(), any())).thenReturn(id);

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
	void patchContracts() {
		final var contract = TestFactory.getUpdatedLandLeaseContract();

		webTestClient.patch()
			.uri("/contracts/1984/1")
			.bodyValue(contract)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();

		verify(contractServiceMock).updateContract(eq("1984"), eq(1L), any());
		verifyNoMoreInteractions(contractServiceMock);
	}
}
