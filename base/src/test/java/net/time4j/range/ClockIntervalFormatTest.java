package net.time4j.range;

import net.time4j.ClockUnit;
import net.time4j.PlainTime;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;

import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.IsoDecimalStyle;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ClockIntervalFormatTest {

    @Test
    public void printSHOW_NEVER() throws IOException {
        PlainTime start = PlainTime.of(12, 0, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        ChronoFormatter<PlainTime> formatter =
            Iso8601Format.EXTENDED_WALL_TIME;
        StringBuilder buffer = new StringBuilder();
        interval.print(formatter, '/', formatter, BracketPolicy.SHOW_NEVER, InfinityStyle.ABORT, buffer);
        assertThat(
            buffer.toString(),
            is("12:00/14:15:30"));
        buffer = new StringBuilder();
        interval.withOpenEnd().print(formatter, '/', formatter, BracketPolicy.SHOW_NEVER, InfinityStyle.ABORT, buffer);
        assertThat(
            buffer.toString(),
            is("12:00/14:15:30"));
    }

    @Test
    public void printSHOW_WHEN_NON_STANDARD() throws IOException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        ChronoFormatter<PlainTime> formatter =
            Iso8601Format.BASIC_WALL_TIME;
        StringBuilder buffer = new StringBuilder();
        interval.print(formatter, '/', formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD, InfinityStyle.ABORT, buffer);
        assertThat(
            buffer.toString(),
            is("1220/141530"));
        buffer = new StringBuilder();
        interval.withClosedEnd().print(
            formatter, '/', formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD, InfinityStyle.ABORT, buffer);
        assertThat(
            buffer.toString(),
            is("[1220/141530]"));
    }

    @Test
    public void printSHOW_ALWAYS() throws IOException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        ChronoFormatter<PlainTime> formatter =
            Iso8601Format.BASIC_WALL_TIME;
        StringBuilder buffer = new StringBuilder();
        interval.print(formatter, '/', formatter, BracketPolicy.SHOW_ALWAYS, InfinityStyle.ABORT, buffer);
        assertThat(
            buffer.toString(),
            is("[1220/141530)"));
    }

    @Test
    public void parseISOBasic() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("1220/141530"),
            is(interval));
    }

    @Test
    public void parseISOExtended() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30, 123000000);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("12:20/14:15:30.123"),
            is(interval));
        assertThat(
            ClockInterval.parseISO("12:20/14:15:30,123"),
            is(interval));
    }

    @Test
    public void parseISO24() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.midnightAtEndOfDay();
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("12:20/24:00:00,000"),
            is(interval));
    }

    @Test
    public void parseISOExtendedWithStartPeriod() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("PT1H55M30S/14:15:30"),
            is(interval));
    }

    @Test
    public void parseISOExtendedWithEndPeriod() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("12:20/PT1H55M30S"),
            is(interval));
    }

    @Test
    public void parseISOBasicWithStartPeriod() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("PT1H55M30S/141530"),
            is(interval));
    }

    @Test
    public void parseISOBasicWithEndPeriod() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("1220/PT1H55M30S"),
            is(interval));
    }

    @Test
    public void parseISOBasicWithAlternativePeriod() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parseISO("PT01:55:30/14:15:30"),
            is(interval));
    }

    @Test
    public void parseCustom() throws ParseException {
        PlainTime start = PlainTime.of(12, 20, 0);
        PlainTime end = PlainTime.of(14, 15, 30);
        ClockInterval interval = ClockInterval.between(start, end);
        ParseLog plog = new ParseLog();
        assertThat(
            ClockInterval.parse(
                "[1220/141530)",
                Iso8601Format.BASIC_WALL_TIME, '/', Iso8601Format.BASIC_WALL_TIME, BracketPolicy.SHOW_ALWAYS, plog),
            is(interval));
        plog.reset();
        assertThat(
            ClockInterval.parse(
                "[1220-141530)",
                Iso8601Format.BASIC_WALL_TIME, '-', Iso8601Format.BASIC_WALL_TIME, BracketPolicy.SHOW_ALWAYS, plog),
            is(interval));
        plog.reset();
        assertThat(
            ClockInterval.parse(
                "1220~141530",
                Iso8601Format.BASIC_WALL_TIME, '~', Iso8601Format.BASIC_WALL_TIME,
                BracketPolicy.SHOW_WHEN_NON_STANDARD, plog),
            is(interval));
    }

    @Test
    public void parseHHMM1() {
        PlainTime start = PlainTime.of(7, 20);
        PlainTime end = PlainTime.of(24, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        ParseLog plog = new ParseLog();
        assertThat(
            ClockInterval.parse(
                "07:20 - 24:00",
                ChronoFormatter.ofTimePattern("HH:mm ", PatternType.CLDR_24, Locale.ROOT),
                '-',
                ChronoFormatter.ofTimePattern(" HH:mm", PatternType.CLDR_24, Locale.ROOT),
                BracketPolicy.SHOW_WHEN_NON_STANDARD,
                plog),
            is(interval));
    }

    @Test(expected=ParseException.class)
    public void parseHHMM2() throws ParseException {
        ClockInterval.parse(
            "07:20 - 24:00",
            ChronoFormatter.ofTimePattern("HH:mm", PatternType.CLDR_24, Locale.ROOT));
    }

    @Test
    public void parseHHMM3() throws ParseException {
        PlainTime start = PlainTime.of(7, 20);
        PlainTime end = PlainTime.of(24, 0);
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(
            ClockInterval.parse(
                "07:20 – 24:00",
                ChronoFormatter.ofTimePattern("HH:mm", PatternType.CLDR_24, Locale.US)),
            is(interval));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void parseHHMM4() throws ParseException {
        ClockInterval.parse(
            "",
            ChronoFormatter.ofTimePattern("HH:mm", PatternType.CLDR_24, Locale.ROOT));
    }

    @Test(expected=ParseException.class)
    public void parseHHMM5() throws ParseException {
        ClockInterval.parse(
            "[07:20/24:00)",
            ChronoFormatter.ofTimePattern("HH:mm", PatternType.CLDR_24, Locale.ROOT),
            "[{0}/{1})");
    }

    @Test(expected=ParseException.class)
    public void parseTrailingSpace() throws ParseException {
        ClockInterval.parseISO("1745/2015 ");
    }

    @Test
    public void formatBasicISO() {
        PlainTime start = PlainTime.of(8, 30, 15).plus(123450, ClockUnit.MICROS);
        PlainTime end = PlainTime.midnightAtEndOfDay();
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(interval.formatBasicISO(IsoDecimalStyle.DOT, ClockUnit.MICROS), is("083015.123450/240000.000000"));
    }

    @Test
    public void formatExtendedISO() {
        PlainTime start = PlainTime.of(8, 30, 15).plus(123456, ClockUnit.MICROS);
        PlainTime end = PlainTime.midnightAtEndOfDay();
        ClockInterval interval = ClockInterval.between(start, end);
        assertThat(interval.formatExtendedISO(IsoDecimalStyle.DOT, ClockUnit.MILLIS), is("08:30:15.123/24:00:00.000"));
    }

    @Test
    public void parseFullRange() throws ParseException {
        ClockInterval fullRange =
            ClockInterval.between(PlainTime.midnightAtStartOfDay(), PlainTime.midnightAtEndOfDay());
        assertThat(
            ClockInterval.parseISO("00:00/24:00"),
            is(fullRange)); // open end
        assertThat(
            ClockInterval.parse("[00:00/24:00]", Iso8601Format.EXTENDED_WALL_TIME, BracketPolicy.SHOW_ALWAYS),
            is(fullRange.withClosedEnd()));
    }

    @Test(expected=ParseException.class)
    public void parseInfinity1() throws ParseException {
        ClockInterval.parseISO("-/24:00");
    }

    @Test(expected=ParseException.class)
    public void parseInfinity2() throws ParseException {
        ClockInterval.parseISO("00:00/-");
    }

    @Test(expected=ParseException.class)
    public void parseInfinity3() throws ParseException {
        ClockInterval.parseISO("-\u221E/24:00");
    }

    @Test(expected=ParseException.class)
    public void parseInfinity4() throws ParseException {
        ClockInterval.parseISO("00:00/+\u221E");
    }

}