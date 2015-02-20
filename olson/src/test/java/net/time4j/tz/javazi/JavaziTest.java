package net.time4j.tz.javazi;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.ZoneProvider;

import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class JavaziTest {

    private static boolean testable = false;
    private static ZoneProvider zp = null;

    @BeforeClass
    public static void init() {
        zp = new TraditionalZoneProviderSPI();
        testable = !zp.getVersion().isEmpty();

        System.out.println(
            "javazi-version="
            + (testable ? zp.getVersion() : "<NOT AVAILABLE>"));
    }

    @Test
    public void dumpBerlin() throws IOException {
        if (testable) {
            zp.load("Europe/Berlin").dump(System.out);
        }
    }

    @Test
    public void hasHistory() throws IOException {
        if (testable) {
            assertThat(
                (zp.load("Europe/Berlin") != null),
                is(true));
        }
    }

    @Test
    public void midsummer() throws IOException {
        if (testable) {
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
    }

    @Test
    public void dhakaAtEndOf2009() throws IOException {
        if (testable) {
            PlainDate date = PlainDate.of(2009, 12, 31);
            PlainTime time = PlainTime.of(23, 59); // dirty hack in JDK for T24
            Moment m = date.at(time).at(ZonalOffset.ofTotalSeconds(7 * 3600));
            ZonalTransition conflict =
                zp.load("Asia/Dhaka").getConflictTransition(date, time);
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

}