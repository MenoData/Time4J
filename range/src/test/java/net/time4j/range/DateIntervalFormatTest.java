package net.time4j.range;

import net.time4j.PlainDate;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.ParseLog;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DateIntervalFormatTest {

    @Test
    public void printCustom1() throws IOException {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> startFormat = Iso8601Format.EXTENDED_CALENDAR_DATE;
        ChronoFormatter<PlainDate> endFormat = ChronoFormatter.ofDatePattern("MM-dd", PatternType.CLDR, Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        interval.print(startFormat, '/', endFormat, BracketPolicy.SHOW_ALWAYS, sb);
        assertThat(
            sb.toString(),
            is("[2014-02-27/05-14]"));
    }

    @Test
    public void printCustom2() {
        DateInterval interval = DateInterval.since(PlainDate.of(2015, 1, 1));
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("MMM d, yyyy", PatternType.CLDR, Locale.US);
        assertThat(
            interval.print(formatter, "since {0}"),
            is("since Jan 1, 2015")
        );
    }

    @Test
    public void printCustom3() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("MMM d, yyyy", PatternType.CLDR, Locale.US);
        assertThat(
            interval.print(formatter),
            is("Feb 27, 2014 – May 14, 2014")
        );
    }

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
            is("20140227/20140513"));
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
        DateInterval interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_ALWAYS),
            is("[20140227/20140514]"));
    }

    @Test
    public void printInfinitePastSHOW_ALWAYS() {
        PlainDate end = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.until(end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_ALWAYS),
            is("(-\u221E/20140514]"));
    }

    @Test
    public void printInfiniteFutureSHOW_ALWAYS() {
        PlainDate start = PlainDate.of(2014, 5, 14);
        DateInterval interval = DateInterval.since(start);
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
            is("-\u221E/20140513"));
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
        ChronoFormatter<PlainDate> formatter = Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            DateInterval.parse("20140227 - 20140514", formatter, "{0} - {1}"),
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

    @Test(expected=ParseException.class)
    public void parseTrailingSpace1() throws ParseException {
        DateInterval.parseISO("20120101/20140214 ");
    }

    @Test(expected=ParseException.class)
    public void parseTrailingSpace2() throws ParseException {
        DateInterval.parseISO("2012-01-01/2014-02-14 ");
    }

    @Test
    public void parseExtendedOpenCalendardate() throws ParseException {
        PlainDate start = PlainDate.of(2012, 1, 1);
        PlainDate end = PlainDate.of(2014, 2, 14);
        ParseLog plog = new ParseLog();

        DateInterval parsed = DateInterval.parse(
            "(2012-01-01/2014-02-14)",
            Iso8601Format.EXTENDED_CALENDAR_DATE,
            BracketPolicy.SHOW_ALWAYS,
            plog);

        assertThat(
            parsed.getStart(),
            is(Boundary.ofOpen(start)));
        assertThat(
            parsed.getEnd(),
            is(Boundary.ofOpen(end)));
    }

    @Test // open start equals open end
    public void parseInvalidOpenCalendardate() throws ParseException {
        ParseLog plog = new ParseLog();
        DateInterval.parse(
            "(2012-01-01/2012-01-01)",
            Iso8601Format.EXTENDED_CALENDAR_DATE,
            BracketPolicy.SHOW_ALWAYS,
            plog);
        assertThat(plog.isError(), is(true));
    }

    @Test
    public void parseCustomUS1() throws ParseException {
        PlainDate start = PlainDate.of(2015, 7, 20);
        PlainDate end = PlainDate.of(2015, 12, 31);
        DateInterval interval = DateInterval.between(start, end);
        ParseLog plog = new ParseLog();
        assertThat(
            DateInterval.parse(
                "July 20 / 2015 - December 31 / 2015",
                ChronoFormatter.ofDatePattern("MMMM d / uuuu ", PatternType.CLDR, Locale.US),
                '-',
                ChronoFormatter.ofDatePattern(" MMMM d / uuuu", PatternType.CLDR, Locale.US),
                BracketPolicy.SHOW_WHEN_NON_STANDARD,
                plog),
            is(interval));
    }

    @Test
    public void parseCustomUS2() throws ParseException {
        PlainDate start = PlainDate.of(2015, 7, 20);
        PlainDate end = PlainDate.of(2015, 12, 31);
        DateInterval interval = DateInterval.between(start, end);
        assertThat(
            DateInterval.parse(
                "July 20 / 2015 – December 31 / 2015",
                ChronoFormatter.ofDatePattern("MMMM d / uuuu", PatternType.CLDR, Locale.US)),
            is(interval));
    }

    @Test(expected=ParseException.class)
    public void parseCustomStartAfterEnd() throws ParseException {
        DateInterval.parse(
            "July 20 / 2016 – December 31 / 2015",
            ChronoFormatter.ofDatePattern("MMMM d / uuuu", PatternType.CLDR, Locale.US));
    }

    @Test
    public void parseAlways() throws ParseException {
        DateInterval always = DateInterval.ALWAYS;
        assertThat(
            DateInterval.parseISO("-∞/+∞"),
            is(always));
    }

}