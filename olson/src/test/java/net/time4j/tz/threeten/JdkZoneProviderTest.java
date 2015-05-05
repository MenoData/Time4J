package net.time4j.tz.threeten;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.ZoneProvider;

import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.ClockUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class JdkZoneProviderTest {

    private static ZoneProvider zp = null;

    @BeforeClass
    public static void init() {
        zp = new JdkZoneProviderSPI();

        System.out.println(
            "Test of Threeten-ZoneProvider: version="
            + zp.getVersion());
    }

    @Test
    public void getFallback() {
        assertThat(zp.getFallback(), is(""));
    }

    @Test
    public void getLocation() {
        assertThat(zp.getLocation(), is("{java.home}/lib/tzdb.dat"));
    }

    @Test
    public void getName() {
        assertThat(zp.getName(), is("TZDB"));
    }

    @Test
    public void getAliases() {
        assertThat(
            zp.getAliases().get("Atlantic/Jan_Mayen"),
            is("Europe/Oslo"));
    }

    @Test
    public void compareAliasWithOriginal() {
        TransitionHistory histJanMayen = zp.load("Atlantic/Jan_Mayen");
        TransitionHistory histOslo = zp.load("Europe/Oslo");

        assertThat(histJanMayen, nullValue());
        assertThat(histOslo, notNullValue());
    }

    @Test
    public void loadSystemV() {
        TransitionHistory h = zp.load("SystemV/EST5");
        assertThat(h.isEmpty(), is(true));
        assertThat(
            h.getInitialOffset(),
            is(ZonalOffset.ofHours(OffsetSign.BEHIND_UTC, 5)));
    }

    @Test
    public void loadCasablanca() {
        try {
            // special test for tzdb-version 2013h
            TransitionHistory h = zp.load("Africa/Casablanca");
            assertThat(h.isEmpty(), is(false));
        } catch (RuntimeException ex) {
            fail("No workaround for handling broken timezone data.");
        }
    }

    @Test
    public void loadAllData() {
        for (String tzid : zp.getAvailableIDs()) {
            try {
                TransitionHistory history = zp.load(tzid);
                if (history == null) {
                    assertThat(
                        zp.getAliases().containsKey(tzid),
                        is(true));
                }
            } catch (RuntimeException ex) {
                fail("Problem with loading history of: " + tzid);
            }
        }
    }

    @Test
    public void dumpBerlin() throws IOException {
        zp.load("Europe/Berlin").dump(System.out);
    }

    @Test
    public void hasHistory() throws IOException {
        assertThat(
            (zp.load("Europe/Berlin") != null),
            is(true));
    }

    @Test
    public void midsummer() throws IOException {
        PlainDate date = PlainDate.of(1945, 5, 24);
        PlainTime time = PlainTime.of(2);
        Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(7200));
        ZonalTransition conflict =
            zp.load("Europe/Berlin").getConflictTransition(date, time);
        assertThat(
            conflict.getPosixTime(),
            is(m.getPosixTime()));
        assertThat(
            conflict.getPreviousOffset(),
            is(2 * 3600));
        assertThat(
            conflict.getTotalOffset(),
            is(3 * 3600));
        assertThat(
            conflict.getDaylightSavingOffset(),
            is(2 * 3600));
    }

    @Test
    public void dhakaInDSTa() throws IOException {
        TransitionHistory history = zp.load("Asia/Dhaka");
        PlainDate date = PlainDate.of(2009, 6, 19);
        PlainTime time = PlainTime.of(23, 0);
        Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(6 * 3600));

        ZonalTransition conflict = // at first position in gap
            history.getConflictTransition(date, time);

        assertThat(
            conflict.getPosixTime(),
            is(m.getPosixTime()));
        assertThat(
            conflict.getPreviousOffset(),
            is(6 * 3600));
        assertThat(
            conflict.getTotalOffset(),
            is(7 * 3600));
        assertThat(
            conflict.getDaylightSavingOffset(),
            is(3600));
    }

    @Test
    public void dhakaInDSTb() throws IOException {
        TransitionHistory history = zp.load("Asia/Dhaka");
        PlainDate date = PlainDate.of(2009, 6, 19);
        PlainTime time = PlainTime.of(23, 0);
        Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(6 * 3600));

        ZonalTransition conflict = // at late position in gap
            history.getConflictTransition(date, time.plus(59, MINUTES));

        assertThat(
            conflict.getPosixTime(),
            is(m.getPosixTime()));
        assertThat(
            conflict.getPreviousOffset(),
            is(6 * 3600));
        assertThat(
            conflict.getTotalOffset(),
            is(7 * 3600));
        assertThat(
            conflict.getDaylightSavingOffset(),
            is(3600));
    }

    @Test
    public void dhakaAtEndOf2009a() throws IOException {
        TransitionHistory history = zp.load("Asia/Dhaka");
        PlainDate date = PlainDate.of(2009, 12, 31);
        PlainTime time = PlainTime.of(23, 59); // dirty hack in JDK for T24
        Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(7 * 3600));

        ZonalTransition conflict = // at first ambivalent time
            history.getConflictTransition(date, PlainTime.of(22, 59));

        assertThat(
            conflict.getPosixTime(),
            is(m.getPosixTime()));
        assertThat(
            conflict.getPreviousOffset(),
            is(7 * 3600));
        assertThat(
            conflict.getTotalOffset(),
            is(6 * 3600));
        assertThat(
            conflict.getDaylightSavingOffset(),
            is(0));
    }

    @Test
    public void dhakaAtEndOf2009b() throws IOException {
        TransitionHistory history = zp.load("Asia/Dhaka");
        PlainDate date = PlainDate.of(2009, 12, 31);
        PlainTime time = PlainTime.of(23, 59); // dirty hack in JDK for T24
        Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(7 * 3600));

        ZonalTransition conflict = // any ambivalent time
            history.getConflictTransition(date, PlainTime.of(23, 30));

        assertThat(
            conflict.getPosixTime(),
            is(m.getPosixTime()));
        assertThat(
            conflict.getPreviousOffset(),
            is(7 * 3600));
        assertThat(
            conflict.getTotalOffset(),
            is(6 * 3600));
        assertThat(
            conflict.getDaylightSavingOffset(),
            is(0));
    }

}