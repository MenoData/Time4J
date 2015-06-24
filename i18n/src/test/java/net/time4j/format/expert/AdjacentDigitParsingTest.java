package net.time4j.format.expert;

import net.time4j.ClockUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;

import java.text.ParseException;
import java.util.Locale;

import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class AdjacentDigitParsingTest {

    @Test
    public void endingWithTwoDigitYear() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter
                .setUp(PlainDate.class, Locale.ROOT)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addTwoDigitYear(PlainDate.YEAR)
                .build();
        assertThat(
            formatter.parse("022900"),
            is(PlainDate.of(2000, 2, 29)));
        assertThat(
            formatter.parse("02282001"),
            is(PlainDate.of(2001, 2, 28)));
    }

    @Test
    public void adjacentYearMonthDay() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter
                .setUp(PlainDate.class, Locale.ROOT)
                .addInteger(PlainDate.YEAR, 4, 9)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .build();
        assertThat(
            formatter.parse("20000229"),
            is(PlainDate.of(2000, 2, 29)));
        assertThat(
            formatter.parse("1234567890228"),
            is(PlainDate.of(123456789, 2, 28)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void adjacentVariableYearMonthDayHourFraction1() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.ROOT)
                .addInteger(PlainDate.YEAR, 4, 9)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.ISO_HOUR, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, false)
                .build();
        formatter.parse("20000229174521123456");
    }

    @Test
    public void adjacentVariableYearMonthDayHourFraction2() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.ROOT)
                .addInteger(PlainDate.YEAR, 4, 9)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.ISO_HOUR, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 6, 6, false)
                .build();
        assertThat(
            formatter.parse("2000022917123456"),
            is(PlainTimestamp
                .of(2000, 2, 29, 17, 0)
                .with(PlainTime.NANO_OF_SECOND, 123456000)));
    }

    @Test
    public void adjacentFixedDateTimeFraction1() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.ROOT)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, false)
                .build();
        assertThat(
            formatter.parse("20000229174521123456"),
            is(PlainTimestamp.of(2000, 2, 29, 17, 45, 21)
                .plus(123456, ClockUnit.MICROS)));
    }

    @Test
    public void adjacentFixedDateTimeFraction2() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.US)
                .addInteger(PlainDate.YEAR, 4, 4)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, true)
                .build()
                .with(Attributes.LENIENCY, Leniency.STRICT);
        assertThat(
            formatter.parse("20000229174521.123456"),
            is(PlainTimestamp.of(2000, 2, 29, 17, 45, 21)
                             .plus(123456, ClockUnit.MICROS)));
    }

    @Test
    public void adjacentFixedDateTimeFraction3() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.US)
                .addInteger(PlainDate.YEAR, 4, 4)
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, true)
                .build()
                .with(Attributes.LENIENCY, Leniency.LAX);
        assertThat(
            formatter.parse("20000229174521.123456"),
            is(PlainTimestamp.of(2000, 2, 29, 17, 45, 21)
                             .plus(123456, ClockUnit.MICROS)));
    }

    @Test(expected=ParseException.class)
    public void adjacentFixedDateTimeFraction4() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.US)
                .startSection(Attributes.LENIENCY, Leniency.LAX)
                .addInteger(PlainDate.YEAR, 4, 4) // no fixed-width!
                .endSection()
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, true)
                .build(); // lenient mode
        try {
            formatter.parse("20000229174521123456");
        } catch (ParseException pe) {
            assertThat(
                pe.getMessage(),
                is("[MONTH_OF_YEAR] No enum found for value: 74"));
            assertThat(
                pe.getErrorOffset(),
                is(9));
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void adjacentFixedDateTimeFraction5() throws ParseException {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter
                .setUp(PlainTimestamp.class, Locale.US)
                .startSection(Attributes.LENIENCY, Leniency.SMART)
                .addInteger(PlainDate.YEAR, 4, 5) // no fixed-width!
                .endSection()
                .addFixedNumerical(PlainDate.MONTH_OF_YEAR, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, true)
                .build(); // lenient mode
        try {
            formatter.parse("20000229174521123456");
        } catch (ParseException pe) {
            assertThat(
                pe.getMessage(),
                is("[MONTH_OF_YEAR] No enum found for value: 22"));
            assertThat(
                pe.getErrorOffset(),
                is(5));
            throw pe;
        }
    }

    @Test
    public void adjacentVariableHourDecimalMinute()
        throws ParseException {

        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter
                .setUp(PlainTime.class, Locale.ROOT)
                .addInteger(PlainTime.ISO_HOUR, 1, 2)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build();
        assertThat(
            formatter.parse("1708.5"),
            is(PlainTime.of(17, 8, 30)));
        assertThat(
            formatter.parse("708,5"),
            is(PlainTime.of(7, 8, 30)));
    }

    @Test
    public void adjacentMonthDayYear_yyyy()
        throws ParseException {

        assertThat(
            ChronoFormatter.ofDatePattern("Mddyyyy", PatternType.CLDR, Locale.ROOT).parse("8302011"),
            is(PlainDate.of(2011, 8, 30)));
    }

    @Test
    public void adjacentMonthDayYear_uuuu()
        throws ParseException {

        assertThat(
            ChronoFormatter.ofDatePattern("Mdduuuu", PatternType.CLDR, Locale.ROOT).parse("8302011"),
            is(PlainDate.of(2011, 8, 30)));
    }

    @Test
    public void adjacentMonthDayYear_uu_threeten()
        throws ParseException {

        assertThat(
            ChronoFormatter.ofDatePattern("Mdduu", PatternType.THREETEN, Locale.ROOT).parse("83011"),
            is(PlainDate.of(2011, 8, 30)));
    }

}