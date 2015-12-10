package net.time4j.format.expert;

import net.time4j.DayPeriod;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DayPeriodTest {

    @Test
    public void displayMidnight() {
        assertThat(
            PlainTime.midnightAtEndOfDay().get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("midnight"));
        assertThat(
            PlainTime.midnightAtStartOfDay().get(
                DayPeriod.of(Locale.GERMAN).approximate()),
            is("Mitternacht"));
    }

    @Test
    public void displayMorning() {
        assertThat(
            PlainTime.of(11, 59).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("am"));
        assertThat(
            PlainTime.of(11, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("in the morning"));
    }

    @Test
    public void displayNoon() {
        assertThat(
            PlainTime.of(12).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("noon"));
        assertThat(
            PlainTime.of(12).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("noon"));
    }

    @Test
    public void displayAfternoon() {
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("pm"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.GERMAN).fixed(TextWidth.ABBREVIATED, OutputContext.FORMAT)),
            is("nachm."));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("in the afternoon"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.GERMAN).approximate()),
            is("nachmittags"));
    }

    @Test
    public void displayEvening() {
        assertThat(
            PlainTime.of(20, 45).get(
                DayPeriod.of(Locale.ENGLISH).approximate(TextWidth.WIDE, OutputContext.STANDALONE)
            ),
            is("evening"));
    }

    @Test
    public void displayNight() {
        assertThat(
            PlainTime.of(5, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate(TextWidth.WIDE, OutputContext.FORMAT)
            ),
            is("at night"));
    }

    @Test
    public void startEndForMidnight() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(0)),
            is(PlainTime.of(0)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(0)),
            is(PlainTime.of(0, 1)));
    }

    @Test
    public void startEndForNight1() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(4)),
            is(PlainTime.of(0, 1)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(4)),
            is(PlainTime.of(6)));
    }

    @Test
    public void startEndForMorning() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(9)),
            is(PlainTime.of(6)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(9)),
            is(PlainTime.of(12)));
    }

    @Test
    public void startEndForNoon() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(12)),
            is(PlainTime.of(12)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(12)),
            is(PlainTime.of(12, 1)));
    }

    @Test
    public void startEndForAfternoon() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(15)),
            is(PlainTime.of(12, 1)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(15)),
            is(PlainTime.of(18)));
    }

    @Test
    public void startEndForEvening() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(20)),
            is(PlainTime.of(18)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(20)),
            is(PlainTime.of(21)));
    }

    @Test
    public void startEndForNight2() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(23)),
            is(PlainTime.of(21)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(23)),
            is(PlainTime.of(0)));
    }

    @Test
    public void fallback() {
        assertThat(
            PlainTime.of(5).get(DayPeriod.of(new Locale("xyz")).approximate()),
            is("AM"));
        assertThat(
            PlainTime.of(12).get(DayPeriod.of(new Locale("xyz")).approximate()),
            is("PM"));
    }

    @Test
    public void colombiaMorning() {
        DayPeriod dp = DayPeriod.of(new Locale("es", "CO"));
        assertThat(
            dp.getStart(PlainTime.of(3)),
            is(PlainTime.midnightAtStartOfDay()));
        assertThat(
            dp.getStart(PlainTime.of(7)),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void formatFixedEnglish() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm b", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.format(PlainTime.of(3, 45)),
            is("3:45 am"));
        assertThat(
            f.parse("3:45 am"),
            is(PlainTime.of(3, 45)));
        assertThat(
            f.format(PlainTime.of(23, 45)),
            is("11:45 pm"));
        assertThat(
            f.parse("11:45 pm"),
            is(PlainTime.of(23, 45)));
        assertThat(
            f.format(PlainTime.of(0)),
            is("12:00 midnight"));
        assertThat(
            f.parse("12:00 midnight"),
            is(PlainTime.of(0)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 noon"));
        assertThat(
            f.parse("12:00 noon"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(17, 15)),
            is("5:15 pm"));
        assertThat(
            f.parse("5:15 pm"),
            is(PlainTime.of(17, 15)));
    }

    @Test
    public void formatFlexibleEnglishWide() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBB", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.format(PlainTime.of(3, 45)),
            is("3:45 at night"));
        assertThat(
            f.parse("3:45 at night"),
            is(PlainTime.of(3, 45)));
        assertThat(
            f.format(PlainTime.of(23, 45)),
            is("11:45 at night"));
        assertThat(
            f.parse("11:45 at night"),
            is(PlainTime.of(23, 45)));
        assertThat(
            f.format(PlainTime.of(0)),
            is("12:00 midnight"));
        assertThat(
            f.parse("12:00 midnight"),
            is(PlainTime.of(0)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 noon"));
        assertThat(
            f.parse("12:00 noon"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(17, 15)),
            is("5:15 in the afternoon"));
        assertThat(
            f.parse("5:15 in the afternoon"),
            is(PlainTime.of(17, 15)));
    }

    @Test
    public void formatFlexibleEnglishNarrow() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBBB", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.format(PlainTime.of(3, 45)),
            is("3:45 at night"));
        assertThat(
            f.parse("3:45 at night"),
            is(PlainTime.of(3, 45)));
        assertThat(
            f.format(PlainTime.of(23, 45)),
            is("11:45 at night"));
        assertThat(
            f.parse("11:45 at night"),
            is(PlainTime.of(23, 45)));
        assertThat(
            f.format(PlainTime.of(0)),
            is("12:00 mi"));
        assertThat(
            f.parse("12:00 mi"),
            is(PlainTime.of(0)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 n"));
        assertThat(
            f.parse("12:00 n"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(17, 15)),
            is("5:15 in the afternoon"));
        assertThat(
            f.parse("5:15 in the afternoon"),
            is(PlainTime.of(17, 15)));
    }

    @Test
    public void formatFlexibleGerman0345() throws ParseException {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.ofTimestampPattern("d. MMMM uuuu h:mm BBBB", PatternType.CLDR, Locale.GERMAN);
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 3, 45)),
            is("10. Dezember 2015 3:45 nachts"));
        assertThat(
            f.parse("10. Dezember 2015 3:45 nachts"),
            is(PlainTimestamp.of(2015, 12, 10, 3, 45)));
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 15, 45)),
            is("10. Dezember 2015 3:45 nachmittags"));
        assertThat(
            f.parse("10. Dezember 2015 3:45 nachmittags"),
            is(PlainTimestamp.of(2015, 12, 10, 15, 45)));
    }

    @Test
    public void formatFlexibleGerman0900() throws ParseException {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.ofTimestampPattern("d. MMMM uuuu h:mm BBBB", PatternType.CLDR, Locale.GERMAN);
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 9, 0)),
            is("10. Dezember 2015 9:00 morgens"));
        assertThat(
            f.parse("10. Dezember 2015 9:00 morgens"),
            is(PlainTimestamp.of(2015, 12, 10, 9, 0)));
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 21, 0)),
            is("10. Dezember 2015 9:00 abends"));
        assertThat(
            f.parse("10. Dezember 2015 9:00 abends"),
            is(PlainTimestamp.of(2015, 12, 10, 21, 0)));
    }

    @Test
    public void formatFlexibleIndonesian() throws ParseException { // test for ambivalent code "afternoon1"
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBB", PatternType.CLDR, new Locale("id")); // or "in"
        assertThat(
            f.format(PlainTime.of(13, 45)),
            is("1:45 siang"));
        assertThat(
            f.parse("1:45 siang"),
            is(PlainTime.of(13, 45)));
        assertThat(
            f.format(PlainTime.of(11, 15)),
            is("11:15 siang"));
        assertThat(
            f.parse("11:15 siang"),
            is(PlainTime.of(11, 15)));
    }

}
