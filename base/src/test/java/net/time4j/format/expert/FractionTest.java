package net.time4j.format.expert;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.format.Leniency;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FractionTest {

    @Test
    public void smartMoment() throws ParseException {
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm:ss.SSSSSS[X]", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);
        Moment expected = PlainTimestamp.of(2016, 7, 14, 15, 45, 0).plus(123, ClockUnit.MILLIS).atUTC();
        assertThat(f.parse("2016-07-14T15:45:00.123"), is(expected));
        assertThat(f.parse("2016-07-14T15:45:00.123Z"), is(expected));

        expected = expected.plus(456, TimeUnit.MICROSECONDS);
        assertThat(f.parse("2016-07-14T15:45:00.123456"), is(expected));
        assertThat(f.parse("2016-07-14T15:45:00.123456Z"), is(expected));
    }

    @Test(expected=ParseException.class)
    public void strictMoment1() throws ParseException {
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm:ss.SSSSSS[X]", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC
            ).with(Leniency.STRICT);
        f.parse("2016-07-14T15:45:00.123"); // less than 6 fractional digits
    }

    @Test(expected=ParseException.class)
    public void strictMoment2() throws ParseException {
        ChronoFormatter<Moment> f =
            ChronoFormatter.ofMomentPattern(
                "uuuu-MM-dd'T'HH:mm:ss.SSSSSS[X]", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC
            ).with(Leniency.STRICT);
        f.parse("2016-07-14T15:45:00.123Z"); // less than 6 fractional digits
    }

}
