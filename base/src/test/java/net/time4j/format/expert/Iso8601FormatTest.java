package net.time4j.format.expert;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.Weekday;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class Iso8601FormatTest {

    @Test
    public void printBasicCalendarDate() {
        assertThat(
            Iso8601Format.BASIC_CALENDAR_DATE.print(
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
            Iso8601Format.EXTENDED_CALENDAR_DATE.print(
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
            Iso8601Format.BASIC_ORDINAL_DATE.print(
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
            Iso8601Format.EXTENDED_ORDINAL_DATE.print(
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
            Iso8601Format.EXTENDED_ORDINAL_DATE.print(
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
            Iso8601Format.BASIC_WEEK_DATE.print(
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
            Iso8601Format.EXTENDED_WEEK_DATE.print(
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
            Iso8601Format.EXTENDED_WEEK_DATE.print(
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
            Iso8601Format.BASIC_WALL_TIME.print(
                PlainTime.of(23, 59, 28)),
            is("235928"));
    }

    @Test
    public void parseBasicTime() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.parse("235928"),
            is(PlainTime.of(23, 59, 28)));
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.parse("T235928"),
            is(PlainTime.of(23, 59, 28)));
    }

    @Test
    public void printBasicTimeHHMMSSffffff() {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.print(
                PlainTime.of(23, 59, 28, 123456000)),
            is("235928,123456"));
    }

    @Test
    public void parseBasicTimeHHMMSSffffff() throws ParseException {
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.parse("235928,123456"),
            is(PlainTime.of(23, 59, 28, 123456000)));
        assertThat(
            Iso8601Format.BASIC_WALL_TIME.parse("T235928,123456"),
            is(PlainTime.of(23, 59, 28, 123456000)));
    }

    @Test
    public void printExtendedTime24() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.midnightAtEndOfDay()),
            is("24:00"));
    }

    @Test
    public void parseExtendedTime24() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("24:00"),
            is(PlainTime.midnightAtEndOfDay()));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T24:00"),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void printExtendedTimeHH() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.of(23)),
            is("23:00"));
    }

    @Test
    public void parseExtendedTimeHH() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23"),
            is(PlainTime.of(23)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23"),
            is(PlainTime.of(23)));
    }

    @Test
    public void printExtendedTimeHHMM() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.of(23, 59)),
            is("23:59"));
    }

    @Test
    public void parseExtendedTimeHHMM() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59"),
            is(PlainTime.of(23, 59)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59"),
            is(PlainTime.of(23, 59)));
    }

    @Test
    public void printExtendedTimeHHMMSS() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.of(23, 59, 28)),
            is("23:59:28"));
    }

    @Test
    public void parseExtendedTimeHHMMSS() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28"),
            is(PlainTime.of(23, 59, 28)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59:28"),
            is(PlainTime.of(23, 59, 28)));
    }

    @Test
    public void printExtendedTimeHHMMSSff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.of(23, 59, 28, 120000000)),
            is("23:59:28,12"));
    }

    @Test
    public void parseExtendedTimeHHMMSSff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,12"),
            is(PlainTime.of(23, 59, 28, 120000000)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59:28,12"),
            is(PlainTime.of(23, 59, 28, 120000000)));
    }

    @Test
    public void printExtendedTimeHHMMSSfff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.of(23, 59, 28, 123000000)),
            is("23:59:28,123"));
    }

    @Test
    public void parseExtendedTimeHHMMSSfff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,123"),
            is(PlainTime.of(23, 59, 28, 123000000)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59:28,123"),
            is(PlainTime.of(23, 59, 28, 123000000)));
    }

    @Test
    public void printExtendedTimeHHMMSSffffff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(
                PlainTime.of(23, 59, 28, 123456000)),
            is("23:59:28,123456"));
    }

    @Test
    public void parseExtendedTimeHHMMSSffffff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,123456"),
            is(PlainTime.of(23, 59, 28, 123456000)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59:28,123456"),
            is(PlainTime.of(23, 59, 28, 123456000)));
    }

    @Test
    public void printExtendedTimeHHMMSSfffffffff() {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.print(PlainTime.of(23, 59, 28, 123456789)),
            is("23:59:28,123456789"));
    }

    @Test
    public void parseExtendedTimeHHMMSSfffffffff() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28,123456789"),
            is(PlainTime.of(23, 59, 28, 123456789)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59:28,123456789"),
            is(PlainTime.of(23, 59, 28, 123456789)));
    }

    @Test
    public void parseExtendedTimeWithDecimalPoint() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("23:59:28.123456789"),
            is(PlainTime.of(23, 59, 28, 123456789)));
        assertThat(
            Iso8601Format.EXTENDED_WALL_TIME.parse("T23:59:28.123456789"),
            is(PlainTime.of(23, 59, 28, 123456789)));
    }

    @Test
    public void printExtendedDateTimeOffsetZ() {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET.print(
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
                .withTimezone(ZonalOffset.ofTotalSeconds(7200)).print(
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
                .withTimezone(ZonalOffset.ofHoursMinutes(OffsetSign.BEHIND_UTC, 5, 30))
                .print(PlainDate.of(2012, 6, 30).at(PlainTime.of(23, 59, 59)).atUTC().plus(1, SI.SECONDS)),
            is("2012-06-30T18:29:60-05:30"));
    }

    @Test
    public void parseExtendedDateTimeOffsetMinus0530() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse("2012-06-30T18:29:60-05:30"),
            is(
                PlainDate.of(2012, 6, 30)
                    .at(PlainTime.of(23, 59, 59))
                    .atUTC()
                    .plus(1, SI.SECONDS)));
    }

    // Leap second can only be represented  with timezone-offset in full minutes: -00:00:01
    @Test(expected=IllegalArgumentException.class)
    public void printExtendedDateTimeOffsetInvalid() {
        Iso8601Format.EXTENDED_DATE_TIME_OFFSET
                .withTimezone(ZonalOffset.ofTotalSeconds(-1))
                .print(
                    PlainDate.of(2012, 6, 30)
                        .at(PlainTime.of(23, 59, 59))
                        .atUTC()
                        .plus(1, SI.SECONDS));
    }

    @Test(expected=ParseException.class)
    public void parseExtendedDateTimeOffsetMissing() throws ParseException {
        Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse("2012-06-30T18:29:42");
    }

    @Test
    public void printExtendedDateTime() {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME.print(
                PlainDate.of(2012, 6, 30).at(PlainTime.of(23, 59, 59))),
            is("2012-06-30T23:59:59"));
    }

    @Test
    public void parseExtendedDateTime() throws ParseException {
        assertThat(
            Iso8601Format.EXTENDED_DATE_TIME.parse("2012-06-30T23:59:59"),
            is(PlainDate.of(2012, 6, 30).at(PlainTime.of(23, 59, 59))));
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

    @Test
    public void methodParseDate() throws ParseException {
        assertThat(
            Iso8601Format.parseDate("20160101"),
            is(PlainDate.of(2016, 1, 1)));
        assertThat(
            Iso8601Format.parseDate("2016001"),
            is(PlainDate.of(2016, 1, 1)));
        assertThat(
            Iso8601Format.parseDate("2015W535"),
            is(PlainDate.of(2016, 1, 1)));
        assertThat(
            Iso8601Format.parseDate("2016-01-01"),
            is(PlainDate.of(2016, 1, 1)));
        assertThat(
            Iso8601Format.parseDate("2016-001"),
            is(PlainDate.of(2016, 1, 1)));
        assertThat(
            Iso8601Format.parseDate("2015-W53-5"),
            is(PlainDate.of(2016, 1, 1)));
    }

    @Test(expected=ParseException.class)
    public void methodParseDateTooShort() throws ParseException {
        Iso8601Format.parseDate("123456");
    }

    @Test
    public void ofBasicTimeT24() {
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("24"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("2400"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("240000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("240000,000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("240000,000000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("240000,000000000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("24"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("2400"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("240000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("240000.000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("240000.000000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("240000.000000000"));
    }

    @Test
    public void ofBasicTimeWithMillis120() {
        PlainTime time = PlainTime.of(7, 45, 8, 120_000_000);
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("0745"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("074508"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("074508,120"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("074508,120000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("074508,120000000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("0745"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("074508"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("074508.120"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("074508.120000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("074508.120000000"));
    }

    @Test
    public void ofBasicTimeWithMicros400() {
        PlainTime time = PlainTime.of(7, 45, 8, 123_400_000);
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("0745"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("074508"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("074508,123"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("074508,123400"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("074508,123400000"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("0745"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("074508"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("074508.123"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("074508.123400"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("074508.123400000"));
    }

    @Test
    public void ofBasicTimeWithNanos() {
        PlainTime time = PlainTime.of(7, 45, 8, 123_456_789);
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("0745"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("074508"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("074508,123"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("074508,123456"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("074508,123456789"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("0745"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("074508"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("074508.123"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("074508.123456"));
        assertThat(
            Iso8601Format.ofBasicTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("074508.123456789"));
    }

    @Test
    public void ofExtendedTimeT24() {
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("24"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("24:00"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("24:00:00"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("24:00:00,000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("24:00:00,000000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("24:00:00,000000000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("24"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("24:00"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("24:00:00"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("24:00:00.000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("24:00:00.000000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("24:00:00.000000000"));
    }

    @Test
    public void ofExtendedTimeWithMillis120() {
        PlainTime time = PlainTime.of(7, 45, 8, 120_000_000);
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("07:45"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("07:45:08"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("07:45:08,120"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("07:45:08,120000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("07:45:08,120000000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("07:45"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("07:45:08"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("07:45:08.120"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("07:45:08.120000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("07:45:08.120000000"));
    }

    @Test
    public void ofExtendedTimeWithMicros400() {
        PlainTime time = PlainTime.of(7, 45, 8, 123_400_000);
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("07:45"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("07:45:08"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("07:45:08,123"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("07:45:08,123400"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("07:45:08,123400000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("07:45"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("07:45:08"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("07:45:08.123"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("07:45:08.123400"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("07:45:08.123400000"));
    }

    @Test
    public void ofExtendedTimeWithNanos() {
        PlainTime time = PlainTime.of(7, 45, 8, 123_456_000);
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MINUTES).print(time),
            is("07:45"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.SECONDS).print(time),
            is("07:45:08"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MILLIS).print(time),
            is("07:45:08,123"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.MICROS).print(time),
            is("07:45:08,123456"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.COMMA, ClockUnit.NANOS).print(time),
            is("07:45:08,123456000"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.HOURS).print(time),
            is("07"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MINUTES).print(time),
            is("07:45"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.SECONDS).print(time),
            is("07:45:08"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MILLIS).print(time),
            is("07:45:08.123"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.MICROS).print(time),
            is("07:45:08.123456"));
        assertThat(
            Iso8601Format.ofExtendedTime(IsoDecimalStyle.DOT, ClockUnit.NANOS).print(time),
            is("07:45:08.123456000"));
    }

    @Test
    public void ofTimestamp() {
        PlainTimestamp tsp = PlainTimestamp.of(2016, 2, 29, 17, 45, 30).plus(123450, ClockUnit.MICROS);
        assertThat(
            Iso8601Format.ofTimestamp(
                IsoDateStyle.BASIC_CALENDAR_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MICROS
            ).print(tsp),
            is("20160229T174530,123450"));
        assertThat(
            Iso8601Format.ofTimestamp(
                IsoDateStyle.BASIC_ORDINAL_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MICROS
            ).print(tsp),
            is("2016060T174530,123450"));
        assertThat(
            Iso8601Format.ofTimestamp(
                IsoDateStyle.BASIC_WEEK_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MICROS
            ).print(tsp),
            is("2016W091T174530,123450"));
        assertThat(
            Iso8601Format.ofTimestamp(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MICROS
            ).print(tsp),
            is("2016-02-29T17:45:30.123450"));
        assertThat(
            Iso8601Format.ofTimestamp(
                IsoDateStyle.EXTENDED_ORDINAL_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MICROS
            ).print(tsp),
            is("2016-060T17:45:30.123450"));
        assertThat(
            Iso8601Format.ofTimestamp(
                IsoDateStyle.EXTENDED_WEEK_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MICROS
            ).print(tsp),
            is("2016-W09-1T17:45:30.123450"));
    }

    @Test
    public void ofMoment() {
        Moment ls = PlainTimestamp.of(2012, 6, 30, 23, 59, 59).atUTC().plus(1, SI.SECONDS);
        assertThat(
            Iso8601Format.ofMoment(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MILLIS,
                ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30)
            ).print(ls),
            is("2012-07-01T05:29:60,000+05:30"));
        assertThat(
            Iso8601Format.ofMoment(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.COMMA,
                ClockUnit.MILLIS,
                ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 4)
            ).print(ls),
            is("2012-06-30T19:59:60,000-04:00"));
        assertThat(
            Iso8601Format.ofMoment(
                IsoDateStyle.EXTENDED_CALENDAR_DATE,
                IsoDecimalStyle.DOT,
                ClockUnit.MILLIS,
                ZonalOffset.UTC
            ).print(ls),
            is("2012-06-30T23:59:60.000Z"));
    }

}
