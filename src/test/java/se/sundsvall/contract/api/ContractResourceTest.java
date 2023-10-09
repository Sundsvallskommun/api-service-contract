package se.sundsvall.contract.api;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

		when(contractServiceMock.getContract(1L)).thenReturn(LandLeaseContract.builder().build());

		final var response = webTestClient.get()
			.uri("/contracts/1")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(LandLeaseContract.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(contractServiceMock, times(1)).getContract(any(Long.class));
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void getContracts() {
		webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/contracts")
				.queryParam("personId", "1")
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<List<LandLeaseContract>>() {

			})
			.returnResult()
			.getResponseBody();

		verify(contractServiceMock, times(1)).getContracts(any(ContractRequest.class));
		verifyNoMoreInteractions(contractServiceMock);
	}


	@Test
	void postContracts() {

		final var contract = LandLeaseContract.builder()
			.withVersion(0)
			.withArea(0)
			.withPropertyDesignation("SUNDSVALL NORRMALM 1:1")
			.build();

		webTestClient.post()
			.uri("/contracts")
			.bodyValue(contract)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL_VALUE)
			.expectBody().isEmpty();

		verify(contractServiceMock, times(1)).createContract(contract);
		verifyNoMoreInteractions(contractServiceMock);
	}

	@Test
	void patchContracts() {

		final var contract = TestFactory.getUpdatedLandLeaseContract();

		webTestClient.patch()
			.uri("/contracts/1")
			.bodyValue(contract)
			.exchange()
			.expectStatus().isNoContent()
			.expectHeader().contentType(ALL_VALUE)
			.expectBody().isEmpty();

		verify(contractServiceMock, times(1)).updateContract(any(Long.class), any());
		verifyNoMoreInteractions(contractServiceMock);
	}

}
