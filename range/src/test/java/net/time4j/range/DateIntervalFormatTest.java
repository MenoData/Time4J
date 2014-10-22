package net.time4j.range;

import net.time4j.Iso8601Format;
import net.time4j.PlainDate;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DateIntervalFormatTest {

    @Test
    public void printSHOW_NEVER() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_NEVER),
            is("20140227/20140514"));
        assertThat(
            interval.withOpenEnd().print(formatter, BracketPolicy.SHOW_NEVER),
            is("20140227/20140514"));
    }

    @Test
    public void printSHOW_WHEN_NON_STANDARD() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is("20140227/20140514"));
        assertThat(
            interval.withOpenEnd().print(
                formatter,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is("[20140227/20140514)"));
    }

    @Test
    public void printSHOW_ALWAYS() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_ALWAYS),
            is("[20140227/20140514]"));
    }

    @Test
    public void printInfinitePastSHOW_ALWAYS() {
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.until(end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_ALWAYS),
            is("(-\u221E/20140514]"));
    }

    @Test
    public void printInfiniteFutureSHOW_ALWAYS() {
        PlainDate start = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.since(start);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_ALWAYS),
            is("[20140514/+\u221E)"));
    }

    @Test
    public void printInfinitePastSHOW_WHEN_NON_STANDARD() {
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.until(end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is("(-\u221E/20140514]"));
        assertThat(
            interval.withOpenEnd().print(
                formatter,
                BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is("(-\u221E/20140514)"));
    }

    @Test
    public void printInfiniteFutureSHOW_WHEN_NON_STANDARD() {
        PlainDate start = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.since(start);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is("[20140514/+\u221E)"));
    }

    @Test
    public void printInfinitePastSHOW_NEVER() {
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.until(end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_NEVER),
            is("-\u221E/20140514"));
        assertThat(
            interval.withOpenEnd().print(
                formatter,
                BracketPolicy.SHOW_NEVER),
            is("-\u221E/20140514"));
    }

    @Test
    public void printInfiniteFutureSHOW_NEVER() {
        PlainDate start = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.since(start);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_NEVER),
            is("20140514/+\u221E"));
    }

    @Test
    public void parseBasicCalendarDateSHOW_NEVER() throws ParseException {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;

        assertThat(
            IntervalParser.of(
                DateIntervalFactory.INSTANCE,
                formatter,
                BracketPolicy.SHOW_NEVER
            ).parse("20140227/20140514"),
            is(interval));
    }

    @Test(expected=ParseException.class)
    public void parseBasicCalendarDateExSHOW_NEVER() throws ParseException {
        IntervalParser.of(
            DateIntervalFactory.INSTANCE,
            Iso8601Format.BASIC_CALENDAR_DATE,
            BracketPolicy.SHOW_NEVER
        ).parse("[20140227/20140514]");
    }

    @Test(expected=ParseException.class)
    public void parseBasicCalendarDateExSHOW_WHEN_NON_STANDARD()
        throws ParseException {

        IntervalParser.of(
            DateIntervalFactory.INSTANCE,
            Iso8601Format.BASIC_CALENDAR_DATE,
            BracketPolicy.SHOW_WHEN_NON_STANDARD
        ).parse("[20140227/20140514]");
    }

    @Test(expected=ParseException.class)
    public void parseBasicCalendarDateExSHOW_ALWAYS() throws ParseException {
        IntervalParser.of(
            DateIntervalFactory.INSTANCE,
            Iso8601Format.BASIC_CALENDAR_DATE,
            BracketPolicy.SHOW_ALWAYS
        ).parse("20140227/20140514");
    }

    @Test
    public void parseBasicCalendarDateSHOW_WHEN_NON_STANDARD()
        throws ParseException {

        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            IntervalParser.of(
                DateIntervalFactory.INSTANCE,
                formatter,
                BracketPolicy.SHOW_WHEN_NON_STANDARD
            ).parse("20140227/20140514"),
            is(interval));
    }

    @Test
    public void parseBasicCalendarDateSHOW_ALWAYS() throws ParseException {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            IntervalParser.of(
                DateIntervalFactory.INSTANCE,
                formatter,
                BracketPolicy.SHOW_ALWAYS
            ).parse("[20140227/20140514]"),
            is(interval));
    }

    @Test
    public void parseCustom() throws ParseException {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        ParseLog plog = new ParseLog();
        assertThat(
            IntervalParser.of(
                DateIntervalFactory.INSTANCE,
                formatter,
                ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                    .startSection(Attributes.PROTECTED_CHARACTERS, 5)
                    .addFixedInteger(PlainDate.YEAR, 4)
                    .endSection()
                    .startSection(Attributes.PROTECTED_CHARACTERS, 3)
                    .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                    .endSection()
                    .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                    .build(),
                BracketPolicy.SHOW_ALWAYS
            ).parse("[20140227/0514]", plog, formatter.getDefaultAttributes()),
            is(interval));
    }

    @Test
    public void parseBasicIsoCalendardate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("20120101/20140214"),
            is(expected));
    }

    @Test
    public void parseExtendedIsoCalendardate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-01-01/2014-02-14"),
            is(expected));
    }

    @Test
    public void parseBasicIsoOrdinaldate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 12, 31);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012001/2014365"),
            is(expected));
    }

    @Test
    public void parseExtendedIsoOrdinaldate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 12, 31);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-001/2014-365"),
            is(expected));
    }

    @Test
    public void parseBasicIsoWeekdate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 2);
        PlainDate end = PlainDate.of(2014, 1, 30);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012W011/2014W054"),
            is(expected));
    }

    @Test
    public void parseExtendedIsoWeekdate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 2);
        PlainDate end = PlainDate.of(2014, 1, 30);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-W01-1/2014-W05-4"),
            is(expected));
    }

    @Test
    public void parseIsoOrdinaldateAbbreviated() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2012, 12, 31);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-001/366"),
            is(expected));
    }

    @Test
    public void parseIsoWeekdateAbbreviated1() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 2);
        PlainDate end = PlainDate.of(2012, 2, 2);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-W01-1/W05-4"),
            is(expected));
    }

    @Test
    public void parseIsoWeekdateAbbreviated2() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 2);
        PlainDate end = PlainDate.of(2012, 1, 5);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-W01-1/4"),
            is(expected));
    }

    @Test
    public void parseIsoCalendardateAbbreviated1() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2012, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-01-01/02-14"),
            is(expected));
    }

    @Test
    public void parseIsoCalendardateAbbreviated2() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2012, 1, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-01-01/14"),
            is(expected));
    }

    @Test(expected=ParseException.class)
    public void parseBasicIsoWeekdateAbbreviatedMissingWOY()
        throws ParseException {
        DateInterval.parseISO("2012W011/2012");
    }

    @Test(expected=ParseException.class)
    public void parseExtendedIsoOrdinaldateAbbreviatedMissingDOY()
        throws ParseException {
        DateInterval.parseISO("2012-001/2012-");
    }

    @Test(expected=ParseException.class)
    public void parseBasicIsoOrdinaldateAbbreviatedMissingDOY()
        throws ParseException {
        DateInterval.parseISO("2012001/2012");
    }

    @Test
    public void parsePeriodAndCalendarDate1() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        DateInterval expected = DateInterval.atomic(start);

        assertThat(
            DateInterval.parseISO("P0D/2012-01-01"),
            is(expected));
    }

    @Test
    public void parsePeriodAndCalendarDate2() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2012, 1, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("P13D/2012-01-14"),
            is(expected));
    }

    @Test
    public void parsePeriodAndCalendarDate3() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("P775D/2014-02-14"),
            is(expected));
    }

    @Test
    public void parseCalendarDateAndPeriod() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012-01-01/P775D"),
            is(expected));
    }

    @Test
    public void parsePeriodAndOrdinalDate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("P775D/2014045"),
            is(expected));
    }

    @Test
    public void parseOrdinalDateAndPeriod() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        DateInterval expected = DateInterval.between(start, end);

        assertThat(
            DateInterval.parseISO("2012001/P775D"),
            is(expected));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void parseEmpty() throws ParseException {
        DateInterval.parseISO("");
    }

    @Test(expected=ParseException.class)
    public void parseNoSolidus() throws ParseException {
        DateInterval.parseISO("2012001P775D");
    }

}