package se.sundsvall.contract.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "Term")
public class Term {

    @JsonProperty("term")
    @Schema(description = "Term (name)", example = "Parties")
    private String name;

    @Schema(description = "Term description", example = "The parties involved in the lease agreement")
    private String description;
}
