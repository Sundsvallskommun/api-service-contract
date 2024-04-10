package se.sundsvall.contract.integration.db.model.converter;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.PersistenceException;

import org.junit.jupiter.api.Test;

import se.sundsvall.contract.model.LeaseFees;

class LeaseFeesConverterTest {

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

    private final LeaseFeesConverter converter = new LeaseFeesConverter();

    @Test
    void convertToDatabaseColumnWithNullInputReturnsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToDatabaseColumn() {
        var leaseFees = LeaseFees.builder()
            .withCurrency("USD")
            .withYearly(BigDecimal.valueOf(123.45))
            .withMonthly(BigDecimal.valueOf(234.56))
            .withTotal(BigDecimal.valueOf(345.67))
            .withTotalAsText("three hundred forty five point six seven")
            .withIndexYear(1984)
            .withIndexNumber(7)
            .withAdditionalInformation(List.of("aaa", "bbb"))
            .build();

        var result = converter.convertToDatabaseColumn(leaseFees);

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
            .withMessage("Unable to deserialize lease fees");
    }

    @Test
    void convertToEntityAttribute() {
        var result = converter.convertToEntityAttribute(JSON);

        assertThat(result).isNotNull().satisfies(leaseFees -> {
            assertThat(leaseFees.getCurrency()).isEqualTo("USD");
            assertThat(leaseFees.getYearly()).isEqualTo(BigDecimal.valueOf(123.45));
            assertThat(leaseFees.getMonthly()).isEqualTo(BigDecimal.valueOf(234.56));
            assertThat(leaseFees.getTotal()).isEqualTo(BigDecimal.valueOf(345.67));
            assertThat(leaseFees.getTotalAsText()).isEqualTo("three hundred forty five point six seven");
            assertThat(leaseFees.getIndexYear()).isEqualTo(1984);
            assertThat(leaseFees.getIndexNumber()).isEqualTo(7);
            assertThat(leaseFees.getAdditionalInformation()).containsExactly("aaa", "bbb");
        });
    }
}
