package net.time4j.tz.threeten;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.ZoneModelProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static net.time4j.ClockUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class JdkZoneProviderTest {

    private static ZoneModelProvider zp = null;

    @BeforeClass
    public static void init() {
        zp = new JdkZoneProviderSPI();
        System.out.println("Test of Threeten-ZoneProvider: version=" + zp.getVersion());
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
            zp.getAliases().isEmpty(),
            is(true));
    }

    @Test
    public void normalize() {
        assertThat(
            Timezone.normalize("Asia/Calcutta").canonical().equals("Asia/Kolkata"),
            is(false)); // no support for normalization in JSR-310
    }

    /*
    @Test
    public void normalized() {
        assertThat(
            ZoneId.of("Atlantic/Jan_Mayen").normalized().getId(),
            is("Europe/Oslo")); // normalization does not work (is just relevant for resolving fixed offsets)
    }
    */

    @Test
    public void compareAliasWithOriginal() {
        TransitionHistory histJanMayen = zp.load("Atlantic/Jan_Mayen");
        TransitionHistory histOslo = zp.load("Europe/Oslo");
        assertThat(histJanMayen, is(histOslo)); // same rules as expected for an alias
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
    public void loadAllData() {
        for (String tzid : zp.getAvailableIDs()) {
            try {
                TransitionHistory history = zp.load(tzid);
                assertThat(history, notNullValue());
            } catch (RuntimeException ex) {
                fail("Problem with loading history of: " + tzid);
            }
        }
    }

    @Test
    public void dumpCasablanca() throws IOException {
        System.out.println("Africa/Casablanca ----------------------");
        zp.load("Africa/Casablanca").dump(System.out);
    }

    @Test
    public void dumpDhaka() throws IOException {
        System.out.println("Asia/Dhaka -----------------------------");
        zp.load("Asia/Dhaka").dump(System.out);
    }

    @Test
    public void dumpBerlin() throws IOException {
        System.out.println("Europe/Berlin --------------------------");
        zp.load("Europe/Berlin").dump(System.out);
    }

    @Test
    public void midsummer() {
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
            conflict.getExtraOffset(),
            is(2 * 3600));
    }

    @Test
    public void dhakaInDSTa() {
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
            conflict.getExtraOffset(),
            is(3600));
    }

    @Test
    public void dhakaInDSTb() {
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
            conflict.getExtraOffset(),
            is(3600));
    }

    @Test
    public void dhakaAtEndOf2009a() {
        TransitionHistory history = zp.load("Asia/Dhaka");
        PlainDate date = PlainDate.of(2009, 12, 31);
        PlainTime time = PlainTime.midnightAtEndOfDay();
        Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(7 * 3600));

        ZonalTransition conflict = // at first ambivalent time
            history.getConflictTransition(date, PlainTime.of(23, 0));

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
            conflict.getExtraOffset(),
            is(0));
    }

    @Test
    public void dhakaAtEndOf2009b() {
        TransitionHistory history = zp.load("Asia/Dhaka");
        PlainDate date = PlainDate.of(2009, 12, 31);
        PlainTime time = PlainTime.midnightAtEndOfDay();
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
            conflict.getExtraOffset(),
            is(0));
    }

}