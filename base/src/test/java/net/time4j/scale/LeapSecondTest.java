package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.base.GregorianDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Folgender Test basiert auf leapseconds.data!
 *
 * @author Meno Hochschild
 */
@RunWith(JUnit4.class)
public class LeapSecondTest {

    // in case of change, please also update pom-file of core-module
    public static final String TEST_DATA = "data/leapseconds2017.data";

    private static final int UTC_OFFSET = 2 * 365 * 86400;
    private static final long NLS_OFFSET =
        (31 + 31 + 30 + 31 + 30 + 31 + 100 * 365 + 24) * 86400L;
    private static final long UTC_2012_06_30_LS = 1341100824L - UTC_OFFSET;

    @BeforeClass
    public static void initNegativeLS() {
        System.setProperty(
            "net.time4j.scale.leapseconds.path",
            TEST_DATA);

        LeapSeconds instance = LeapSeconds.getInstance();
        System.out.println("LS-Initialization => " + instance);

        if (instance.iterator().next().getDate().getYear() != 2112) {
            int year = 2112;
            int month = 12;
            int dayOfMonth = 31;
            instance.registerNegativeLS(year, month, dayOfMonth);
        }
    }

    @Test
    public void isEnabled() {
        assertThat(
            LeapSeconds.getInstance().isEnabled(),
            is(!Boolean.getBoolean("net.time4j.scale.leapseconds.suppressed"))
        );
    }

    @Test
    public void isExtensible() {
        assertThat(
            LeapSeconds.getInstance().isExtensible(),
            is(!Boolean.getBoolean("net.time4j.scale.leapseconds.final")
                && LeapSeconds.getInstance().isEnabled())
        );
    }

    @Test
    public void getCount() {
        LeapSeconds instance = LeapSeconds.getInstance();
        int expected = 27;
        if (instance.iterator().next().getDate().getYear() == 2112) {
            expected = 28;
        }
        assertThat(
            LeapSeconds.getInstance().getCount(),
            is(expected));
        assertThat(
            LeapSeconds.getInstance().getCount(Moment.UNIX_EPOCH),
            is(0));
        assertThat(
            LeapSeconds.getInstance().getCount(PlainTimestamp.of(1974, 1, 1, 0, 0).atUTC()),
            is(3));
        assertThat(
            LeapSeconds.getInstance().getCount(PlainTimestamp.of(2010, 1, 1, 0, 0).atUTC()),
            is(24));
    }

    @Test
    public void register() { // see @Before-Initialisierung
        LeapSeconds instance = LeapSeconds.getInstance();

        for (LeapSecondEvent lse : instance) {
            System.out.println("Registered: " + lse);
            assertThat(lse.getShift(), is(-1));
            assertThat(lse.getDate().getYear(), is(2112));
            assertThat(lse.getDate().getMonth(), is(12));
            assertThat(lse.getDate().getDayOfMonth(), is(31));
            assertThat(
                ((ExtendedLSE) lse).raw(),
                is(UTC_2012_06_30_LS // 2112-12-31 (UNIX)
                - 25
                + NLS_OFFSET));
            assertThat(
                ((ExtendedLSE) lse).utc(),
                is(UTC_2012_06_30_LS // 2112-12-31 (UTC)
                + NLS_OFFSET
                + 2 // leap seconds of 2015 + 2016
                - 1)); // negative LS
            break;
        }
    }

    @Test
    public void getShift_GregorianDate() {
        LeapSeconds instance = LeapSeconds.getInstance();
        assertThat(instance.getShift(PlainDate.of(2012, 6, 30)), is(1));
        assertThat(instance.getShift(PlainDate.of(2012, 7, 1)), is(0));
    }

    @Test
    public void getShift_long() {
        LeapSeconds instance = LeapSeconds.getInstance();
        assertThat(instance.getShift(UTC_2012_06_30_LS + 1), is(0));
        assertThat(instance.getShift(UTC_2012_06_30_LS), is(1));
        assertThat(instance.getShift(UTC_2012_06_30_LS - 1), is(0));
    }

    @Test
    public void isPositiveLS() {
        LeapSeconds instance = LeapSeconds.getInstance();
        assertThat(instance.isPositiveLS(UTC_2012_06_30_LS + 1), is(false));
        assertThat(instance.isPositiveLS(UTC_2012_06_30_LS), is(true));
        assertThat(instance.isPositiveLS(UTC_2012_06_30_LS - 1), is(false));
    }

    @Test
    public void iterator() {
        int i = 0;
        for (LeapSecondEvent lse : LeapSeconds.getInstance()) {
            PlainDate date = toPlainDate(lse.getDate());
            if (date.getYear() < 2017) {
                assertThat(lse.getShift(), is(1));
                switch (i) {
                    case 0:
                        assertThat(date, is(PlainDate.of(2016, 12, 31)));
                        break;
                    case 1:
                        assertThat(date, is(PlainDate.of(2015, 6, 30)));
                        break;
                    case 2:
                        assertThat(date, is(PlainDate.of(2012, 6, 30)));
                        break;
                    case 3:
                        assertThat(date, is(PlainDate.of(2008, 12, 31)));
                        break;
                    case 4:
                        assertThat(date, is(PlainDate.of(2005, 12, 31)));
                        break;
                    case 5:
                        assertThat(date, is(PlainDate.of(1998, 12, 31)));
                        break;
                    case 6:
                        assertThat(date, is(PlainDate.of(1997, 6, 30)));
                        break;
                    case 7:
                        assertThat(date, is(PlainDate.of(1995, 12, 31)));
                        break;
                    case 8:
                        assertThat(date, is(PlainDate.of(1994, 6, 30)));
                        break;
                    case 9:
                        assertThat(date, is(PlainDate.of(1993, 6, 30)));
                        break;
                    case 10:
                        assertThat(date, is(PlainDate.of(1992, 6, 30)));
                        break;
                    case 11:
                        assertThat(date, is(PlainDate.of(1990, 12, 31)));
                        break;
                    case 12:
                        assertThat(date, is(PlainDate.of(1989, 12, 31)));
                        break;
                    case 13:
                        assertThat(date, is(PlainDate.of(1987, 12, 31)));
                        break;
                    case 14:
                        assertThat(date, is(PlainDate.of(1985, 6, 30)));
                        break;
                    case 15:
                        assertThat(date, is(PlainDate.of(1983, 6, 30)));
                        break;
                    case 16:
                        assertThat(date, is(PlainDate.of(1982, 6, 30)));
                        break;
                    case 17:
                        assertThat(date, is(PlainDate.of(1981, 6, 30)));
                        break;
                    case 18:
                        assertThat(date, is(PlainDate.of(1979, 12, 31)));
                        break;
                    case 19:
                        assertThat(date, is(PlainDate.of(1978, 12, 31)));
                        break;
                    case 20:
                        assertThat(date, is(PlainDate.of(1977, 12, 31)));
                        break;
                    case 21:
                        assertThat(date, is(PlainDate.of(1976, 12, 31)));
                        break;
                    case 22:
                        assertThat(date, is(PlainDate.of(1975, 12, 31)));
                        break;
                    case 23:
                        assertThat(date, is(PlainDate.of(1974, 12, 31)));
                        break;
                    case 24:
                        assertThat(date, is(PlainDate.of(1973, 12, 31)));
                        break;
                    case 25:
                        assertThat(date, is(PlainDate.of(1972, 12, 31)));
                        break;
                    case 26:
                        assertThat(date, is(PlainDate.of(1972, 6, 30)));
                        break;
                    default:
                        break;
                }
                i++;
            }
        }
        assertThat(i, is(27));
    }

    @Test
    public void stream() {
        Collection<LeapSecondEvent> coll = new ArrayList<>();

        for (LeapSecondEvent lse : LeapSeconds.getInstance()) {
            coll.add(lse);
        }

        assertThat(LeapSeconds.getInstance().stream().collect(Collectors.toList()), is(coll));
    }

    @Test
    public void enhance() {
        LeapSeconds instance = LeapSeconds.getInstance();
        assertThat(
            instance.enhance(-1L),
            is(-1L - 2 * 365 * 86400));
        assertThat(
            instance.enhance(1341100799L),
            is(UTC_2012_06_30_LS - 1));
        assertThat(
            instance.enhance(1341100800L),
            is(UTC_2012_06_30_LS + 1));
        assertThat(
            instance.enhance(1341100801L),
            is(UTC_2012_06_30_LS + 2));
        assertThat(
            instance.enhance(1341100799L + NLS_OFFSET),
            is(UTC_2012_06_30_LS + 2 + NLS_OFFSET));
        assertThat(
            instance.enhance(1341100800L + NLS_OFFSET),
            is(UTC_2012_06_30_LS + 2 + NLS_OFFSET));
        assertThat(
            instance.enhance(1341100801L + NLS_OFFSET),
            is(UTC_2012_06_30_LS + 3 + NLS_OFFSET));
    }

    @Test
    public void strip() {
        LeapSeconds instance = LeapSeconds.getInstance();
        assertThat(
            instance.strip(UTC_2012_06_30_LS + 1),
            is(1341100800L));
        assertThat(
            instance.strip(UTC_2012_06_30_LS),
            is(1341100799L));
        assertThat(
            instance.strip(UTC_2012_06_30_LS - 1),
            is(1341100799L));
        assertThat(
            instance.strip(UTC_2012_06_30_LS + 1 + NLS_OFFSET),
            is(1341100798L + NLS_OFFSET));
        assertThat(
            instance.strip(UTC_2012_06_30_LS + 2 + NLS_OFFSET),
            is(1341100800L + NLS_OFFSET));
        assertThat(
            instance.strip(UTC_2012_06_30_LS + 3 + NLS_OFFSET),
            is(1341100801L + NLS_OFFSET));
    }

    @Test
    public void getDateOfExpiration() {
        GregorianDate expected = PlainDate.of(2017, 12, 28);
        GregorianDate date = LeapSeconds.getInstance().getDateOfExpiration();
        assertThat(
            date.getDayOfMonth(),
            is(expected.getDayOfMonth()));
        assertThat(
            date.getMonth(),
            is(expected.getMonth()));
        assertThat(
            date.getYear(),
            is(expected.getYear()));
    }

    private static PlainDate toPlainDate(GregorianDate date) {
        return PlainDate.of(
            date.getYear(), date.getMonth(), date.getDayOfMonth());
    }

}
