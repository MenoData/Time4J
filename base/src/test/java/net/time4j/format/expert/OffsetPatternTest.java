package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.time4j.tz.OffsetSign.BEHIND_UTC;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class OffsetPatternTest {

 	@Parameterized.Parameters
        (name= "{index}: "
            + "(pattern={0},timezone={1},value={2},text={3})")
 	public static Iterable<Object[]> data() {
 		return Arrays.asList(
            new Object[][] {
                {"uuuuMMdd'T'HHmmssSSSSSSSSSx",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123456789Z",
                    "20120630T235960123456789+00"},
                {"uuuuMMdd'T'HHmmssSSSSSSSSSxx",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123456789Z",
                    "20120630T235960123456789+0000"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSxxx",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123456789Z",
                    "2012-06-30T23:59:60.123456789+00:00"},
                {"uuuuMMdd'T'HHmmx",
                    ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30),
                    "2012-06-30T12:00Z",
                    "20120630T1730+0530"},
                {"uuuuMMdd'T'HHmmxx",
                    ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30),
                    "2012-06-30T12:00Z",
                    "20120630T1730+0530"},
                {"uuuu-MM-dd'T'HH:mmxxx",
                    ZonalOffset.ofHoursMinutes(OffsetSign.AHEAD_OF_UTC, 5, 30),
                    "2012-06-30T12:00Z",
                    "2012-06-30T17:30+05:30"},
                {"uuuuMMdd'T'HHmmssSSSSSSSSSXX",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123456789Z",
                    "20120630T235960123456789Z"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123456789Z",
                    "2012-06-30T23:59:60.123456789Z"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSxxxxx",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123+00:00"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSZ",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123+0000"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSZZZZ",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123GMT"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123Z"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSOOOO",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123GMT"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSO",
                    ZonalOffset.UTC,
                    "2012-06-30T23:59:60,123000000Z",
                    "2012-06-30T23:59:60.123GMT"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXXXX",
                    ZonalOffset.atLongitude(BEHIND_UTC, 14, 30, 0.0),
                    "2012-06-30T12:00Z",
                    "2012-06-30T11:02:00.000000000-00:58"},
                {"uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXXXX",
                    ZonalOffset.atLongitude(BEHIND_UTC, 14, 30, 20.0),
                    "2012-06-30T12:00:00Z",
                    "2012-06-30T11:01:58.000000000-00:58:02"} // rounded!!!
           }
        );
    }

    private ChronoFormatter<Moment> formatter;
    private Moment value;
    private String text;

    public OffsetPatternTest(
        String pattern,
        ZonalOffset tzid,
        String value,
        String text
    ) throws ParseException {
        super();

        this.formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern(pattern, PatternType.CLDR).build()
                .withTimezone(tzid);
        this.value = Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(value);
        this.text = text;
    }

    @Test
    public void print() {
        assertThat(
            this.formatter.format(this.value),
            is(this.text));
    }

    @Test
    public void parse() throws ParseException {
        assertThat(
            this.formatter.parse(this.text),
            is(this.value));
    }

}