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

	private static final String RESPONSE_FILE = "response.json";
	private static final String REQUEST_FILE = "request.json";

	@Autowired
	private ContractRepository contractRepository;

	@Test
	void test01_readContract() {
		setupCall()
			.withServicePath("/contracts/1984/2024-12345")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_readContracts() {
		setupCall()
			.withServicePath("/contracts/1984?page=1&limit=9")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_createContract() {
		var test = setupCall()
			.withServicePath("/contracts/1984")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequestAndVerifyResponse();

		var location = test.getResponseHeaders().getLocation();

		//Verify it's there
		setupCall()
			.withServicePath(location.getPath())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_updateContract() {
		final String path = "/contracts/1984/2024-12345";
		final String allContractsPath = "/contracts/1984/2024-12345";

		//Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		//Verify update
		setupCall()
			.withServicePath(allContractsPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		//We can't fetch older versions via the API, so check that we have an older version of the contract
		//The old version should have id 1.
		var contractEntity = contractRepository.findById(1L).get();
		assertThat(contractEntity.getContractId()).isEqualTo("2024-12345");
		assertThat(contractEntity.getVersion()).isEqualTo(1);
	}

	@Test
	void test05_deleteContract() {
		final String contractPath = "/contracts/1984/2024-12345";
		final String attachmentPath = contractPath + "/attachments/1";

		//Verify the contract is there
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		//Delete
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		//Verify it's gone
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();

		//Verify the attachments are gone as well
		setupCall()
			.withServicePath(attachmentPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();
	}

}
