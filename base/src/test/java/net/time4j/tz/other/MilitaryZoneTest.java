package net.time4j.tz.other;

import net.time4j.Moment;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.NameStyle;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class MilitaryZoneTest {

    @Test
    public void getOffsetAlpha() {
        assertThat(
            MilitaryZone.ALPHA.getOffset(),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1)));
    }

    @Test
    public void getOffsetBravo() {
        assertThat(
            MilitaryZone.BRAVO.getOffset(),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2)));
    }

    @Test
    public void getOffsetCharlie() {
        assertThat(
            MilitaryZone.CHARLIE.getOffset(),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3)));
    }

    @Test
    public void getOffsetDelta() {
        assertThat(
            MilitaryZone.DELTA.getOffset(),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 4)));
    }

    @Test
    public void getOffsetEcho() {
        assertThat(
            MilitaryZone.ECHO.getOffset(),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 5)));
    }

    @Test
    public void getDisplayNameZulu() {
        Timezone tz = Timezone.of(MilitaryZone.ZULU);
        assertThat(
            tz.getDisplayName(NameStyle.LONG_STANDARD_TIME, Locale.ROOT),
            is("Zulu"));
        assertThat(
            tz.getDisplayName(NameStyle.SHORT_STANDARD_TIME, Locale.ROOT),
            is("Z"));
    }

    @Test
    public void getDisplayNameMike() {
        Timezone tz = Timezone.of("MILITARY~UTC+12:00");
        assertThat(
            tz.getDisplayName(NameStyle.LONG_STANDARD_TIME, Locale.ROOT),
            is("Mike"));
        assertThat(
            tz.getDisplayName(NameStyle.SHORT_STANDARD_TIME, Locale.ROOT),
            is("M"));
    }

    @Test
    public void canonicalRomeo() {
        assertThat(MilitaryZone.ROMEO.canonical(), is("MILITARY~UTC-05:00"));
    }

    @Test
    public void militaryTime() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.US)
                .addPattern("HHmmz", PatternType.CLDR).build()
                .withTimezone(MilitaryZone.FOXTROT);
        String s = formatter.format(Moment.UNIX_EPOCH);
        assertThat(s, is("0600F")); // zero six hundred Foxtrot
    }

    @Test
    public void print() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.FRENCH)
                .addPattern("yyyy-MM-dd HH:mm zzzz", PatternType.CLDR).build()
                .withTimezone(MilitaryZone.FOXTROT);
        String s = formatter.format(Moment.UNIX_EPOCH);
        assertThat(s, is("1970-01-01 06:00 Foxtrot"));
    }

    @Test
    public void parse() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.FRENCH)
                .addPattern("yyyy-MM-dd HH:mm zzzz", PatternType.CLDR).build()
                .withTimezone(ZonalOffset.UTC);
        String input = "1970-01-01 06:00 Foxtrot";
        Moment m = formatter.parse(input);
        assertThat(m, is(Moment.UNIX_EPOCH));
    }

    @Test
    public void normalize() {
        assertThat(
            Timezone.normalize(MilitaryZone.BRAVO),
            is(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 2)));
    }

}