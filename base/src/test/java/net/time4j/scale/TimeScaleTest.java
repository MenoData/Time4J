package net.time4j.scale;

import net.time4j.CalendarUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.tz.ZonalOffset;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimeScaleTest {

    private static final long UTC_TAI_DELTA = ((1972 - 1958) * 365 + 3) * 86400;

    @BeforeClass
    public static void useTestDataForLeapSeconds() {
        System.setProperty(
            "net.time4j.scale.leapseconds.path",
            LeapSecondTest.TEST_DATA);
    }

    @Test
    public void epoch() {
        for (TimeScale scale : TimeScale.values()) {
            Moment epoch = Moment.of(0, scale);
            assertThat(
                "Error in elapsed time of: " + scale,
                epoch.getElapsedTime(scale),
                is(0L));
            assertThat(
                "Error in fraction value of: " + scale,
                epoch.getNanosecond(scale),
                is(0));
            System.out.println(scale + "-" + epoch.toString());
        }
    }

    @Test
    public void transformUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.UTC),
            is(new BigDecimal("252892809.123456789")));
        assertThat(
            Moment.of(252892809, 123456789, TimeScale.UTC),
            is(utc));
    }

    @Test
    public void transformGPS() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 1, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.GPS),
            is(new BigDecimal("1.123456789")));
        assertThat(
            Moment.of(1, 123456789, TimeScale.GPS),
            is(utc));
    }

    @Test
    public void transformTAI() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 50, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        long expectedSecs =
            UTC_TAI_DELTA
            + (8 * 365 + 2 + 5) * 86400 // 1972-01-01/1980-01-06
            - 10 // 10 secs before GPS epoch
            + 10 // TAI = UTC + 10 after 1972
            + LeapSeconds.getInstance().getCount(utc); // 9
        assertThat(
            utc.transform(TimeScale.TAI),
            is(new BigDecimal("694656009.123456789")));
        assertThat(
            Moment.of(expectedSecs, 123456789, TimeScale.TAI),
            is(utc));
    }

    @Test
    public void transformPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.POSIX),
            is(new BigDecimal("315964800.123456789")));
        assertThat(
            Moment.of(315964800L, 123456789, TimeScale.POSIX),
            is(utc));
    }

    @Test
    public void transformTT() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1973, 1, 1),
                PlainTime.of(0, 0, 0, 999_000_000)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.TT),
            is(new BigDecimal("31622445.183000000")));
        assertThat(
            Moment.of(31622445, 183_000_000, TimeScale.TT),
            is(utc));
        Moment ut =
            PlainTimestamp.of(
                PlainDate.of(1971, 1, 1),
                PlainTime.of(0, 0, 0, 0)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            ut.transform(TimeScale.TT),
            is(new BigDecimal("-31535958.728863512")));
        assertThat(
            Moment.of(-31535959, 1_000_000_000 - 728_863_512, TimeScale.TT),
            is(ut));
    }

    @Test
    public void transformUT() {
        Moment utc =
            PlainDate.of(1973, 1, 1).atStartOfDay().inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.UT),
            is(new BigDecimal("31622400.856857940")));
        assertThat(
            Moment.of(31622400, 856_857_940, TimeScale.UT),
            is(utc));
        Moment ut =
            PlainDate.of(1971, 1, 1).atStartOfDay().inTimezone(ZonalOffset.UTC);
        assertThat(
            ut.transform(TimeScale.UT),
            is(new BigDecimal("-31536000.000000000")));
        assertThat(
            Moment.of(-31536000, 0, TimeScale.UT),
            is(ut));
    }

    @Test
    public void getElapsedTimeUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getElapsedTime(TimeScale.UTC),
            is(315964800 + 9 - 2 * 365 * 86400L));
    }

    @Test
    public void getElapsedTimeTAI() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 50, 123456789)
            ).inTimezone(ZonalOffset.UTC); // 10 secs before GPS epoch
        assertThat(
            utc.getElapsedTime(TimeScale.TAI),
            is(UTC_TAI_DELTA + (8 * 365 + 2 + 5) * 86400 - 10 + 9 + 10));
    }

    @Test
    public void getElapsedTimeGPS() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 1, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getElapsedTime(TimeScale.GPS),
            is(1L));
    }

    @Test
    public void getElapsedTimePOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getElapsedTime(TimeScale.POSIX),
            is(315964800L));
    }

    @Test
    public void getElapsedTimeTT() {
        Moment m1971 = PlainTimestamp.of(1971, 12, 31, 23, 59, 59).with(PlainTime.MILLI_OF_SECOND, 999).atUTC();
        Moment m1972_a = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 1).atUTC();
        Moment m1972_b = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 987).atUTC();
        assertThat(m1971.getElapsedTime(TimeScale.TT), is(42L));
        assertThat(m1972_a.getElapsedTime(TimeScale.TT), is(42L));
        assertThat(m1972_b.getElapsedTime(TimeScale.TT), is(43L));
    }

    @Test
    public void getElapsedTimeUT() {
        Moment m1971 = PlainTimestamp.of(1971, 12, 31, 23, 59, 59).with(PlainTime.MILLI_OF_SECOND, 999).atUTC();
        Moment m1972 = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 1).atUTC();
        assertThat(m1971.getElapsedTime(TimeScale.UT), is(-1L));
        assertThat(m1972.getElapsedTime(TimeScale.UT), is(-1L));
    }

    @Test
    public void getNanosecondUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.UTC),
            is(123456789));
    }

    @Test
    public void getNanosecondGPS() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.GPS),
            is(123456789));
    }

    @Test
    public void getNanosecondTAI() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.TAI),
            is(123456789));
    }

    @Test
    public void getNanosecondPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 6),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.getNanosecond(TimeScale.POSIX),
            is(123456789));
    }

    @Test
    public void getNanosecondTT() {
        Moment m1971 = PlainTimestamp.of(1971, 12, 31, 23, 59, 59).with(PlainTime.MILLI_OF_SECOND, 999).atUTC();
        Moment m1972_a = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 1).atUTC();
        Moment m1972_b = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 987).atUTC();
        assertThat(m1971.getNanosecond(TimeScale.TT), is(282_784_289)); // approximation
        assertThat(m1972_a.getNanosecond(TimeScale.TT), is(185_000_000));
        assertThat(m1972_b.getNanosecond(TimeScale.TT), is(171_000_000));
    }

    @Test
    public void getNanosecondUT() {
        Moment m1971 = PlainTimestamp.of(1971, 12, 31, 23, 59, 59).with(PlainTime.MILLI_OF_SECOND, 999).atUTC();
        Moment m1972 = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 1).atUTC();
        assertThat(m1971.getNanosecond(TimeScale.UT), is(999_000_000));
        assertThat(m1972.getNanosecond(TimeScale.UT), is(898_395_543)); // approximation
    }

    @Test
    public void toStringUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc.toString(TimeScale.UTC),
            is("UTC-2012-06-30T23:59:60,123456789Z"));
    }

    @Test
    public void toStringGPS() {
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc1.toString(TimeScale.GPS),
            is("GPS-2012-07-01T00:00:15,123456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc2.toString(TimeScale.GPS),
            is("GPS-2012-07-01T00:00:16,123456789Z"));
    }

    @Test
    public void toStringTAI() {
        Moment m1971 =
            PlainTimestamp.of(1971, 12, 31, 0, 0).atUTC();
        assertThat(
            m1971.toString(TimeScale.TAI),
            is("TAI-1971-12-31T00:00:10,099784289Z"));
        Moment m1972 =
            PlainTimestamp.of(1972, 1, 1, 0, 0).atUTC();
        assertThat(
            m1972.toString(TimeScale.TAI),
            is("TAI-1972-01-01T00:00:10Z"));
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc1.toString(TimeScale.TAI),
            is("TAI-2012-07-01T00:00:34,123456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc2.toString(TimeScale.TAI),
            is("TAI-2012-07-01T00:00:35,123456789Z"));
    }

    @Test
    public void toStringPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc.toString(TimeScale.POSIX),
            is("POSIX-2012-06-30T23:59:59,123456789Z"));
    }

    @Test
    public void toStringTT() {
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            utc1.toString(TimeScale.TT),
            is("TT-2012-07-01T00:01:06,307456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            utc2.toString(TimeScale.TT),
            is("TT-2012-07-01T00:01:07,307456789Z"));
    }

    @Test
    public void toStringUT() {
        Moment ut1 =
            PlainTimestamp.of(1971, 1, 1, 0, 0, 0).with(PlainTime.NANO_OF_SECOND, 123456789).atUTC();
        assertThat(
            ut1.toString(TimeScale.UT),
            is("UT-1971-01-01T00:00:00,123456789Z"));
        Moment ut2 = Moment.of(31622400, 813_081_576, TimeScale.UT); // 1973-01-01T00:00
        assertThat(
            ut2.toString(TimeScale.UT),
            is("UT-1973-01-01T00:00:00,815951148Z"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void getElapsedTimeGPSBefore1980() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        utc.getElapsedTime(TimeScale.GPS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getNanosecondGPSBefore1980() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1980, 1, 5),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        utc.getNanosecond(TimeScale.GPS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getElapsedTimeTAIBefore1958() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1957, 12, 31),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        utc.getElapsedTime(TimeScale.TAI);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getNanosecondTAIBefore1958() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1957, 12, 31),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        utc.getNanosecond(TimeScale.TAI);
    }

    @Test
    public void delta_UTC_UT_SmallerThan_0_9() {
        double[] t = {getT(2004, 12), getT(2008, 2), getT(2011, 3), getT(2014, 4), getT(2017, 4)};
        double[] iers = {0.4964766 + 22, 0.3182364 + 23, 0.1910293 + 24, 0.2234228 + 25, -0.4492437 + 27};

        for (int i = 0; i < iers.length; i++) {
            iers[i] += 42.184;
        }

        System.out.println(Arrays.toString(polynom(t, iers)));
        // [63.59341315150316, 0.17141689687396672, 0.01420115217577018, -0.001127450524364615, 4.206031730419899E-5]

        System.out.println(TimeScale.deltaT(2004, 12)); // 64.70995596369828, alt=64.70995596369828
        System.out.println(TimeScale.deltaT(2005, 1)); // 64.70128510827738, alt=64.68633720312503
        System.out.println(TimeScale.deltaT(2012, 1)); // 66.63245712358452, alt=67.60987845312503
        System.out.println(TimeScale.deltaT(2016, 3)); // 68.20460258010081, alt=69.61012532812497
        System.out.println(TimeScale.deltaT(2017, 1)); // 68.6063591755649, alt=70.03346220312504

        Moment m2005 = PlainTimestamp.of(2005, 1, 15, 0, 0).atUTC();
        System.out.println(m2005.toString(TimeScale.UTC)); // UTC-2005-01-15T00:00:00Z
        System.out.println(m2005.toString(TimeScale.UT)); // UT-2005-01-14T23:59:59,483543168Z

        Moment m2016 = PlainTimestamp.of(2016, 3, 15, 0, 0).atUTC();
        System.out.println(m2016.toString(TimeScale.UTC)); // UTC-2016-03-15T00:00:00Z

        System.out.println(m2016.toString(TimeScale.UT));
        // neu=UT-2016-03-14T23:59:59,982221824Z
        // alt=UT-2016-03-14T23:59:58,573874688Z

        PlainTimestamp start = PlainTimestamp.of(1972, 1, 1, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2017, 5, 1, 0, 0);

        while (start.isBefore(end)) {
            Moment m = start.atUTC();
            BigDecimal utc = m.transform(TimeScale.UTC);
            BigDecimal ut = m.transform(TimeScale.UT);

            BigDecimal delta =
                utc.subtract(BigDecimal.valueOf(LeapSeconds.getInstance().getCount(m))).subtract(ut);
//            System.out.println(
//                start + ": utc-ut = " + delta + ", delta-T=" + TimeScale.deltaT(start.getCalendarDate()));
            assertThat(start.toString(), Double.compare(Math.abs(delta.doubleValue()), 0.9) < 0, is(true));
            start = start.plus(1, CalendarUnit.DAYS);
        }
    }

    private static double getT(int year, int month) {
        return year + (month - 0.5) / 12 - 2000;
    }

    private static double[] polynom(
        double[] x,
        double[] y
    ) {

        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays of different lengths.");
        }

        int n = x.length - 1;
        double[] a  = new double[x.length];

        for (int i = 0; i <= n; i++) {
            a[i] = y[i];
        }

        for (int i = 1; i <= n; i++) {
            for (int j = n; j >= i; j--) {
                a[j] = (a[j] - a[j - 1]) / (x[j] - x[j - i]);
            }
        }

        for (int i = 0; i <= n - 1; i++) {
            for (int j = n - 1; j >= i; j--) {
                a[j] = a[j] - x[j - i] * a[j + 1];
            }
        }

        return a;

    }

//    @Test
//    public void prediction_2018_2025() {
//        double[] t = {getT(2017, 1), getT(2020, 1), getT(2025, 1)};
//        double[] prediction = {-0.593 + 27 + 42.184, 70.1, 73.1};
//
//        System.out.println(Arrays.toString(polynom(t, prediction)));
//        // [64.1602554253472, 0.053364583333332216, 0.012124999999999983]
//
//        PlainDate start = PlainDate.of(2018, 1, 1);
//        PlainDate end = PlainDate.of(2026, 1, 1);
//
//        while (start.isBefore(end)) {
//            System.out.println(
//                start + ": delta-T=" + TimeScale.deltaT(start.getYear(), start.getMonth()));
//            start = start.plus(1, CalendarUnit.MONTHS);
//        }
//    }

}