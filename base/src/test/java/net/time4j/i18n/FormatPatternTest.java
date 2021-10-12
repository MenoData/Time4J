package net.time4j.i18n;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.CalendarText;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FormatPatternTest {

    @Test
    public void patternForUnsupportedLocale() {
        String patternXX = CalendarText.patternForDate(FormatStyle.FULL, new Locale("xx"));
        String patternRoot = CalendarText.patternForDate(FormatStyle.FULL, Locale.ROOT);
        assertThat(patternXX, is(patternRoot));
    }

    @Test
    public void datePattern() {
        PlainDate date = PlainDate.of(2015, 9, 8);
        assertThat(
            ChronoFormatter.ofDateStyle(FormatStyle.FULL, Locale.GERMANY).format(date),
            is("Dienstag, 8. September 2015"));
        assertThat(
            ChronoFormatter.ofDateStyle(FormatStyle.LONG, Locale.GERMANY).format(date),
            is("8. September 2015"));
        assertThat(
            ChronoFormatter.ofDateStyle(FormatStyle.MEDIUM, Locale.GERMANY).format(date),
            is("08.09.2015"));
        assertThat(
            ChronoFormatter.ofDateStyle(FormatStyle.SHORT, Locale.GERMANY).format(date),
            is("08.09.15"));
    }

    @Test
    public void timePattern() {
        PlainTime time = PlainTime.of(17, 45, 30);
        assertThat(
            ChronoFormatter.ofTimeStyle(FormatStyle.FULL, Locale.GERMANY).print(time),
            is("17:45:30")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofTimeStyle(FormatStyle.LONG, Locale.GERMANY).print(time),
            is("17:45:30")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofTimeStyle(FormatStyle.MEDIUM, Locale.GERMANY).print(time),
            is("17:45:30")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofTimeStyle(FormatStyle.SHORT, Locale.GERMANY).print(time),
            is("17:45")); // ohne Offset!!!
    }

    @Test
    public void timestampPattern() {
        PlainTimestamp tsp = Moment.UNIX_EPOCH.toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1));
        assertThat(
            ChronoFormatter.ofTimestampStyle(FormatStyle.FULL, FormatStyle.FULL, Locale.GERMANY).format(tsp),
            is("Donnerstag, 1. Januar 1970 um 01:00:00")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofTimestampStyle(FormatStyle.LONG, FormatStyle.LONG, Locale.GERMANY).format(tsp),
            is("1. Januar 1970 um 01:00:00")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofTimestampStyle(FormatStyle.MEDIUM, FormatStyle.MEDIUM, Locale.GERMANY).format(tsp),
            is("01.01.1970, 01:00:00")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofTimestampStyle(FormatStyle.SHORT, FormatStyle.SHORT, Locale.GERMANY).format(tsp),
            is("01.01.70, 01:00")); // ohne Offset!!!
        assertThat(
            ChronoFormatter.ofStyle(FormatStyle.MEDIUM, Locale.GERMANY, PlainTimestamp.axis()).format(tsp),
            is("01.01.1970, 01:00:00")); // ohne Offset!!!
    }

    @Test
    public void dateTimePattern() {
        Moment m = Moment.UNIX_EPOCH;
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.FULL, FormatStyle.FULL, Locale.GERMANY, Timezone.of("Europe/Berlin").getID()).print(m),
            is("Donnerstag, 1. Januar 1970 um 01:00:00 Mitteleuropäische Normalzeit"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.LONG, FormatStyle.LONG, Locale.GERMANY, Timezone.of("Europe/Berlin").getID()).print(m),
            is("1. Januar 1970 um 01:00:00 MEZ"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, Locale.GERMANY, Timezone.of("Europe/Berlin").getID()).print(m),
            is("01.01.1970, 01:00:00"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.GERMANY, Timezone.of("Europe/Berlin").getID()).print(m),
            is("01.01.70, 01:00"));
    }

    @Test
    public void dateTimePatternH11() {
        Moment m = Moment.UNIX_EPOCH.minus(1, TimeUnit.HOURS);
        Locale locale = Locale.forLanguageTag("de-DE-u-hc-h11");
        TZID berlin = () -> "Europe/Berlin";
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.FULL, FormatStyle.FULL, locale, berlin).print(m),
            is("Donnerstag, 1. Januar 1970 um 0:00:00 Mitternacht Mitteleuropäische Normalzeit"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.LONG, FormatStyle.LONG, locale, berlin).print(m),
            is("1. Januar 1970 um 0:00:00 Mitternacht MEZ"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, locale, berlin).print(m),
            is("01.01.1970, 0:00:00 Mitternacht"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, locale, berlin).print(m),
            is("01.01.70, 0:00 Mitternacht"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, locale, berlin).getPattern(),
            is("dd.MM.y, K:mm:ss B"));
    }

    @Test
    public void dateTimePatternH12() {
        Moment m = Moment.UNIX_EPOCH.plus(14, TimeUnit.HOURS);
        Locale locale = Locale.forLanguageTag("de-DE-u-hc-h12");
        TZID berlin = () -> "Europe/Berlin";
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.FULL, FormatStyle.FULL, locale, berlin).print(m),
            is("Donnerstag, 1. Januar 1970 um 3:00:00 nachm. Mitteleuropäische Normalzeit"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.LONG, FormatStyle.LONG, locale, berlin).print(m),
            is("1. Januar 1970 um 3:00:00 nachm. MEZ"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, locale, berlin).print(m),
            is("01.01.1970, 3:00:00 nachm."));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, locale, berlin).print(m),
            is("01.01.70, 3:00 nachm."));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, locale, berlin).getPattern(),
            is("dd.MM.y, h:mm:ss B"));
    }

    @Test
    public void dateTimePatternH23() {
        Moment m = Moment.UNIX_EPOCH.plus(38, TimeUnit.HOURS);
        Locale locale = Locale.forLanguageTag("en-US-u-hc-h23");
        TZID berlin = () -> "Europe/Berlin";
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.FULL, FormatStyle.FULL, locale, berlin).print(m),
            is("Friday, January 2, 1970 at 15:00:00 Central European Standard Time"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.LONG, FormatStyle.LONG, locale, berlin).print(m),
            is("January 2, 1970 at 15:00:00 CET"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, locale, berlin).print(m),
            is("Jan 2, 1970, 15:00:00"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, locale, berlin).print(m),
            is("1/2/70, 15:00"));
        assertThat(
            ChronoFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.MEDIUM, locale, berlin).getPattern(),
            is("MMM d, y, HH:mm:ss"));
    }

}