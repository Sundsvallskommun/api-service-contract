package se.sundsvall.contract.apptest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.integration.db.OutboxRepository;
import se.sundsvall.contract.scheduler.OutboxDispatcher;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/OutboxIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class OutboxIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";
	private static final String REQUEST_FILE = "request.json";
	private static final String PATH = "/{municipalityId}/contracts";

	@Autowired
	private OutboxRepository outboxRepository;

	@Autowired
	private OutboxDispatcher outboxDispatcher;

	/**
	 * Verifies that creating a contract writes a CONTRACT_CREATED entry to the outbox.
	 */
	@Test
	void test01_createContractWritesContractCreatedToOutbox() {
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequest();

		final var entries = outboxRepository.findAll();
		assertThat(entries).hasSize(1);
		assertThat(entries.getFirst().getEventType()).isEqualTo("CONTRACT_CREATED");
		assertThat(entries.getFirst().getContractId()).isNotNull();
		assertThat(entries.getFirst().getRetries()).isZero();
		assertThat(entries.getFirst().getPayload()).contains("\"municipalityId\":\"" + MUNICIPALITY_ID + "\"");
	}

	/**
	 * Verifies that updating a contract writes a CONTRACT_UPDATED entry to the outbox.
	 */
	@Test
	void test02_updateContractWritesContractUpdatedToOutbox() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{contractId}").build(MUNICIPALITY_ID, CONTRACT_ID).toString())
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		final var entries = outboxRepository.findAll();
		assertThat(entries).hasSize(1);
		assertThat(entries.getFirst().getEventType()).isEqualTo("CONTRACT_UPDATED");
		assertThat(entries.getFirst().getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(entries.getFirst().getPayload()).contains("\"contractId\":\"" + CONTRACT_ID + "\"");
	}

	/**
	 * Verifies that deleting a contract writes a CONTRACT_DELETED entry to the outbox.
	 */
	@Test
	void test03_deleteContractWritesContractDeletedToOutbox() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{contractId}").build(MUNICIPALITY_ID, CONTRACT_ID).toString())
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequest();

		final var entries = outboxRepository.findAll();
		assertThat(entries).hasSize(1);
		assertThat(entries.getFirst().getEventType()).isEqualTo("CONTRACT_DELETED");
		assertThat(entries.getFirst().getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(entries.getFirst().getPayload()).contains("\"contractId\":\"" + CONTRACT_ID + "\"");
	}

	/**
	 * Verifies the full outbox dispatch flow for a created contract:
	 * create contract → CONTRACT_CREATED in outbox → dispatcher sends to billing → outbox cleared.
	 */
	@Test
	void test04_dispatchSendsCreatedEventToBilling() {
		// Stub token endpoint
		wiremock.stubFor(post(urlPathEqualTo("/api-gateway/token"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("common/responses/api-gateway-token-response.json")));

		// Stub billing endpoint
		wiremock.stubFor(post(urlEqualTo("/api-billing-data-collector/" + MUNICIPALITY_ID + "/contracts/created"))
			.willReturn(aResponse().withStatus(200)));

		// Create contract → writes to outbox
		setupCall()
			.withServicePath(fromPath(PATH).build(MUNICIPALITY_ID).toString())
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.sendRequest();

		assertThat(outboxRepository.findUnsent()).hasSize(1);

		// Dispatch → sends to billing
		outboxDispatcher.dispatch();

		// Verify billing was called once
		wiremock.verify(1, postRequestedFor(urlEqualTo("/api-billing-data-collector/" + MUNICIPALITY_ID + "/contracts/created")));

		// Verify outbox was cleared after successful dispatch
		assertThat(outboxRepository.findUnsent()).isEmpty();
	}

	/**
	 * Verifies the full outbox dispatch flow for an updated contract:
	 * update contract → CONTRACT_UPDATED in outbox → dispatcher sends to billing → outbox cleared.
	 */
	@Test
	void test05_dispatchSendsUpdatedEventToBilling() {
		wiremock.stubFor(post(urlPathEqualTo("/api-gateway/token"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("common/responses/api-gateway-token-response.json")));

		wiremock.stubFor(post(urlEqualTo("/api-billing-data-collector/" + MUNICIPALITY_ID + "/contracts/updated"))
			.willReturn(aResponse().withStatus(200)));

		setupCall()
			.withServicePath(fromPath(PATH + "/{contractId}").build(MUNICIPALITY_ID, CONTRACT_ID).toString())
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		outboxDispatcher.dispatch();

		wiremock.verify(1, postRequestedFor(urlEqualTo("/api-billing-data-collector/" + MUNICIPALITY_ID + "/contracts/updated")));
		assertThat(outboxRepository.findUnsent()).isEmpty();
	}

	/**
	 * Verifies the full outbox dispatch flow for a deleted contract:
	 * delete contract → CONTRACT_DELETED in outbox → dispatcher sends to billing → outbox cleared.
	 */
	@Test
	void test06_dispatchSendsDeletedEventToBilling() {
		wiremock.stubFor(post(urlPathEqualTo("/api-gateway/token"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("common/responses/api-gateway-token-response.json")));

		wiremock.stubFor(post(urlEqualTo("/api-billing-data-collector/" + MUNICIPALITY_ID + "/contracts/deleted"))
			.willReturn(aResponse().withStatus(200)));

		setupCall()
			.withServicePath(fromPath(PATH + "/{contractId}").build(MUNICIPALITY_ID, CONTRACT_ID).toString())
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequest();

		outboxDispatcher.dispatch();

		wiremock.verify(1, postRequestedFor(urlEqualTo("/api-billing-data-collector/" + MUNICIPALITY_ID + "/contracts/deleted")));
		assertThat(outboxRepository.findUnsent()).isEmpty();
	}
}
