package se.sundsvall.contract.apptest;

import static org.apache.hc.core5.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.contract.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/AttachmentIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class AttachmentIT extends AbstractAppTest {
	private static final String RESPONSE_FILE = "response.json";
	private static final String REQUEST_FILE = "request.json";
	private static final String PATH = "/1984/contracts/2024-12345/attachments";

	@Test
	void test01_getAttachmentMetadataList() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getAttachmentBinary() throws Exception {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PDF_VALUE))
			.withExpectedResponseHeader(CONTENT_DISPOSITION, List.of("attachment; filename=\"someFile.pdf\""))
			.withExpectedBinaryResponse("someFile.pdf")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_postAndGetNewAttachment() throws Exception {
		var test = setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withRequestFile("file", "LeaseContract12.pdf")
			.withExpectedResponseStatus(CREATED)
			.sendRequestAndVerifyResponse();

		var location = test.getResponseHeaders().getLocation().getPath();

		// Verify the uploaded bytes come back byte-identical
		setupCall()
			.withServicePath(location)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PDF_VALUE))
			.withExpectedBinaryResponse("LeaseContract12.pdf")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_patchAttachmentMetadata() {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		// Verify the metadata was updated
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_deleteAttachment() {
		var servicePath = PATH + "/1";
		// Verify the attachment exists since the delete doesn't care if it actually deletes something
		setupCall()
			.withServicePath(servicePath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.sendRequestAndVerifyResponse();

		// Delete it
		setupCall()
			.withServicePath(servicePath)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		// Verify it's gone i.e. a 404
		setupCall()
			.withServicePath(servicePath)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();
	}
}
