package net.time4j.scale;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.tz.ZonalOffset;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TimeScaleTest {

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
            ).inTimezone(ZonalOffset.UTC); // 10 secs before GPS epoch
        assertThat(
            utc.transform(TimeScale.TAI),
            is(new BigDecimal("252892809.123456789")));
        assertThat(
            Moment.of(252892809, 123456789, TimeScale.TAI),
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
            is(new BigDecimal("-31535958.687161776")));
        assertThat(
            Moment.of(-31535959, 1_000_000_000 - 687_161_776, TimeScale.TT),
            is(ut));
    }

    @Test
    public void transformUT() {
        Moment utc =
            PlainDate.of(1973, 1, 1).atStartOfDay().inTimezone(ZonalOffset.UTC);
        assertThat(
            utc.transform(TimeScale.UT),
            is(new BigDecimal("31622400.813081576")));
        assertThat(
            Moment.of(31622400, 813_081_576, TimeScale.UT),
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
            is(315964800 + 9 - 2 * 365 * 86400L));
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
        assertThat(m1971.getNanosecond(TimeScale.TT), is(242_734_838)); // approximation
        assertThat(m1972_a.getNanosecond(TimeScale.TT), is(185_000_000));
        assertThat(m1972_b.getNanosecond(TimeScale.TT), is(171_000_000));
    }

    @Test
    public void getNanosecondUT() {
        Moment m1971 = PlainTimestamp.of(1971, 12, 31, 23, 59, 59).with(PlainTime.MILLI_OF_SECOND, 999).atUTC();
        Moment m1972 = PlainTimestamp.of(1972, 1, 1, 0, 0, 0).with(PlainTime.MILLI_OF_SECOND, 1).atUTC();
        assertThat(m1971.getNanosecond(TimeScale.UT), is(999_000_000));
        assertThat(m1972.getNanosecond(TimeScale.UT), is(855_482_401)); // approximation
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
            is("UT-1973-01-01T00:00:00,813081576Z"));
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
    public void getElapsedTimeTAIBefore1972() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1971, 12, 31),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        utc.getElapsedTime(TimeScale.TAI);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getNanosecondTAIBefore1972() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(1971, 12, 31),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        utc.getNanosecond(TimeScale.TAI);
    }

//    @Test
//    public void toStringTT1972() {
//        Moment moment =
//            PlainDate.of(1972, 1, 1).atStartOfDay().atUTC();
//        System.out.println(moment.minus(1, TimeUnit.SECONDS).toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UTC));
//
//        System.out.println(moment.minus(1, TimeUnit.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, TimeUnit.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, TimeUnit.SECONDS).toString(TimeScale.UT));
//    }
//
//    @Test
//    public void toStringUT1972() {
//        Moment moment =
//            PlainDate.of(1972, 6, 30)
//                .at(PlainTime.of(23, 59, 59))
//                .atUTC();
//        System.out.println(moment.toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, SI.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, SI.SECONDS).toString(TimeScale.UT));
//    }
//
//    @Test
//    public void toStringUT1977() {
//        Moment moment =
//            PlainDate.of(1977, 12, 31)
//                .at(PlainTime.of(23, 59, 59))
//                .atUTC();
//        System.out.println(moment.toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, SI.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, SI.SECONDS).toString(TimeScale.UT));
//    }
//
//    @Test
//    public void toStringUT1998() {
//        Moment moment =
//            PlainDate.of(1998, 12, 31)
//                .at(PlainTime.of(23, 59, 59))
//                .atUTC();
//        System.out.println(moment.toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, SI.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, SI.SECONDS).toString(TimeScale.UT));
//    }
//
//    @Test
//    public void toStringUT2005() {
//        Moment moment =
//            PlainDate.of(2005, 12, 31)
//                .at(PlainTime.of(23, 59, 59))
//                .atUTC();
//        System.out.println(moment.toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, SI.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, SI.SECONDS).toString(TimeScale.UT));
//    }
//
//    @Test
//    public void toStringUT2008() {
//        Moment moment =
//            PlainDate.of(2008, 12, 31)
//                .at(PlainTime.of(23, 59, 59))
//                .atUTC();
//        System.out.println(moment.toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, SI.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, SI.SECONDS).toString(TimeScale.UT));
//    }
//
//    @Test
//    public void toStringUT2012() {
//        Moment moment =
//            PlainDate.of(2012, 6, 30)
//                .at(PlainTime.of(23, 59, 59))
//                .atUTC();
//        System.out.println(moment.toString(TimeScale.UTC));
//        System.out.println(moment.toString(TimeScale.UT));
//        System.out.println(moment.plus(1, SI.SECONDS).toString(TimeScale.UT));
//        System.out.println(moment.plus(2, SI.SECONDS).toString(TimeScale.UT));
//    }

}