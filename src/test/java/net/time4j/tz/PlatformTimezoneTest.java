package net.time4j.tz;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.base.GregorianDate;
import net.time4j.scale.TimeScale;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PlatformTimezoneTest {

    @Test
    public void getOffsetOfUnixTimeSTD() {
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        Moment utc = Moment.of(40 * 365 * 86400, TimeScale.POSIX);
        assertThat(tz.getOffset(utc).canonical(), is("UTC+01:00"));
    }

    @Test
    public void getOffsetOfUnixTimeDST() {
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        Moment utc = Moment.of((40 * 365 + 180) * 86400, TimeScale.POSIX);
        assertThat(tz.getOffset(utc).canonical(), is("UTC+02:00"));
    }

    @Test
    public void isDaylightSavingSTD() {
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        Moment utc = Moment.of(40 * 365 * 86400, TimeScale.POSIX);
        assertThat(tz.isDaylightSaving(utc), is(false));
    }

    @Test
    public void isDaylightSavingDST() {
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        Moment utc = Moment.of((40 * 365 + 180) * 86400, TimeScale.POSIX);
        assertThat(tz.isDaylightSaving(utc), is(true));
    }

    @Test
    public void getOffsetOfLocalDateTimeWithWinterDST() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.midnightAtStartOfDay());
        assertThat(
            offset.canonical(),
            is("UTC+01:00"));
    }

    @Test
    public void getOffsetOfLocalDateTimeWithSummerDST() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.of(3));
        assertThat(
            offset.canonical(),
            is("UTC+02:00"));
    }

    @Test
    public void getOffsetOfLocalDateWithTimeInGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.of(2, 30));
        assertThat(
            offset.canonical(),
            is("UTC+02:00"));
    }

    @Test
    public void getOffsetOfLocalDateWithTimeInOverlap() {
        GregorianDate date = PlainDate.of(2014, 10, 26);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.of(2, 30));
        assertThat(
            offset.canonical(),
            is("UTC+01:00"));
    }

    @Test
    public void getOffsetOfLocalDateWithTimeT24() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.midnightAtEndOfDay());
        assertThat(
            offset.canonical(),
            is("UTC+02:00"));
    }

    @Test
    public void isInvalidBeforeGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        assertThat(
            tz.isInvalid(date, PlainTime.of(1, 59)),
            is(false));
    }

    @Test
    public void isInvalidInGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        assertThat(
            tz.isInvalid(date, PlainTime.of(2, 0)),
            is(true));
        assertThat(
            tz.isInvalid(date, PlainTime.of(2, 59)),
            is(true));
    }

    @Test
    public void isInvalidAfterGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        assertThat(
            tz.isInvalid(date, PlainTime.of(3, 0)),
            is(false));
    }

    @Test
    public void getAvailableIDs() {
        List<TZID> zoneIDs = Timezone.getAvailableIDs();
        assertThat(zoneIDs.size() > 1, is(true));
        assertThat(zoneIDs.contains(ZonalOffset.UTC), is(true));
        assertThat(zoneIDs.contains(Timezone.ofSystem().getID()), is(true));
    }

    @Test
    public void getDisplayName() {
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        assertThat(
            tz.getDisplayName(true, false, Locale.GERMAN),
            is("Mitteleuropäische Sommerzeit"));
        assertThat(
            tz.getDisplayName(true, true, Locale.GERMAN),
            is("MESZ"));
        assertThat(
            tz.getDisplayName(false, false, Locale.GERMAN),
            is("Mitteleuropäische Zeit"));
        assertThat(
            tz.getDisplayName(false, true, Locale.GERMAN),
            is("MEZ"));
    }

    @Test
    public void getHistory() {
        Timezone tz = loadFromPlatform(TZID.EUROPE.BERLIN);
        assertThat(tz.getHistory(), is(nullValue()));
    }

    @Test
    public void getID() {
        TZID id = TZID.ASIA.TOKYO;
        Timezone tz = loadFromPlatform(id);
        assertThat(tz.getID(), is(id));
    }

    @Test
    public void getPreferredIDs() {
        TZID tzid = TZID.EUROPE.BERLIN;
        assertThat(
            Timezone.getPreferredIDs(Locale.GERMANY),
            is(Collections.singleton(tzid)));
    }

    @Test
    public void ofTZID() {
        TZID tzid = TZID.EUROPE.BERLIN;
        Timezone expected = loadFromPlatform(tzid);
        assertThat(Timezone.of(tzid), is(expected));
    }

    @Test
    public void ofTZIDFallback() {
        TZID tzid = TZID.EUROPE.BERLIN;
        Timezone expected = loadFromPlatform(tzid);
        assertThat(
            Timezone.of(
                new TZID() {
                    @Override
                    public String canonical() {
                        return "???";
                    }
                },
                tzid),
            is(expected));
    }

    private static Timezone loadFromPlatform(TZID tzid) {
        return new PlatformTimezone(tzid, tzid.canonical());
    }

}