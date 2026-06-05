package se.sundsvall.contract.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geojson.FeatureCollection;
import org.openapitools.jackson.nullable.JsonNullable;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Status;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Schema(description = "Partial contract payload used for PATCH (JSON Merge Patch semantics): a field that is omitted is "
	+ "left unchanged, a field set to null is cleared, and a field set to a value is updated.")
public class PatchContract {

	@Schema(description = "A description of the contract", examples = "A simple description of the contract")
	@Builder.Default
	private JsonNullable<String> description = JsonNullable.undefined();

	@Schema(description = "External referenceId", examples = "123")
	@Builder.Default
	private JsonNullable<String> externalReferenceId = JsonNullable.undefined();

	@Schema(description = "Type of lease")
	@Builder.Default
	private JsonNullable<LeaseType> leaseType = JsonNullable.undefined();

	@Schema(description = "Object identity (from Lantmäteriet)", examples = "909a6a80-d1a4-90ec-e040-ed8f66444c3f")
	@Builder.Default
	private JsonNullable<String> objectIdentity = JsonNullable.undefined();

	@Schema(description = "Contract status")
	@Builder.Default
	private JsonNullable<Status> status = JsonNullable.undefined();

	@Schema(description = "Contract type")
	@Builder.Default
	private JsonNullable<ContractType> type = JsonNullable.undefined();

	@Schema(description = "Type of leasehold")
	@Builder.Default
	private JsonNullable<Leasehold> leasehold = JsonNullable.undefined();

	@ArraySchema(schema = @Schema(description = "Additional terms for the contract"))
	@Builder.Default
	private JsonNullable<List<TermGroup>> additionalTerms = JsonNullable.undefined();

	@Schema(description = "Extra parameters")
	@Builder.Default
	private JsonNullable<List<ExtraParameterGroup>> extraParameters = JsonNullable.undefined();

	@ArraySchema(schema = @Schema(description = "Index terms for the contract"))
	@Builder.Default
	private JsonNullable<List<TermGroup>> indexTerms = JsonNullable.undefined();

	@ArraySchema(schema = @Schema(description = "Property designations"))
	@Builder.Default
	private JsonNullable<List<PropertyDesignation>> propertyDesignations = JsonNullable.undefined();

	@ArraySchema(schema = @Schema(description = "List of stakeholders"))
	@Builder.Default
	private JsonNullable<List<Stakeholder>> stakeholders = JsonNullable.undefined();

	@Schema(description = "Lease extension")
	@Builder.Default
	private JsonNullable<Extension> extension = JsonNullable.undefined();

	@Valid
	@Schema(description = "Fee details")
	@Builder.Default
	private JsonNullable<Fees> fees = JsonNullable.undefined();

	@Schema(description = "Invoicing details")
	@Builder.Default
	private JsonNullable<Invoicing> invoicing = JsonNullable.undefined();

	@Schema(description = "Start date of the contract", examples = "2020-01-01", format = "date")
	@Builder.Default
	private JsonNullable<LocalDate> startDate = JsonNullable.undefined();

	@Schema(description = "End date of the contract. Set when the contract is terminated", examples = "2022-12-31", format = "date")
	@Builder.Default
	private JsonNullable<LocalDate> endDate = JsonNullable.undefined();

	@Schema(description = "Notice details")
	@Builder.Default
	private JsonNullable<Notice> notice = JsonNullable.undefined();

	@Schema(description = "Current contract period")
	@Builder.Default
	private JsonNullable<Period> currentPeriod = JsonNullable.undefined();

	@Schema(description = "Leased area (m2)", examples = "150")
	@Builder.Default
	private JsonNullable<Integer> area = JsonNullable.undefined();

	@Schema(description = "Whether the contract is signed by a witness")
	@Builder.Default
	private JsonNullable<Boolean> signedByWitness = JsonNullable.undefined();

	@Schema(description = "Part(s) of property covered by the lease. Described by GeoJSON using polygon(s)")
	@Builder.Default
	private JsonNullable<FeatureCollection> areaData = JsonNullable.undefined();
}
