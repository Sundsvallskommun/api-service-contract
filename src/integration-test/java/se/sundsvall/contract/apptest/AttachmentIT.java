package se.sundsvall.contract.apptest;

import static org.apache.hc.core5.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import java.util.List;
import net.javacrumbs.jsonunit.core.Option;
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

	/**
	 * The seed contract 2024-23456 has no attachments of its own, which makes it the counterpart whenever a test needs to
	 * show that something is scoped to one contract.
	 */
	private static final String OTHER_CONTRACT_PATH = "/1984/contracts/2024-23456/attachments";
	private static final String UNKNOWN_CONTRACT_PATH = "/1984/contracts/2024-99999/attachments";

	/**
	 * The attachment list exactly as the seed leaves it. Tests that must show that a rejected or missed request changed
	 * nothing assert against this, rather than against a per-test copy of the same fixture.
	 */
	private static final String SEED_ATTACHMENT_LIST = """
		[
			{
				"id": 1,
				"category": "CONTRACT",
				"filename": "someFile.pdf",
				"mimeType": "application/pdf",
				"note": "someNote",
				"hash": "5c9e50d35ba81e923e013a2f9629f744d6bf50390839c40cc5873dba54c7240e",
				"created": "2024-01-15T10:30:00Z"
			}
		]""";

	/**
	 * Problem bodies carry a type, a title and an instance on top of what is asserted here, so they are compared with
	 * extra fields ignored - the status and the detail are what each of these tests is actually about.
	 */
	private static final List<Option> IGNORE_EXTRA_FIELDS = List.of(Option.IGNORING_ARRAY_ORDER, Option.IGNORING_EXTRA_FIELDS);

	private static String attachmentNotFound(final String contractId, final long attachmentId, final String municipalityId) {
		return """
			{
				"status": 404,
				"detail": "Contract with contractId '%s' and attachmentId '%s' is not present within municipality '%s'."
			}""".formatted(contractId, attachmentId, municipalityId);
	}

	private static String contractNotFound(final String contractId, final String municipalityId) {
		return """
			{
				"status": 404,
				"detail": "Contract with contractId '%s' is not present within municipality '%s'."
			}""".formatted(contractId, municipalityId);
	}

	private static String mimeTypeViolation() {
		return """
			{
				"status": 400,
				"violations": [
					{
						"field": "mimeType",
						"message": "must be a valid mime type on the form 'type/subtype', without parameters"
					}
				]
			}""";
	}

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
			// RFC 6266: an escaped, ISO-8859-1 transliterated 'filename' for old clients, plus a percent-encoded
			// 'filename*' carrying the real UTF-8 name
			.withExpectedResponseHeader(CONTENT_DISPOSITION, List.of("attachment; filename=\"someFile.pdf\"; filename*=UTF-8''someFile.pdf"))
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
		// Establish that the attachment is there to begin with, so the 404 at the end proves the delete did the work
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

	/**
	 * The round trip that matters for binary content: 64 KiB covering every one of the 256 byte values - null bytes, CR,
	 * LF and everything above 0x7F included - has to come back with not a single byte changed, at the same length, and
	 * with the SHA-256 the metadata advertises. A file this size also rules out the failure mode a 17-byte fixture cannot
	 * see, namely content truncated at a buffer boundary on its way in or out of the blob.
	 */
	@Test
	void test06_postBinaryAttachmentPreservesBytesAndHash() throws Exception {
		final var location = setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withRequestFile("file", "binaryContent.bin")
			.withExpectedResponseStatus(CREATED)
			.sendRequestAndVerifyResponse()
			.getResponseHeaders().getLocation().getPath();

		setupCall()
			.withServicePath(location)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_OCTET_STREAM_VALUE))
			.withExpectedResponseHeader(CONTENT_LENGTH, List.of("65536"))
			// Client-supplied content is always served as a download that the browser must not sniff a type for
			.withExpectedResponseHeader("X-Content-Type-Options", List.of("nosniff"))
			.withExpectedBinaryResponse("binaryContent.bin")
			.sendRequestAndVerifyResponse();

		// The stored hash is the SHA-256 of exactly those bytes
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The hash is derived from the content and from nothing else: the same bytes uploaded twice under different
	 * filenames, categories and mime types hash identically, while different bytes do not. That is what lets a client -
	 * or another service - recognize the same file without transferring it.
	 */
	@Test
	void test07_identicalContentProducesIdenticalHash() throws Exception {
		uploadAttachment(PATH, "attachment-first.json", "sameContent.bin");
		uploadAttachment(PATH, "attachment-second.json", "sameContent.bin");
		uploadAttachment(PATH, "attachment-other.json", "otherContent.bin");

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A Swedish filename cannot be carried verbatim in a header, which uses ISO-8859-1. The response therefore pairs a
	 * transliterated 'filename' with an RFC 5987 'filename*' holding the percent-encoded UTF-8 name - only the latter is
	 * asserted exactly, since what the transliteration produces is the client's problem and not the API's contract. The
	 * name itself is stored and returned intact in the JSON metadata.
	 */
	@Test
	void test08_nonAsciiFilenameIsEncodedInContentDisposition() throws Exception {
		final var location = uploadAttachment(PATH, "attachment.json", "someFile.pdf");

		setupCall()
			.withServicePath(location)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_DISPOSITION,
				List.of("attachment; filename=\"[^\"]*\"; filename\\*=UTF-8''Kontrakt-%C3%84rende-%C3%85%C3%A4%C3%B6\\.pdf"))
			.withExpectedBinaryResponse("someFile.pdf")
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The filename is client-supplied and ends up inside a quoted-string in the Content-Disposition header. A quote in it
	 * must therefore come back escaped rather than closing the quoted-string early - otherwise the rest of the name would
	 * be read as header parameters.
	 */
	@Test
	void test09_filenameWithQuotesIsEscapedInContentDisposition() throws Exception {
		final var location = uploadAttachment(PATH, "attachment.json", "someFile.pdf");

		setupCall()
			.withServicePath(location)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_DISPOSITION,
				List.of("attachment; filename=\"He said \\\"no\\\".pdf\"; filename*=UTF-8''He%20said%20%22no%22.pdf"))
			.withExpectedBinaryResponse("someFile.pdf")
			.sendRequestAndVerifyResponse();

		// The quote survives the JSON round trip unescaped-in-meaning, i.e. it is still one quote in the filename
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A contract without attachments is not an error - it is an empty list.
	 */
	@Test
	void test10_getAttachmentsForContractWithoutAttachments() {
		setupCall()
			.withServicePath(OTHER_CONTRACT_PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse("[]")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test11_getAttachmentsForUnknownContractReturnsNotFound() {
		setupCall()
			.withServicePath(UNKNOWN_CONTRACT_PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(contractNotFound("2024-99999", "1984"))
			.sendRequestAndVerifyResponse();
	}

	/**
	 * An attachment id is unique across the whole table, so the contract and the municipality in the path are the only
	 * thing keeping one tenant's content out of another's reach. Asking for the seed attachment through the wrong
	 * contract, or through the wrong municipality, has to be a 404 and not the file - and the attachment must still be
	 * reachable through its own path afterwards.
	 */
	@Test
	void test12_getAttachmentIsScopedToMunicipalityAndContract() {
		setupCall()
			.withServicePath(OTHER_CONTRACT_PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(attachmentNotFound("2024-23456", 1, "1984"))
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath("/2281/contracts/2024-12345/attachments/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(attachmentNotFound("2024-12345", 1, "2281"))
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH + "/999")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(attachmentNotFound("2024-12345", 999, "1984"))
			.sendRequestAndVerifyResponse();

		// None of that took the attachment away from the path it does belong to
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PDF_VALUE))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test13_createAttachmentForUnknownContractReturnsNotFound() throws Exception {
		setupCall()
			.withServicePath(UNKNOWN_CONTRACT_PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withRequestFile("file", "someFile.pdf")
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(contractNotFound("2024-99999", "1984"))
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The mime type is echoed into the Content-Type response header when the file is fetched, so a parameterised one is
	 * rejected on the way in rather than stored and served later.
	 */
	@Test
	void test14_createAttachmentWithInvalidMimeTypeIsRejected() throws Exception {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withRequestFile("file", "someFile.pdf")
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(mimeTypeViolation())
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	/**
	 * An attachment exists to carry content, so an empty file part is rejected rather than stored as a zero-byte
	 * attachment.
	 */
	@Test
	void test15_createAttachmentWithEmptyFileIsRejected() throws Exception {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withRequestFile("file", "empty.bin")
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse("""
				{
					"status": 400,
					"detail": "The 'file' part must not be empty"
				}""")
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	/**
	 * A patch cannot blank out the two fields the API requires an attachment to have: a whitespace-only filename or mime
	 * type is normalized to "not provided" and the stored value survives. The note has no such requirement, so an empty
	 * one is applied as sent - clearing a note is a legitimate thing to want.
	 */
	@Test
	void test16_patchWithBlankValuesKeepsExistingMetadata() {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		// The blanked mime type did not reach the header either
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PDF_VALUE))
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The content is immutable once uploaded, and the hash is derived from it on upload alone. Renaming and retyping an
	 * attachment therefore changes what the download headers say and nothing about the bytes or the hash - if the hash
	 * were re-derived, or dropped, from a metadata patch, this is where it would show.
	 */
	@Test
	void test17_patchMetadataLeavesContentAndHashIntact() throws Exception {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_XML_VALUE))
			.withExpectedResponseHeader(CONTENT_DISPOSITION, List.of("attachment; filename=\"renamed.pdf\"; filename*=UTF-8''renamed.pdf"))
			.withExpectedBinaryResponse("someFile.pdf")
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The patch payload is validated for the same reason the create payload is: a CRLF smuggled into the mime type would
	 * otherwise be stored and later split the response headers apart.
	 */
	@Test
	void test18_patchWithInvalidMimeTypeIsRejected() {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(mimeTypeViolation())
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();

		// The rejected value never reached the download header - the stored mime type is still the seed's
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PDF_VALUE))
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test19_patchUnknownAttachmentReturnsNotFound() {
		setupCall()
			.withServicePath(PATH + "/999")
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(attachmentNotFound("2024-12345", 999, "1984"))
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	@Test
	void test20_deleteUnknownAttachmentReturnsNotFound() {
		setupCall()
			.withServicePath(PATH + "/999")
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse(attachmentNotFound("2024-12345", 999, "1984"))
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	/**
	 * Deleting an attachment takes its content row with it by cascade. The row the delete is not aimed at has to be left
	 * alone - both its metadata and, more to the point, its bytes.
	 */
	@Test
	void test21_deleteOneAttachmentLeavesTheOthersIntact() throws Exception {
		final var first = uploadAttachment(PATH, "attachment-first.json", "firstContent.bin");
		final var second = uploadAttachment(PATH, "attachment-second.json", "secondContent.bin");

		setupCall()
			.withServicePath(first)
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(first)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(second)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_OCTET_STREAM_VALUE))
			.withExpectedBinaryResponse("secondContent.bin")
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * An attachment belongs to the contract it was posted to and to no other: it is absent from every other contract's
	 * list, and its id is not a key that unlocks it from under a different contract.
	 */
	@Test
	void test22_attachmentsAreScopedToTheirContract() throws Exception {
		final var location = uploadAttachment(OTHER_CONTRACT_PATH, "attachment.json", "someFile.pdf");

		setupCall()
			.withServicePath(OTHER_CONTRACT_PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();

		// The very same attachment id, asked for under the contract it does not belong to
		setupCall()
			.withServicePath(location.replace("2024-23456", "2024-12345"))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();
	}

	/**
	 * {@code id} and {@code hash} are documented as read-only, but that is OpenAPI prose - the same class is used for
	 * reading and for writing, so Jackson happily binds both from a create payload. What actually keeps a client from
	 * dictating them is the mapper, which does not copy either, and the service, which derives the hash from the bytes
	 * afterwards. The hash exists so that other services can trust that two attachments with the same value hold the
	 * same file, which a client-supplied one would quietly break.
	 */
	@Test
	void test23_clientSuppliedIdAndHashAreIgnored() throws Exception {
		final var location = uploadAttachment(PATH, "attachment.json", "someFile.pdf");

		assertThat(location).doesNotEndWith("/999");

		// The stored hash is the SHA-256 of the uploaded bytes, not the value that was sent in
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Both parts are mandatory. Neither the metadata nor the content is of any use on its own, so a request missing
	 * either is rejected outright rather than stored half-formed.
	 */
	@Test
	void test24_createAttachmentWithAMissingPartIsRejected() throws Exception {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("file", "someFile.pdf")
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	/**
	 * A patch that carries nothing is a no-op rather than an error - a client that computes its payload from a diff may
	 * legitimately end up with an empty one, and rejecting it would make "nothing changed" harder to handle than it is
	 * worth.
	 */
	@Test
	void test25_patchWithAnEmptyPayloadChangesNothing() {
		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	/**
	 * The filename and the mime type are both stored in a 255-character column and both end up in a response header, so
	 * the length bound is enforced at the API boundary rather than left to the database to truncate.
	 */
	@Test
	void test26_createAttachmentWithAnOversizedFilenameIsRejected() throws Exception {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", "attachment.json")
			.withRequestFile("file", "someFile.pdf")
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withJsonAssertOptions(IGNORE_EXTRA_FIELDS)
			.withExpectedResponse("""
				{
					"status": 400,
					"violations": [
						{
							"field": "filename",
							"message": "size must be between 1 and 255"
						}
					]
				}""")
			.sendRequestAndVerifyResponse();

		verifySeedAttachmentListIsUnchanged();
	}

	/**
	 * Posts an attachment and returns the path the Location header points at, so the caller can fetch it back without
	 * having to guess the generated id.
	 */
	private String uploadAttachment(final String servicePath, final String metadataFile, final String contentFile) throws Exception {
		final var location = setupCall()
			.withServicePath(servicePath)
			.withHttpMethod(POST)
			.withContentType(MULTIPART_FORM_DATA)
			.withRequestFile("attachment", metadataFile)
			.withRequestFile("file", contentFile)
			.withExpectedResponseStatus(CREATED)
			.sendRequestAndVerifyResponse()
			.getResponseHeaders().getLocation();

		assertThat(location).isNotNull();

		return location.getPath();
	}

	/**
	 * Asserts, through the API, that the seed contract's attachments are exactly as the test data left them.
	 */
	private void verifySeedAttachmentListIsUnchanged() {
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(SEED_ATTACHMENT_LIST)
			.sendRequestAndVerifyResponse();
	}
}
