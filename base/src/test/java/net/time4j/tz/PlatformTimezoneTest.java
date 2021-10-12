package net.time4j.tz;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.base.GregorianDate;
import net.time4j.scale.TimeScale;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class PlatformTimezoneTest {

    private static final String PREFIX = "java.util.TimeZone~";

    @Test
    public void ofPlatform() {
        Timezone platformTZ = Timezone.ofPlatform();
        assertThat(
            platformTZ.getID().canonical().equals(Timezone.ofSystem().getID().canonical()),
            is(true));
        assertThat(
            platformTZ.getHistory() != null,
            is(true));
    }

    @Test
    public void getOffsetOfUnixTimeSTD() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment utc = Moment.of(40 * 365 * 86400, TimeScale.POSIX);
        assertThat(tz.getOffset(utc).canonical(), is("UTC+01:00"));
    }

    @Test
    public void getOffsetOfUnixTimeDST() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment utc = Moment.of((40 * 365 + 180) * 86400, TimeScale.POSIX);
        assertThat(tz.getOffset(utc).canonical(), is("UTC+02:00"));
    }

    @Test
    public void getPartialOffsetsOfUnixTimeWinter() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment utc = Moment.of(40 * 365 * 86400, TimeScale.POSIX);
        assertThat(tz.getRawOffset(utc).canonical(), is("UTC+01:00"));
        assertThat(tz.getExtraOffset(utc).canonical(), is("Z"));
    }

    @Test
    public void getPartialOffsetsOfUnixTimeSummer() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment utc = Moment.of((40 * 365 + 180) * 86400, TimeScale.POSIX);
        assertThat(tz.getRawOffset(utc).canonical(), is("UTC+01:00"));
        assertThat(tz.getExtraOffset(utc).canonical(), is("UTC+01:00"));
    }

    @Test
    public void isDaylightSavingSTD() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment utc = Moment.of(40 * 365 * 86400, TimeScale.POSIX);
        assertThat(tz.isDaylightSaving(utc), is(false));
    }

    @Test
    public void isDaylightSavingDST() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment utc = Moment.of((40 * 365 + 180) * 86400, TimeScale.POSIX);
        assertThat(tz.isDaylightSaving(utc), is(true));
    }

    @Test
    public void customProviderID() {
        Timezone tz = Timezone.of("java.util.TimeZone~Europe/Berlin");
        assertThat(
            tz,
            is(loadFromPlatform("Europe/Berlin")));
    }

    @Test
    public void getOffsetOfLocalDateTimeWithWinterDST() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform("Europe/Berlin");
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.midnightAtStartOfDay());
        assertThat(
            offset.canonical(),
            is("UTC+01:00"));
    }

    @Test
    public void getOffsetOfLocalDateTimeWithSummerDST() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform("Europe/Berlin");
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.of(3));
        assertThat(
            offset.canonical(),
            is("UTC+02:00"));
    }

    @Test
    public void getOffsetOfLocalDateWithTimeInGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform("Europe/Berlin");
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.of(2, 30));
        assertThat(
            offset.canonical(),
            is("UTC+02:00"));
    }

    @Test
    public void getOffsetOfLocalDateWithTimeInOverlap() {
        GregorianDate date = PlainDate.of(2014, 10, 26);
        Timezone tz = loadFromPlatform("Europe/Berlin");
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.of(2, 30));
        assertThat(
            offset.canonical(),
            is("UTC+01:00"));
    }

    @Test
    public void getOffsetOfLocalDateWithTimeT24() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform("Europe/Berlin");
        ZonalOffset offset =
            tz.getOffset(date, PlainTime.midnightAtEndOfDay());
        assertThat(
            offset.canonical(),
            is("UTC+02:00"));
    }

    @Test
    public void isInvalidBeforeGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            tz.isInvalid(date, PlainTime.of(1, 59)),
            is(false));
    }

    @Test
    public void isInvalidInGap() {
        GregorianDate date = PlainDate.of(2014, 3, 30);
        Timezone tz = loadFromPlatform("Europe/Berlin");
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
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            tz.isInvalid(date, PlainTime.of(3, 0)),
            is(false));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withStrictModeDirectUse() {
        Timezone.STRICT_MODE.resolve(
            PlainDate.of(2014, 3, 30),
            PlainTime.of(2, 30),
            loadFromPlatform("Europe/Berlin"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withStrictModeIndirectUse() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        PlainTimestamp.of(
            PlainDate.of(2014, 3, 30),
            PlainTime.of(2, 30)
        ).in(tz.with(Timezone.STRICT_MODE));
    }

    @Test
    public void withDefaultConflictStrategyGetOffset() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            Timezone.DEFAULT_CONFLICT_STRATEGY.getOffset(
                PlainDate.of(2014, 3, 30),
                PlainTime.of(2, 30),
                tz
            ),
            is(ZonalOffset.ofTotalSeconds(7200)));
    }

    @Test
    public void withDefaultConflictStrategyResolvingGermany() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        Moment m =
            PlainTimestamp.of(
                PlainDate.of(2014, 3, 30),
                PlainTime.of(2, 30)
            ).in(tz.with(Timezone.DEFAULT_CONFLICT_STRATEGY));
        assertThat(
            m,
            is(PlainTimestamp.of(2014, 3, 30, 3, 30).in(tz)));
        assertThat(
            m.toZonalTimestamp(tz.getID()),
            is(PlainTimestamp.of(2014, 3, 30, 3, 30)));
    }

    @Test
    public void withDefaultConflictStrategyResolvingBrazil() {
        Timezone tz = loadFromPlatform("America/Sao_Paulo");
        Moment m =
            PlainTimestamp.of(
                PlainDate.of(2014, 10, 19),
                PlainTime.midnightAtStartOfDay()
            ).in(tz.with(Timezone.DEFAULT_CONFLICT_STRATEGY));
        assertThat(
            m,
            is(PlainTimestamp.of(2014, 10, 19, 1, 0).in(tz)));
        assertThat(
            m.toZonalTimestamp(tz.getID()),
            is(PlainTimestamp.of(2014, 10, 19, 1, 0)));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void withSpecificTransitionStrategy() {
        Timezone.of("java.util.TimeZone~Europe/Berlin")
            .with(GapResolver.NEXT_VALID_TIME.and(OverlapResolver.EARLIER_OFFSET));
    }

    @Test
    public void getAvailableIDs() {
        List<TZID> zoneIDs = Timezone.getAvailableIDs();
        assertThat(zoneIDs.size() > 1, is(true));
        assertThat(zoneIDs.contains(ZonalOffset.UTC), is(true));
        assertThat(zoneIDs.contains(Timezone.ofSystem().getID()), is(true));
    }

    @Test
    public void getDisplayName_LONG_DAYLIGHT_TIME() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            tz.getDisplayName(NameStyle.LONG_DAYLIGHT_TIME, Locale.GERMAN),
            is("Mitteleuropäische Sommerzeit"));
    }

    @Test
    public void getDisplayName_SHORT_DAYLIGHT_TIME() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            tz.getDisplayName(NameStyle.SHORT_DAYLIGHT_TIME, Locale.GERMAN),
            is("MESZ"));
    }

    @Test
    public void getDisplayName_LONG_STANDARD_TIME() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            tz.getDisplayName(NameStyle.LONG_STANDARD_TIME, Locale.GERMAN),
            is("Mitteleuropäische Normalzeit"));
    }

    @Test
    public void getDisplayName_SHORT_STANDARD_TIME() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(
            tz.getDisplayName(NameStyle.SHORT_STANDARD_TIME, Locale.GERMAN),
            is("MEZ"));
    }

    @Test
    public void getHistory() {
        Timezone tz = loadFromPlatform("Europe/Berlin");
        assertThat(tz.getHistory(), is(nullValue()));
    }

    @Test
    public void getID() {
        TZID id = Timezone.of("Asia/Tokyo").getID();
        Timezone tz = loadFromPlatform("Asia/Tokyo");
        assertThat(tz.getID(), is(id));
    }

    @Test
    public void ofTZID() {
        TZID tzid = Timezone.of(PREFIX + "Europe/Berlin").getID();
        Timezone expected = loadFromPlatform("Europe/Berlin");
        assertThat(Timezone.of(tzid), is(expected));
    }

    @Test
    public void ofTZIDFallback() {
        TZID fallback = Timezone.of(PREFIX + "Europe/Berlin").getID();
        Timezone expected = loadFromPlatform("Europe/Berlin");
        assertThat(
            Timezone.of("???", fallback),
            is(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofInvalidTZIDAsString() {
        Timezone.of("xyz");
    }

    @BeforeClass
    public static void initSystemTzOverride() {
        System.setProperty(
            "net.time4j.allow.system.tz.override",
            "true");
    }

    @Test
    public void systemTZ() throws IOException {
        String zoneID = "!test-id!";
        java.util.TimeZone oldTZ = java.util.TimeZone.getDefault();
        java.util.TimeZone platformTZ = java.util.TimeZone.getDefault();
        platformTZ.setID(zoneID);
        java.util.TimeZone.setDefault(platformTZ);
        Timezone.Cache.refresh();
        assertThat(Timezone.ofSystem().getID().canonical(), is(zoneID));
        assertThat(Timezone.ofSystem() instanceof PlatformTimezone, is(true));
        assertThat(Timezone.of(zoneID) instanceof PlatformTimezone, is(true));
        Timezone.ofSystem().dump(System.out);
        java.util.TimeZone.setDefault(oldTZ);
        Timezone.Cache.refresh();
        assertThat(Timezone.ofSystem().getID().canonical(), not(zoneID));
    }

    private static Timezone loadFromPlatform(String tzid) {
        return new PlatformTimezone(Timezone.of(tzid).getID(), tzid);
    }

}