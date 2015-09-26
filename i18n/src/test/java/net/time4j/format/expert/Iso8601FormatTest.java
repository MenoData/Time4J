package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.Weekday;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class Iso8601FormatTest {

    @Test
    public void printBasicCalendarDate() {
        assertThat(
            Iso8601Format.BASIC_CALENDAR_DATE.format(
                PlainDate.of(2012, 2, 29)),
            is("20120229"));
    }

    @Test
    public void parseBasicCalendarDate() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_CALENDAR_DATE.parse("20120229"),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test
    public void printExtendedCalendarDate() {
        assertThat(
            Iso8601Format.EXTENDED_CALENDAR_DATE.format(
                PlainDate.of(2012, 2, 29)),
            is("2012-02-29"));
    }

    @Test
    public void parseExtendedCalendarDate() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_CALENDAR_DATE.parse("2012-02-29"),
            is(PlainDate.of(2012, 2, 29)));
    }

    @Test
    public void printBasicOrdinalDate() {
        assertThat(
            Iso8601Format.BASIC_ORDINAL_DATE.format(
                PlainDate.of(2014, 365)),
            is("2014365"));
    }

    @Test
    public void parseBasicOrdinalDate() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_ORDINAL_DATE.parse("2014365"),
            is(PlainDate.of(2014, 365)));
    }

    @Test
    public void printExtendedOrdinalDate32() {
        assertThat(
            Iso8601Format.EXTENDED_ORDINAL_DATE.format(
                PlainDate.of(2014, 32)),
            is("2014-032"));
    }

    @Test
    public void parseExtendedOrdinalDate32() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_ORDINAL_DATE.parse("2014-032"),
            is(PlainDate.of(2014, 32)));
    }

    @Test
    public void printExtendedOrdinalDate365() {
        assertThat(
            Iso8601Format.EXTENDED_ORDINAL_DATE.format(
                PlainDate.of(2014, 365)),
            is("2014-365"));
    }

    @Test
    public void parseExtendedOrdinalDate365() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_ORDINAL_DATE.parse("2014-365"),
            is(PlainDate.of(2014, 365)));
    }

    @Test
    public void printBasicWeekDate() {
        assertThat(
            Iso8601Format.BASIC_WEEK_DATE.format(
                PlainDate.of(2014, 4, Weekday.MONDAY)),
            is("2014W041"));
    }

    @Test
    public void parseBasicWeekDate() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_WEEK_DATE.parse("2014W041"),
            is(PlainDate.of(2014, 4, Weekday.MONDAY)));
    }

    @Test
    public void printExtendedWeekDate() {
        assertThat(
            Iso8601Format.EXTENDED_WEEK_DATE.format(
                PlainDate.of(2014, 4, Weekday.MONDAY)),
            is("2014-W04-1"));
    }

    @Test
    public void parseExtendedWeekDate() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WEEK_DATE.parse("2014-W04-1"),
            is(PlainDate.of(2014, 4, Weekday.MONDAY)));
    }

    @Test
    public void printExtendedWeekDateKW53() {
        assertThat(
            Iso8601Format.EXTENDED_WEEK_DATE.format(
                PlainDate.of(2009, 12, 28)),
            is("2009-W53-1"));
    }

    @Test
    public void parseExtendedWeekDateKW53Valid() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WEEK_DATE.parse("2009-W53-1"),
            is(PlainDate.of(2009, 12, 28)));
    }

    @Test(expected=ParseException.class)
    public void parseExtendedWeekDateKW53Invalid() throws ParseException {
        Iso8601Format.EXTENDED_WEEK_DATE.parse("2014-W53-1");
    }

    @Test
    public void printBasicTime() {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.format(
                PlainTime.of(23, 59, 28)),
            is("235928"));
    }

    @Test
    public void parseBasicTime() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.parse("235928"),
            is(PlainTime.of(23, 59, 28)));
    }

    @Test
    public void printBasicTimeHHMMSSffffff() {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.format(
                PlainTime.of(23, 59, 28, 123456000)),
            is("235928,123456"));
    }

    @Test
    public void parseBasicTimeHHMMSSffffff() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.parse("235928,123456"),
            is(PlainTime.of(23, 59, 28, 123456000)));
    }

    @Test
    public void printExtendedTime24() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.midnightAtEndOfDay()),
            is("24:00"));
    }

    @Test
    public void parseExtendedTime24() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("24:00"),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void printExtendedTimeHH() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23)),
            is("23:00"));
    }

    @Test
    public void parseExtendedTimeHH() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23"),
            is(PlainTime.of(23)));
    }

    @Test
    public void printExtendedTimeHHMM() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23, 59)),
            is("23:59"));
    }

    @Test
    public void parseExtendedTimeHHMM() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59"),
            is(PlainTime.of(23, 59)));
    }

    @Test
    public void printExtendedTimeHHMMSS() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23, 59, 28)),
            is("23:59:28"));
    }

    @Test
    public void parseExtendedTimeHHMMSS() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28"),
            is(PlainTime.of(23, 59, 28)));
    }

    @Test
    public void printExtendedTimeHHMMSSff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23, 59, 28, 120000000)),
            is("23:59:28,12"));
    }

    @Test
    public void parseExtendedTimeHHMMSSff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,12"),
            is(PlainTime.of(23, 59, 28, 120000000)));
    }

    @Test
    public void printExtendedTimeHHMMSSfff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23, 59, 28, 123000000)),
            is("23:59:28,123"));
    }

    @Test
    public void parseExtendedTimeHHMMSSfff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,123"),
            is(PlainTime.of(23, 59, 28, 123000000)));
    }

    @Test
    public void printExtendedTimeHHMMSSffffff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23, 59, 28, 123456000)),
            is("23:59:28,123456"));
    }

    @Test
    public void parseExtendedTimeHHMMSSffffff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,123456"),
            is(PlainTime.of(23, 59, 28, 123456000)));
    }

    @Test
    public void printExtendedTimeHHMMSSfffffffff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.format(
                PlainTime.of(23, 59, 28, 123456789)),
            is("23:59:28,123456789"));
    }

    @Test
    public void parseExtendedTimeHHMMSSfffffffff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,123456789"),
            is(PlainTime.of(23, 59, 28, 123456789)));
    }

    @Test
    public void parseExtendedTimeWithDecimalPoint() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28.123456789"),
            is(PlainTime.of(23, 59, 28, 123456789)));
    }

    @Test
    public void printExtendedDateTimeOffsetZ() {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET.format(
                PlainDate.of(2012, 6, 30)
                    .at(PlainTime.of(23, 59, 59))
                    .atUTC()
                    .plus(1, SI.SECONDS)),
            is("2012-06-30T23:59:60Z"));
    }

    @Test
    public void parseExtendedDateTimeOffsetZ() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .parse("2012-06-30T23:59:60Z"),
            is(
                PlainDate.of(2012, 6, 30)
                .at(PlainTime.of(23, 59, 59))
                .atUTC()
                .plus(1, SI.SECONDS)));
    }

    @Test
    public void printExtendedDateTimeOffsetPlus02() {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .withTimezone(ZonalOffset.ofTotalSeconds(7200)).format(
                PlainDate.of(2012, 6, 30)
                    .at(PlainTime.of(23, 59, 59))
                    .atUTC()
                    .plus(1, SI.SECONDS)),
            is("2012-07-01T01:59:60+02:00"));
    }

    @Test
    public void parseExtendedDateTimeOffsetPlus02() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .parse("2012-07-01T01:59:60+02:00"),
            is(
                PlainDate.of(2012, 6, 30)
                .at(PlainTime.of(23, 59, 59))
                .atUTC()
                .plus(1, SI.SECONDS)));
    }

    @Test
    public void printExtendedDateTimeOffsetMinus0530() {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .withTimezone(ZonalOffset.ofTotalSeconds(-3600 * 5 - 30 * 60))
                .format(
                    PlainDate.of(2012, 6, 30)
                        .at(PlainTime.of(23, 59, 59))
                        .atUTC()
                        .plus(1, SI.SECONDS)),
            is("2012-06-30T18:29:60-05:30"));
    }

    @Test
    public void parseExtendedDateTimeOffsetMinus0530() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .parse("2012-06-30T18:29:60-05:30"),
            is(
                PlainDate.of(2012, 6, 30)
                .at(PlainTime.of(23, 59, 59))
                .atUTC()
                .plus(1, SI.SECONDS)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void printExtendedDateTimeOffsetInvalid() {
        Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .withTimezone(ZonalOffset.ofTotalSeconds(-1))
                .format(
                    PlainDate.of(2012, 6, 30)
                        .at(PlainTime.of(23, 59, 59))
                        .atUTC()
                        .plus(1, SI.SECONDS));
    }

    @Test
    public void printExtendedDateTime() {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME.format(
                PlainDate.of(2012, 6, 30)
                    .at(PlainTime.of(23, 59, 59))),
            is("2012-06-30T23:59:59"));
    }

    @Test
    public void parseExtendedDateTime() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME
                .parse("2012-06-30T23:59:59"),
            is(
                PlainDate.of(2012, 6, 30)
                .at(PlainTime.of(23, 59, 59))));
    }

    @Test
    public void parseExtendedDateTimeStrict24() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME
                .with(Attributes.LENIENCY, Leniency.STRICT)
                .parse("2012-06-30T24:00"),
            is(PlainDate.of(2012, 7, 1).atStartOfDay()));
    }

    @Test(expected=ParseException.class)
    public void parseExtendedDateTimeStrict2401() throws ParseException {
        Iso8601Format.EXTENDED_DATE_TIME.parse("2012-06-30T24:01");
    }

    @Test
    public void parseExtendedDateTimeSmart24() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME
                .with(Attributes.LENIENCY, Leniency.SMART)
                .parse("2012-06-30T24:00"),
            is(PlainDate.of(2012, 7, 1).atStartOfDay()));
    }

    @Test(expected=ParseException.class)
    public void parseExtendedDateTimeSmart27() throws ParseException {
        Iso8601Format.EXTENDED_DATE_TIME
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("2012-06-30T27:45");
    }

    @Test
    public void parseExtendedDateTimeLax27() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME
                .with(Attributes.LENIENCY, Leniency.LAX)
                .parse("2012-06-30T27:45"),
            is(PlainTimestamp.of(2012, 7, 1, 3, 45)));
    }

    @Test(expected=ParseException.class)
    public void parseExtendedDateSmartApril31() throws ParseException {
        Iso8601Format.EXTENDED_CALENDAR_DATE
            .with(Attributes.LENIENCY, Leniency.SMART)
            .parse("2012-04-31");
    }

    @Test
    public void parseExtendedDateLaxApril31() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_CALENDAR_DATE
                .with(Attributes.LENIENCY, Leniency.LAX)
                .parse("2012-04-31"),
            is(PlainDate.of(2012, 5, 1)));
    }

}
