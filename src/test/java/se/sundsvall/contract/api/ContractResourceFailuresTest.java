package se.sundsvall.contract.api;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.contract.Application;
import se.sundsvall.contract.api.model.Contract;
import se.sundsvall.contract.api.model.Extension;
import se.sundsvall.contract.api.model.Invoicing;
import se.sundsvall.contract.api.model.PatchContract;
import se.sundsvall.contract.api.model.Stakeholder;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.IntervalType;
import se.sundsvall.contract.model.enums.InvoicedIn;
import se.sundsvall.contract.model.enums.Status;
import se.sundsvall.contract.service.ContractService;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Verifies that the bean-validation constraints declared on the request models are actually enforced at the resource
 * layer (i.e. an invalid payload is rejected with 400 before the service is ever invoked). The service is mocked, so
 * these tests exercise Spring's {@code @Valid} handling and the constraints on {@link Contract} / {@link PatchContract}
 * and their nested models — not the service-layer {@code ContractValidator} (which is covered by
 * ContractValidatorTest).
 */
@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class ContractResourceFailuresTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String CONTRACT_ID = "2024-12345";
	private static final String BASE_PATH = "/{municipalityId}/contracts";
	private static final String ID_PATH = "/{municipalityId}/contracts/{contractId}";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private ContractService contractServiceMock;

	/** A payload that passes all bean-validation constraints (only status + type are @NotNull). */
	private static Contract.ContractBuilder validContract() {
		return Contract.builder()
			.withStatus(Status.ACTIVE)
			.withType(ContractType.LEASE_AGREEMENT);
	}

	private void postExpectingBadRequest(final Object body) {
		webTestClient.post()
			.uri(BASE_PATH, MUNICIPALITY_ID)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(contractServiceMock);
	}

	private void patchExpectingBadRequest(final String jsonBody) {
		webTestClient.patch()
			.uri(ID_PATH, MUNICIPALITY_ID, CONTRACT_ID)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(jsonBody)
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(contractServiceMock);
	}

	// ----------------------------------------------------------------------------------------------------------
	// @NotNull on Contract.status / Contract.type
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithoutStatusIsRejected() {
		postExpectingBadRequest(Contract.builder().withType(ContractType.LEASE_AGREEMENT).build());
	}

	@Test
	void postWithoutTypeIsRejected() {
		postExpectingBadRequest(Contract.builder().withStatus(Status.ACTIVE).build());
	}

	// ----------------------------------------------------------------------------------------------------------
	// fees.additionalInformation: @NotBlank @Size(min = 1, max = 30) (POST / PATCH)
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithBlankAdditionalInformationIsRejected() {
		final var contract = validContract()
			.withFees(Fees.builder().withAdditionalInformation(List.of("   ")).build())
			.build();

		postExpectingBadRequest(contract);
	}

	@Test
	void postWithTooLongAdditionalInformationIsRejected() {
		final var contract = validContract()
			.withFees(Fees.builder().withAdditionalInformation(List.of("a".repeat(31))).build())
			.build();

		postExpectingBadRequest(contract);
	}

	@Test
	void patchWithBlankAdditionalInformationIsRejected() {
		patchExpectingBadRequest("""
			{
				"fees": {
					"additionalInformation": ["   "]
				}
			}
			""");
	}

	// ----------------------------------------------------------------------------------------------------------
	// fees index trio: @AssertTrue hasConsistentIndexFields (POST / PATCH)
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithPartialFeeIndexIsRejected() {
		final var contract = validContract()
			.withFees(Fees.builder().withIndexType("KPI 80").build())
			.build();

		postExpectingBadRequest(contract);
	}

	@Test
	void postWithNonPositiveIndexNumberIsRejected() {
		final var contract = validContract()
			.withFees(Fees.builder()
				.withIndexType("KPI 80")
				.withIndexYear(2021)
				.withIndexNumber(BigDecimal.ZERO)
				.build())
			.build();

		postExpectingBadRequest(contract);
	}

	@Test
	void patchWithPartialFeeIndexIsRejected() {
		patchExpectingBadRequest("""
			{
				"fees": {
					"indexType": "KPI 80"
				}
			}
			""");
	}

	// ----------------------------------------------------------------------------------------------------------
	// status / type: @NotNull — a mandatory field may be omitted on a PATCH but may not be set to null
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void patchWithNullStatusIsRejected() {
		patchExpectingBadRequest("""
			{
				"status": null
			}
			""");
	}

	@Test
	void patchWithNullTypeIsRejected() {
		patchExpectingBadRequest("""
			{
				"type": null
			}
			""");
	}

	// ----------------------------------------------------------------------------------------------------------
	// stakeholders.partyId: @ValidUuid — nested validation cascades into patched list elements
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void patchWithInvalidStakeholderPartyIdIsRejected() {
		patchExpectingBadRequest("""
			{
				"stakeholders": [
					{
						"partyId": "not-a-uuid"
					}
				]
			}
			""");
	}

	// ----------------------------------------------------------------------------------------------------------
	// fees.indexationRate: @DecimalMin(0.0) @DecimalMax(1.0)
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithIndexationRateAboveOneIsRejected() {
		final var contract = validContract()
			.withFees(Fees.builder().withIndexationRate(new BigDecimal("1.5")).build())
			.build();

		postExpectingBadRequest(contract);
	}

	// ----------------------------------------------------------------------------------------------------------
	// Stakeholder.partyId: @ValidUuid(nullable = true)
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithInvalidStakeholderPartyIdIsRejected() {
		final var contract = validContract()
			.withStakeholders(List.of(Stakeholder.builder().withPartyId("not-a-uuid").build()))
			.build();

		postExpectingBadRequest(contract);
	}

	// ----------------------------------------------------------------------------------------------------------
	// Extension.@AssertTrue: autoExtend = true requires leaseExtension + unit
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithInconsistentExtensionIsRejected() {
		final var contract = validContract()
			.withExtension(Extension.builder().withAutoExtend(true).build())
			.build();

		postExpectingBadRequest(contract);
	}

	// ----------------------------------------------------------------------------------------------------------
	// Invoicing.invoiceInterval / Invoicing.invoicedIn: @NotNull when an invoicing object is present (POST / PATCH)
	// ----------------------------------------------------------------------------------------------------------

	@Test
	void postWithInvoicingMissingInvoicedInIsRejected() {
		final var contract = validContract()
			.withInvoicing(Invoicing.builder().withInvoiceInterval(IntervalType.QUARTERLY).build())
			.build();

		postExpectingBadRequest(contract);
	}

	@Test
	void postWithInvoicingMissingInvoiceIntervalIsRejected() {
		final var contract = validContract()
			.withInvoicing(Invoicing.builder().withInvoicedIn(InvoicedIn.ADVANCE).build())
			.build();

		postExpectingBadRequest(contract);
	}

	@Test
	void postWithEmptyInvoicingIsRejected() {
		final var contract = validContract()
			.withInvoicing(Invoicing.builder().build())
			.build();

		postExpectingBadRequest(contract);
	}
}
