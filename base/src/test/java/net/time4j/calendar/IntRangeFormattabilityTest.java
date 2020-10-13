package net.time4j.calendar;

import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class IntRangeFormattabilityTest {

    @Test
    public void printMinguo() {
        ChronoFormatter<MinguoCalendar> fmt =
            ChronoFormatter.setUp(MinguoCalendar.class, Locale.US)
                .addFixedInteger(MinguoCalendar.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedNumerical(MinguoCalendar.MONTH_OF_YEAR, 2)
                .addLiteral('.')
                .addInteger(MinguoCalendar.YEAR_OF_ERA, 4, 10)
                .addLiteral(' ')
                .addText(MinguoCalendar.ERA)
                .build();
        assertThat(
            fmt.format(MinguoCalendar.axis().getMinimum()),
            is("01.01.1000001911 Before R.O.C."));
    }

    @Test
    public void printThaiSolar() {
        ChronoFormatter<ThaiSolarCalendar> fmt =
            ChronoFormatter.setUp(ThaiSolarCalendar.class, Locale.US)
                .addFixedInteger(ThaiSolarCalendar.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedNumerical(ThaiSolarCalendar.MONTH_OF_YEAR, 2)
                .addLiteral('.')
                .addInteger(ThaiSolarCalendar.YEAR_OF_ERA, 4, 10)
                .build();
        assertThat(
            fmt.format(ThaiSolarCalendar.axis().getMaximum()),
            is("31.12.1000000542"));
    }

    @Test
    public void parseMinguo() throws ParseException {
        ChronoFormatter<MinguoCalendar> fmt =
            ChronoFormatter.setUp(MinguoCalendar.class, Locale.US)
                .addFixedInteger(MinguoCalendar.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedNumerical(MinguoCalendar.MONTH_OF_YEAR, 2)
                .addLiteral('.')
                .addInteger(MinguoCalendar.YEAR_OF_ERA, 4, 10)
                .addLiteral(' ')
                .addText(MinguoCalendar.ERA)
                .build();
        assertThat(
            fmt.parse("01.01.1000001911 Before R.O.C."),
            is(MinguoCalendar.axis().getMinimum()));
    }

    @Test
    public void parseThaiSolar() throws ParseException {
        ChronoFormatter<ThaiSolarCalendar> fmt =
            ChronoFormatter.setUp(ThaiSolarCalendar.class, Locale.US)
                .addFixedInteger(ThaiSolarCalendar.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedNumerical(ThaiSolarCalendar.MONTH_OF_YEAR, 2)
                .addLiteral('.')
                .addInteger(ThaiSolarCalendar.YEAR_OF_ERA, 4, 10)
                .build()
                .withDefault(ThaiSolarCalendar.ERA, ThaiSolarEra.BUDDHIST);
        assertThat(
            fmt.parse("31.12.1000000542"),
            is(ThaiSolarCalendar.axis().getMaximum()));
    }

    @Test
    public void parseThaiSolarBeyondMax1() throws ParseException {
        ChronoFormatter<ThaiSolarCalendar> fmt =
            ChronoFormatter.setUp(ThaiSolarCalendar.class, Locale.US)
                .addFixedInteger(ThaiSolarCalendar.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedNumerical(ThaiSolarCalendar.MONTH_OF_YEAR, 2)
                .addLiteral('.')
                .addInteger(ThaiSolarCalendar.YEAR_OF_ERA, 4, 10)
                .build()
                .withDefault(ThaiSolarCalendar.ERA, ThaiSolarEra.BUDDHIST);
        try {
            fmt.parse("31.12.2000000000");
            fail("Missing parse exception.");
        } catch (ParseException pe) {
            assertThat(
                pe.getMessage(),
                is(
                    "Validation failed => Invalid Thai calendar date. "
                    + "[parsed={DAY_OF_MONTH=31, YEAR_OF_ERA=2000000000, MONTH_OF_YEAR=DECEMBER, ERA=BUDDHIST}]"));
            assertThat(
                pe.getErrorOffset(),
                is(16));
        }
    }

    @Test
    public void parseThaiSolarBeyondMax2() throws ParseException {
        ChronoFormatter<ThaiSolarCalendar> fmt =
            ChronoFormatter.setUp(ThaiSolarCalendar.class, Locale.US)
                .addFixedInteger(ThaiSolarCalendar.DAY_OF_MONTH, 2)
                .addLiteral('.')
                .addFixedNumerical(ThaiSolarCalendar.MONTH_OF_YEAR, 2)
                .addLiteral('.')
                .addInteger(ThaiSolarCalendar.YEAR_OF_ERA, 4, 10)
                .build()
                .withDefault(ThaiSolarCalendar.ERA, ThaiSolarEra.BUDDHIST);
        try {
            fmt.parse("31.12.3000000000");
            fail("Missing parse exception.");
        } catch (ParseException pe) {
            assertThat(
                pe.getMessage(),
                is("Parsed number does not fit into an integer: 3000000000"));
            assertThat(
                pe.getErrorOffset(),
                is(6));
        }
    }

}