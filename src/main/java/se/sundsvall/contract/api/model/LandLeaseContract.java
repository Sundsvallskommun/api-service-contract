package se.sundsvall.contract.api.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.geojson.FeatureCollection;

import se.sundsvall.contract.api.model.enums.IntervalType;
import se.sundsvall.contract.api.model.enums.LandLeaseType;
import se.sundsvall.contract.api.model.enums.Status;
import se.sundsvall.contract.api.model.enums.UsufructType;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Arrendeavtal")
public class LandLeaseContract {

	@Schema(description = "Version på kontraktet (databas-version). Anges endast vid PUT och PATCH.")
	Integer version;

	@Schema(description = "Status på kontraktet.", example = "ACTIVE")
	Status status;

	@Schema(description = "Eventuellt ID på relaterat ärende.", example = "100")
	Long caseId;

	@Schema(description = "Indexvillkor.")
	String indexTerms;

	@Schema(description = "Beskrivning av ändamål (fritext).", example = "En beskrivning av ändamålet med kontraktet.")
	String description;

	@Schema(description = "Övriga villkor.")
	String additionalTerms;

	@ArraySchema(schema = @Schema(description = "Lista med intressenter."))
	List<Stakeholder> stakeholders;

	@ArraySchema(schema = @Schema(description = "Lista med adresser."))
	List<Attachment> attachments;

	@Schema(description = "Typ av arrende.", example = "LEASEHOLD")
	LandLeaseType landLeaseType;

	@Schema(description = "Typ av arrende.", example = "Leasehold")
	Leasehold leaseholdType;

	@Schema(description = "Typ av nyttjanderätt.", example = "Jakt")
	UsufructType usufructType;

	@Schema(description = "Extern referens", example = "123")
	String externalReferenceId;

	@NotBlank
	@Schema(description = "Fastighetsbeteckning.", example = "SUNDSVALL NORRMALM 1:1", requiredMode = Schema.RequiredMode.REQUIRED)
	String propertyDesignation;

	@Schema(description = "Objektidentitet (från Lantmäteriet).", example = "909a6a80-d1a4-90ec-e040-ed8f66444c3f", requiredMode = Schema.RequiredMode.REQUIRED)
	String objectIdentity;

	@Schema(description = "Arrendetid.")
	Duration leaseDuration;

	@Schema(description = "Arrendeavgift per år (kr).", example = "4350")
	BigDecimal rental;

	@Schema(description = "Faktureringsintervall.", example = "QUARTERLY")
	IntervalType invoiceInterval;

	@Schema(description = "Upplåtelsetid - start.", example = "2020-01-01", format = "date")
	LocalDate start;

	@Schema(description = "Upplåtelsetid - slut.", example = "2022-12-31", format = "date")
	LocalDate end;

	@Schema(description = "Markör för om ett avtal skall förlängas automatiskt eller ej", example = "true", defaultValue = "true")
	Boolean autoExtend;

	@Schema(description = "Förlängningstid.")
	Duration leaseExtension;

	@Schema(description = "Uppsägningstid.")
	Duration periodOfNotice;

	@Schema(description = "Arrendeareal (m2).", example = "150")
	Integer area;

	@Schema(description = "Del(ar) av fastighet som omfattas av arrendet. Beskrivs av GeoJSON mha polygon(er)", example = "{\n        \"type\": \"FeatureCollection\",\n        \"features\": [\n            {\n                \"type\": \"Feature\",\n                \"properties\": {},\n                \"geometry\": {\n                    \"type\": \"Polygon\",\n                    \"coordinates\": [\n                        [\n                            [\n                                17.30072021484375,\n                                62.38137830626575\n                            ],\n                            [\n                                17.297286987304688,\n                                62.38050291927199\n                            ],\n                            [\n                                17.297801971435547,\n                                62.37922958346664\n                            ],\n                            [\n                                17.301406860351562,\n                                62.378194958300895\n                            ],\n                            [\n                                17.303810119628906,\n                                62.379149998183046\n                            ],\n                            [\n                                17.303638458251953,\n                                62.38066208244492\n                            ],\n                            [\n                                17.30072021484375,\n                                62.38137830626575\n                            ]\n                        ]\n                    ]\n                }\n            }\n        ]\n    }")
	FeatureCollection areaData;

}
