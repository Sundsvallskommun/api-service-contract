package se.sundsvall.contract.integration.db.model.converter;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.Fees;

import jakarta.persistence.PersistenceException;

class FeesConverterTest {

    public static final String JSON = """
        {
            "currency": "USD",
            "yearly": 123.45,
            "monthly": 234.56,
            "total": 345.67,
            "totalAsText": "three hundred forty five point six seven",
            "indexYear": 1984,
            "indexNumber": 7,
            "additionalInformation": [ "aaa","bbb" ]
        }
        """;

    private final FeesConverter converter = new FeesConverter();

    @Test
    void convertToDatabaseColumnWithNullInputReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToDatabaseColumn() {
        var fees = Fees.builder()
            .withCurrency("USD")
            .withYearly(BigDecimal.valueOf(123.45))
            .withMonthly(BigDecimal.valueOf(234.56))
            .withTotal(BigDecimal.valueOf(345.67))
            .withTotalAsText("three hundred forty five point six seven")
            .withIndexYear(1984)
            .withIndexNumber(7)
            .withAdditionalInformation(List.of("aaa", "bbb"))
            .build();

        var result = converter.convertToDatabaseColumn(fees);

        assertThatJson(result).isEqualTo(JSON);
    }

    @Test
    void convertToEntityAttributeWithNullInputReturnsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttributeWithBlankInputReturnsNull() {
        assertThat(converter.convertToEntityAttribute("")).isNull();
    }

    @Test
    void convertToEntityAttributeWithInvalidInputThrowsException() {
        assertThatExceptionOfType(PersistenceException.class)
            .isThrownBy(() -> converter.convertToEntityAttribute("not-json"))
            .withMessage("Unable to deserialize fees");
    }

    @Test
    void convertToEntityAttribute() {
        var result = converter.convertToEntityAttribute(JSON);

        assertThat(result).isNotNull().satisfies(fees -> {
            assertThat(fees.getCurrency()).isEqualTo("USD");
            assertThat(fees.getYearly()).isEqualTo(BigDecimal.valueOf(123.45));
            assertThat(fees.getMonthly()).isEqualTo(BigDecimal.valueOf(234.56));
            assertThat(fees.getTotal()).isEqualTo(BigDecimal.valueOf(345.67));
            assertThat(fees.getTotalAsText()).isEqualTo("three hundred forty five point six seven");
            assertThat(fees.getIndexYear()).isEqualTo(1984);
            assertThat(fees.getIndexNumber()).isEqualTo(7);
            assertThat(fees.getAdditionalInformation()).containsExactly("aaa", "bbb");
        });
    }
}
