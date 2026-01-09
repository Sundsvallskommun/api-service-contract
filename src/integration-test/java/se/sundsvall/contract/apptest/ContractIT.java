package se.sundsvall.contract.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.contract.Application;
import se.sundsvall.contract.integration.db.ContractRepository;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/ContractIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ContractIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";

	private static final String RESPONSE_FILE = "response.json";
	private static final String REQUEST_FILE = "request.json";
	private static final String PATH = "/{municipalityId}/contracts";

	@Autowired
	private ContractRepository contractRepository;

	@Test
	void test01_readContract() {
		setupCall()
			.withServicePath(fromPath(PATH + "/{contractId}")
				.build(MUNICIPALITY_ID, CONTRACT_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_readContracts() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("page", 1)
				.queryParam("limit", 9)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_createContractForLeaseAgreement() {
		final var createCall = setupCall()
			.withServicePath(fromPath(PATH)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequestAndVerifyResponse();

		final var location = createCall.getResponseHeaders().getLocation();

		assertThat(location).isNotNull();
		assertThat(location.getPath()).isNotNull();

		// Verify it's there
		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_updateContractKeepingTypeIntact() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		// Verify update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		// We can't fetch older versions via the API, so check that we have an older version of the contract
		// The old version should have id 1.
		final var contractEntity = contractRepository.findById(1L).get();
		assertThat(contractEntity.getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(contractEntity.getVersion()).isEqualTo(1);
	}

	@Test
	void test05_deleteContract() {
		final var contractPath = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();
		final var attachmentPath = fromPath(contractPath)
			.path("/attachments/{attachmentId}")
			.build(1)
			.toString();

		// Verify the contract is there
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		// Delete
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		// Verify it's gone
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();

		// Verify the attachments are gone as well
		setupCall()
			.withServicePath(attachmentPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_diffContract() {
		final var path = fromPath(PATH + "/{contractId}/diff")
			.queryParam("oldVersion", 1)
			.queryParam("newVersion", 2)
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		setupCall()
			.withServicePath(path)
			.withHttpMethod(POST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_getContractsWithTerm() {
		setupCall()
			.withServicePath("/" + MUNICIPALITY_ID + "/contracts" + "?term=pöle vault&page=1&limit=15")  // To prevent double encoding
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_createContractForPurchaseAgreement() {
		final var createCall = setupCall()
			.withServicePath(fromPath(PATH)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequestAndVerifyResponse();

		final var location = createCall.getResponseHeaders().getLocation();

		assertThat(location).isNotNull();
		assertThat(location.getPath()).isNotNull();

		// Verify it's there
		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_updateContractWithTypeChange() throws Exception {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		// Verify update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		// We can't fetch older versions via the API, so check that we have an older version of the contract
		// The old version should have id 1.
		final var contractEntity = contractRepository.findById(1L).get();
		assertThat(contractEntity.getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(contractEntity.getVersion()).isEqualTo(1);
	}
}
