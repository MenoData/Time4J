package net.time4j.format.expert;

import net.time4j.ClockUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
                .addFixedInteger(PlainTime.HOUR_FROM_0_TO_24, 2)
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
                .addFixedInteger(PlainTime.HOUR_FROM_0_TO_24, 2)
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
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .addFraction(PlainTime.NANO_OF_SECOND, 3, 6, true)
                .build(); // lenient mode
        try {
            formatter.parse("200000229174521123456");
        } catch (ParseException pe) {
            assertThat(
                pe.getMessage(),
                is("Cannot parse: \"200000229174521123456\" (expected: [.], found: [6])"));
            assertThat(
                pe.getErrorOffset(),
                is(20));
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
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
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
                is("Cannot parse: \"20000229174521123456\" (expected: [.], found: [2])"));
            assertThat(
                pe.getErrorOffset(),
                is(15));
            throw pe;
        }
    }

    @Test
    public void adjacentVariableHourDecimalMinute()
        throws ParseException {

        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter
                .setUp(PlainTime.class, Locale.ROOT)
                .addInteger(PlainTime.HOUR_FROM_0_TO_24, 1, 2)
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

    @Test
    public void adjacentTimestampPatternWithOptionalTime()
        throws ParseException {

        // pattern yyyyMMdd[HHmmss] would cause year parsing with effectively 9 digits
        // because the optional section does not reserve any character in adjacent digit parsing
        // so the chosen or-pattern is much more powerful and correct
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.ofTimestampPattern(
                "yyyyMMddHHmmss|yyyyMMdd",
                PatternType.CLDR,
                Locale.ROOT
            ).withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 0);

        assertThat(
            f.parse("19940513230000"),
            is(PlainTimestamp.of(1994, 5, 13, 23, 0)));
        assertThat(
            f.parse("19940513"),
            is(PlainTimestamp.of(1994, 5, 13, 0, 0)));
    }

}