package se.sundsvall.contract.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.geojson.FeatureCollection;

import se.sundsvall.dept44.common.validators.annotation.OneOf;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(setterPrefix = "with")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LandLeaseContract extends Contract {

	/**
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.LandLeaseType}
	 */
	@Schema(description = "Type of lease", example = "LEASEHOLD")
	@OneOf({"LEASEHOLD", "USUFRUCT", "SITELEASEHOLD"})
	private String landLeaseType;

	@Schema(description = "Type of leasehold")
	private Leasehold leaseholdType;

	/**
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.UsufructType}
	 */
	@Schema(description = "Type of right of use", example = "HUNTING")
	@OneOf({"HUNTING", "FISHING", "MAINTENANCE", "OTHER"})
	private String usufructType;

	@Schema(description = "External referenceId", example = "123")
	private String externalReferenceId;

	@NotBlank
	@Schema(description = "Property designation", example = "SUNDSVALL NORRMALM 1:1", requiredMode = Schema.RequiredMode.REQUIRED)
	private String propertyDesignation;

	@Schema(description = "Object identity (from Lantmäteriet)", example = "909a6a80-d1a4-90ec-e040-ed8f66444c3f", requiredMode = Schema.RequiredMode.REQUIRED)
	private String objectIdentity;

	@Schema(description = "The duration of the lease in years", example = "9")
	private Integer leaseDuration;

	@Schema(description = "Yearly lease fee", example = "4350")
	private BigDecimal rental;

	/**
	 * Backed by enum {@link se.sundsvall.contract.api.model.enums.IntervalType}
	 */
	@Schema(description = "How often the lease is invoiced", example = "QUARTERLY")
	@OneOf({"YEARLY", "QUARTERLY", "MONTHLY"})
	private String invoiceInterval;

	@Schema(description = "Lease period start date", example = "2020-01-01", format = "date")
	private LocalDate start;

	@Schema(description = "Lease period end date", example = "2022-12-31", format = "date")
	private LocalDate end;

	@Schema(description = "Marker for whether an agreement should be extended automatically or not", example = "true", defaultValue = "true")
	private Boolean autoExtend;

	@Schema(description = "Extension period in days", example = "30")
	private Integer leaseExtension;

	@Schema(description = "Termination period in days", example = "30")
	private Integer periodOfNotice;

	@Schema(description = "Leased area (m2)", example = "150")
	private Integer area;

	@Schema(description = "Part(s) of property covered by the lease. Described by GeoJSON using polygon(s)", example = "{\n        \"type\": \"FeatureCollection\",\n        \"features\": [\n            {\n                \"type\": \"Feature\",\n                \"properties\": {},\n                \"geometry\": {\n                    \"type\": \"Polygon\",\n                    \"coordinates\": [\n                        [\n                            [\n                                1730072021484375,\n                                6238137830626575\n                            ],\n                            [\n                                17297286987304688,\n                                6238050291927199\n                            ],\n                            [\n                                17297801971435547,\n                                6237922958346664\n                            ],\n                            [\n                                17301406860351562,\n                                62378194958300895\n                            ],\n                            [\n                                17303810119628906,\n                                62379149998183046\n                            ],\n                            [\n                                17303638458251953,\n                                6238066208244492\n                            ],\n                            [\n                                1730072021484375,\n                                6238137830626575\n                            ]\n                        ]\n                    ]\n                }\n            }\n        ]\n    }")
	private FeatureCollection areaData;

}
