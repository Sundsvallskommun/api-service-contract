package se.sundsvall.contract.api.model;

import java.util.List;

import jakarta.validation.Valid;

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
public class ContractHolder {

	@Valid
	@ArraySchema(schema = @Schema(description = "Lista med arrendeavtal."))
	private List<LandLeaseContract> landLeaseContracts;


}
