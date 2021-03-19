package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class DecimalFormatTest {

    @Test
    public void ldap1() throws ParseException {
        ChronoFormatter<PlainDate> df =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ROOT)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .build();
        ChronoFormatter<Moment> mf =
            ChronoFormatter.setUp(Moment.axis(), Locale.US) // US for preference of dot in decimal elements
                .addCustomized(PlainDate.COMPONENT, df)
                .addFixedDecimal(PlainTime.DECIMAL_HOUR)
                .addTimezoneOffset(FormatStyle.SHORT, false, Collections.singletonList("Z"))
                .or()
                .addCustomized(PlainDate.COMPONENT, df)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE)
                .addTimezoneOffset(FormatStyle.SHORT, false, Collections.singletonList("Z"))
                .or()
                .addCustomized(PlainDate.COMPONENT, df)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .startOptionalSection()
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .startOptionalSection()
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .startOptionalSection()
                .addLiteral('.', ',')
                .addFraction(PlainTime.NANO_OF_SECOND, 1, 9, false)
                .endSection()
                .endSection()
                .endSection()
                .addTimezoneOffset(FormatStyle.SHORT, false, Collections.singletonList("Z"))
                .build();
        assertThat(
            mf.parse("199412160532-0500").toString(),
            is("1994-12-16T10:32:00Z"));
        assertThat(
            mf.parse("199412160532Z").toString(),
            is("1994-12-16T05:32:00Z"));
        assertThat(
            mf.parse("20161231185960.123456789-0500").toString(),
            is("2016-12-31T23:59:60,123456789Z"));
        assertThat(
            mf.parse("201612311859.25-0500").toString(),
            is("2016-12-31T23:59:15Z"));
        assertThat(
            mf.parse("2016123118.25-0500").toString(),
            is("2016-12-31T23:15:00Z"));
        assertThat(
            mf.getPattern(),
            is(""));
    }

    @Test
    public void ldap2() throws ParseException {
        ChronoFormatter<PlainDate> df =
            ChronoFormatter.setUp(PlainDate.axis(), Locale.ROOT)
                .addFixedInteger(PlainDate.YEAR, 4)
                .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                .build();
        ChronoFormatter<Moment> mf =
            ChronoFormatter.setUp(Moment.axis(), Locale.US) // US for preference of dot in decimal elements
                .addCustomized(PlainDate.COMPONENT, df)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .startOptionalSection()
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .startOptionalSection()
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .startOptionalSection()
                .addLiteral('.', ',')
                .addFraction(PlainTime.NANO_OF_SECOND, 1, 9, false)
                .endSection()
                .endSection()
                .endSection()
                .addTimezoneOffset(FormatStyle.SHORT, false, Collections.singletonList("Z"))
                .or()
                .addCustomized(PlainDate.COMPONENT, df)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE)
                .addTimezoneOffset(FormatStyle.SHORT, false, Collections.singletonList("Z"))
                .or()
                .addCustomized(PlainDate.COMPONENT, df)
                .addFixedDecimal(PlainTime.DECIMAL_HOUR)
                .addTimezoneOffset(FormatStyle.SHORT, false, Collections.singletonList("Z"))
                .build();
        assertThat(
            mf.parse("199412160532-0500").toString(),
            is("1994-12-16T10:32:00Z"));
        assertThat(
            mf.parse("199412160532Z").toString(),
            is("1994-12-16T05:32:00Z"));
        assertThat(
            mf.parse("20161231185960.123456789-0500").toString(),
            is("2016-12-31T23:59:60,123456789Z"));
        assertThat(
            mf.parse("201612311859.25-0500").toString(),
            is("2016-12-31T23:59:15Z"));
        assertThat(
            mf.parse("2016123118.25-0500").toString(),
            is("2016-12-31T23:15:00Z"));
    }

    @Test
    public void parseDecimalHour() throws ParseException {
        String input = "2017-03-01 13.52Z";
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("uuuu-MM-dd ", PatternType.CLDR)
                .addFixedDecimal(PlainTime.DECIMAL_HOUR, 4, 2)
                .addTimezoneOffset()
                .build();
        assertThat(
            formatter.parse(input), // 2017-03-01T13:31:12Z
            is(PlainTimestamp.of(2017, 3, 1, 13, 31, 12).atUTC()));
    }

    @Test
    public void momentFormatterPrintingDecimalMinute() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("uuuu-MM-dd HH:", PatternType.CLDR)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build()
                .withTimezone(ZonalOffset.UTC);
        assertThat(
            formatter.format(Moment.UNIX_EPOCH),
            is("1970-01-01 00:00.0"));
    }

    @Test
    public void momentFormatterParsingDecimalMinute() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("uuuu-MM-dd HH:", PatternType.CLDR)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build()
                .withTimezone(ZonalOffset.UTC);
        assertThat(
            formatter.parse("1970-01-01 00:00.0"),
            is(Moment.UNIX_EPOCH));
    }

    @Test
    public void printVariableHourDecimalMinute() {

        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter
                .setUp(PlainTime.class, Locale.ROOT)
                .addInteger(PlainTime.HOUR_FROM_0_TO_24, 1, 2)
                .addFixedDecimal(PlainTime.DECIMAL_MINUTE, 3, 1)
                .build();
        assertThat(
            formatter.format(PlainTime.of(17, 8, 30)),
            is("1708,5"));
        assertThat(
            formatter.format(PlainTime.of(7, 8, 30)),
            is("708,5"));
    }

}
