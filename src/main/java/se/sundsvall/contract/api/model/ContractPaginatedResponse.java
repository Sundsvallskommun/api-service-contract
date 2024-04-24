package se.sundsvall.contract.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import se.sundsvall.dept44.models.api.paging.PagingMetaData;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Schema(description = "Paginated response for contracts")
public class ContractPaginatedResponse {

	@JsonProperty("_meta")
	@Schema(implementation = PagingMetaData.class, accessMode = READ_ONLY)
	private PagingMetaData metaData;

	@ArraySchema(schema = @Schema(implementation = Contract.class))
	private List<Contract> contracts;
}
