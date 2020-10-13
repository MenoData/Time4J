package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.format.Attributes;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class MomentScaleTest {

    private static final ChronoFormatter<Moment> STD_FORMATTER =
        ChronoFormatter.ofMomentPattern(
            "uuuu-MM-dd HH:mm:ss.SSSSSSSSSX", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);
    private static final ChronoFormatter<Moment> POSIX_FORMATTER =
        STD_FORMATTER.with(Attributes.TIME_SCALE, TimeScale.POSIX);
    private static final ChronoFormatter<Moment> UTC_FORMATTER =
        STD_FORMATTER.with(Attributes.TIME_SCALE, TimeScale.UTC);
    private static final ChronoFormatter<Moment> GPS_FORMATTER =
        STD_FORMATTER.with(Attributes.TIME_SCALE, TimeScale.GPS);
    private static final ChronoFormatter<Moment> TAI_FORMATTER =
        STD_FORMATTER.with(Attributes.TIME_SCALE, TimeScale.TAI);

    @Test
    public void formatPOSIX() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            POSIX_FORMATTER.format(utc),
            is("2012-06-30 23:59:59.123456789Z"));
    }

    @Test
    public void parsePOSIX() throws ParseException {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            POSIX_FORMATTER.parse("2012-06-30 23:59:59.123456789Z"),
            is(utc));
    }

    @Test(expected=ParseException.class)
    public void parsePOSIX_leapsecond() throws ParseException {
        POSIX_FORMATTER.parse("2012-06-30 23:59:60.123456789Z");
    }

    @Test
    public void formatUTC() {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            STD_FORMATTER.format(utc),
            is("2012-06-30 23:59:60.123456789Z"));
        assertThat(
            UTC_FORMATTER.format(utc),
            is("2012-06-30 23:59:60.123456789Z"));
    }

    @Test
    public void parseUTC() throws ParseException {
        Moment utc =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            STD_FORMATTER.parse("2012-06-30 23:59:60.123456789Z"),
            is(utc));
        assertThat(
            UTC_FORMATTER.parse("2012-06-30 23:59:60.123456789Z"),
            is(utc));
    }

    @Test
    public void formatGPS() {
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            GPS_FORMATTER.format(utc1),
            is("2012-07-01 00:00:15.123456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            GPS_FORMATTER.format(utc2),
            is("2012-07-01 00:00:16.123456789Z"));
    }

    @Test
    public void parseGPS() throws ParseException {
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            GPS_FORMATTER.parse("2012-07-01 00:00:15.123456789Z"),
            is(utc1));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            GPS_FORMATTER.parse("2012-07-01 00:00:16.123456789Z"),
            is(utc2));
    }

    @Test(expected=ParseException.class)
    public void parseGPS_leapsecond() throws ParseException {
        GPS_FORMATTER.parse("2012-06-30 23:59:60.123456789Z");
    }

    @Test
    public void formatTAI1() { // no leap second, continuous counting on TAI-day-change
        Moment utc0_a =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 25, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.format(utc0_a),
            is("2012-06-30 23:59:59.123456789Z"));
        Moment utc0_b =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 26, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.format(utc0_b),
            is("2012-07-01 00:00:00.123456789Z"));
    }

    @Test
    public void formatTAI2() { // around leap second, continuous counting on TAI-scale
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.format(utc1),
            is("2012-07-01 00:00:33.123456789Z"));
        Moment utc2 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC).plus(1, SI.SECONDS);
        assertThat(
            TAI_FORMATTER.format(utc2),
            is("2012-07-01 00:00:34.123456789Z"));
        Moment utc3 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.format(utc3),
            is("2012-07-01 00:00:35.123456789Z"));
    }

    @Test
    public void parseTAI1() throws ParseException { // no leap second, continuous counting on TAI-day-change
        Moment utc0_a =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 25, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.parse("2012-06-30 23:59:59.123456789Z"),
            is(utc0_a));
        Moment utc0_b =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 26, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.parse("2012-07-01 00:00:00.123456789Z"),
            is(utc0_b));
    }

    @Test
    public void parseTAI2() throws ParseException { // around leap second, continuous counting on TAI-scale
        Moment utc1 =
            PlainTimestamp.of(
                PlainDate.of(2012, 6, 30),
                PlainTime.of(23, 59, 59, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.parse("2012-07-01 00:00:33.123456789Z"),
            is(utc1));
        Moment utc2 =
            PlainTimestamp.of(2012, 1, 1, 0, 0).atUTC().with(Moment.nextLeapSecond()).plus(123456789, SI.NANOSECONDS);
        assertThat(
            TAI_FORMATTER.parse("2012-07-01 00:00:34.123456789Z"),
            is(utc2));
        Moment utc3 =
            PlainTimestamp.of(
                PlainDate.of(2012, 7, 1),
                PlainTime.of(0, 0, 0, 123456789)
            ).inTimezone(ZonalOffset.UTC);
        assertThat(
            TAI_FORMATTER.parse("2012-07-01 00:00:35.123456789Z"),
            is(utc3));
    }

    @Test(expected=ParseException.class)
    public void parseTAI_leapsecond() throws ParseException {
        TAI_FORMATTER.parse("2012-06-30 23:59:60.123456789Z");
    }

}