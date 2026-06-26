package se.sundsvall.contract.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.integration.db.OutboxRepository;
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
	private OutboxRepository outboxRepository;

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
				.queryParam("page", 0)
				.queryParam("size", 10)
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

	/**
	 * Test verifies the following:
	 * - Update is performed in place (same contract, no new record)
	 * - The contract type is changed from PURCHASE_AGREEMENT to LEASE_AGREEMENT; the PURCHASE_AGREEMENT rule no
	 * longer applies, so lease attributes are retained
	 */
	@Test
	void test04_updateContractWithTypeChange() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		// Verify update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
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
			.sendRequest();

		// Delete
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequest();

		// Verify it's gone
		setupCall()
			.withServicePath(contractPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequest();

		// Verify the attachments are gone as well
		setupCall()
			.withServicePath(attachmentPath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_getContractsWithFilter() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "status:'ACTIVE'")
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Test verifies the following:
	 * - Create is performed
	 * - Rule for contracts of type PURCHASE_AGREEMENT is executed and removes attributes not applicable for the type
	 */
	@Test
	void test08_createContractForPurchaseAgreement() {
		final var location = setupCall()
			.withServicePath(fromPath(PATH)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequest()
			.getResponseHeaders().getLocation();

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

	/**
	 * Test verifies the following:
	 * - Update is performed in place (same contract, no new record)
	 * - The type stays PURCHASE_AGREEMENT; the PURCHASE_AGREEMENT rule runs and removes attributes not
	 * applicable for the type
	 */
	@Test
	void test09_updateContractKeepingTypeIntact() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		// Verify update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_updateContractWithPresentBillingCycle() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		// Verify update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test12_filterByStatus() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "status:'ACTIVE'")
				.queryParam("sort", "contractId,asc")
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test13_filterByType() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "type:'LEASE_AGREEMENT'")
				.queryParam("sort", "contractId,asc")
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test14_filterByStatusAndType() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "status:'ACTIVE' and type:'LEASE_AGREEMENT'")
				.buildAndExpand(MUNICIPALITY_ID)
				.toUriString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test15_filterByStatusOr() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "status:'ACTIVE' or status:'DRAFT'")
				.queryParam("sort", "contractId,asc")
				.buildAndExpand(MUNICIPALITY_ID)
				.toUriString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test16_filterNoResults() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "status:'TERMINATED' and type:'PURCHASE_AGREEMENT'")
				.buildAndExpand(MUNICIPALITY_ID)
				.toUriString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test17_filterByContractId() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "contractId:'2024-34567'")
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test18_sortByContractIdAsc() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("sort", "contractId,asc")
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test19_sortByContractIdDesc() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("sort", "contractId,desc")
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test20_paginationFirstPage() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("sort", "contractId,asc")
				.queryParam("page", 0)
				.queryParam("size", 2)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test21_paginationSecondPage() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("sort", "contractId,asc")
				.queryParam("page", 1)
				.queryParam("size", 2)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test22_paginationLastPage() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("sort", "contractId,asc")
				.queryParam("page", 2)
				.queryParam("size", 2)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test23_filterWithSortAndPagination() {
		setupCall()
			.withServicePath(fromPath(PATH)
				.queryParam("filter", "type:'LEASE_AGREEMENT'")
				.queryParam("sort", "contractId,desc")
				.queryParam("page", 0)
				.queryParam("size", 2)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Test verifies the following:
	 * - Create is performed for a LAND_LEASE_RESIDENTIAL contract with YEARLY interval and currentPeriodEndDate June 30
	 * - BDC billing cycle is created with billingMonths [6] (June) instead of [12] (December)
	 */
	@Test
	void test24_createContractWithJuneBillingSchedule() {
		final var location = setupCall()
			.withServicePath(fromPath(PATH)
				.build(MUNICIPALITY_ID)
				.toString())
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(ALL_VALUE))
			.sendRequest()
			.getResponseHeaders().getLocation();

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

	/**
	 * Test verifies the following:
	 * - Update is performed for a contract changed to LAND_LEASE_RESIDENTIAL with YEARLY interval and currentPeriodEndDate June 30
	 * - BDC billing cycle is updated with billingMonths [6] (June) instead of [12] (December)
	 */
	@Test
	void test25_updateContractWithJuneBillingSchedule() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		// Verify update
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Test verifies the following:
	 * - Patch is applied in place on the existing contract
	 * - Only the fields sent in the patch payload (description, area, startDate, endDate and currentPeriod) are updated;
	 *   all other fields remain as before
	 * - Business rule for invoicing is executed (UPDATE action); GET of the existing BDC cycle returns matching
	 *   settings so no further call to BDC is made
	 */
	@Test
	void test26_patchContract() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Patch
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		// Verify outbox entry was written
		final var outboxEntries26 = outboxRepository.findAll();
		assertThat(outboxEntries26).hasSize(1);
		assertThat(outboxEntries26.getFirst().getEventType()).isEqualTo("UPDATED");
		assertThat(outboxEntries26.getFirst().getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(outboxEntries26.getFirst().getPayload()).contains("\"id\":\"" + CONTRACT_ID + "\"");

		// Verify patch by performing a GET: only patched fields changed.
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Test verifies the following:
	 * - Patching indexTerms replaces the existing index term groups in place
	 * - additionalTerms (not sent in the payload) are preserved
	 * - The contract is updated in place
	 */
	@Test
	void test27_patchContractTermGroups() {
		final var path = fromPath(PATH + "/{contractId}")
			.build(MUNICIPALITY_ID, CONTRACT_ID)
			.toString();

		// Patch — replace indexTerms only
		setupCall()
			.withServicePath(path)
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.sendRequest();

		// Verify outbox entry was written
		final var outboxEntries27 = outboxRepository.findAll();
		assertThat(outboxEntries27).hasSize(1);
		assertThat(outboxEntries27.getFirst().getEventType()).isEqualTo("UPDATED");
		assertThat(outboxEntries27.getFirst().getContractId()).isEqualTo(CONTRACT_ID);
		assertThat(outboxEntries27.getFirst().getPayload()).contains("\"id\":\"" + CONTRACT_ID + "\"");

		// Verify: indexTerms replaced, additionalTerms intact
		setupCall()
			.withServicePath(path)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
