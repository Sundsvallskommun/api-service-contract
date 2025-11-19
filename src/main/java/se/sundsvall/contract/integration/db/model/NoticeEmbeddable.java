package se.sundsvall.contract.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.contract.integration.db.model.converter.enums.PartyConverter;
import se.sundsvall.contract.integration.db.model.converter.enums.TimeUnitConverter;
import se.sundsvall.contract.model.enums.Party;
import se.sundsvall.contract.model.enums.TimeUnit;

@Embeddable
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class NoticeEmbeddable {

	@Column(name = "party", nullable = false)
	@Convert(converter = PartyConverter.class)
	private Party party;

	@Column(name = "period_of_notice", nullable = false)
	private Integer periodOfNotice;

	@Column(name = "unit", length = 32, nullable = false)
	@Convert(converter = TimeUnitConverter.class)
	private TimeUnit unit;
}
