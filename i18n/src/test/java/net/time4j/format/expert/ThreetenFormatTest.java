package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ThreetenFormatTest {

    @Test
    public void formatLocalDateTime1() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        assertThat(
            formatter.withTimezone("Europe/Berlin").format(LocalDateTime.of(2015, 3, 29, 2, 30)),
            is("2015-03-29T03:30+02:00")
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void formatLocalDateTime2() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        formatter.format(LocalDateTime.of(2015, 3, 29, 2, 30));
    }

    @Test
    public void formatZonedDateTime1() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        ZonedDateTime zdt = LocalDateTime.of(2015, 6, 1, 12, 0).atZone(ZoneId.of("Europe/London"));
        assertThat(
            formatter.withTimezone("Europe/Berlin").format(zdt),
            is("2015-06-01T13:00+02:00")
        );
    }

    @Test
    public void formatZonedDateTime2() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        ZonedDateTime zdt = LocalDateTime.of(2015, 3, 29, 2, 30).atZone(ZoneId.of("Europe/Berlin"));
        assertThat(
            formatter.format(zdt),
            is("2015-03-29T03:30+02:00")
        );
    }

    @Test
    public void formatInstant1() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        ZonedDateTime zdt = LocalDateTime.of(2015, 3, 29, 2, 30).atZone(ZoneId.of("Europe/Berlin"));
        assertThat(
            formatter.withTimezone("Europe/Berlin").format(zdt.toInstant()),
            is("2015-03-29T03:30+02:00")
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void formatInstant2() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        ZonedDateTime zdt = LocalDateTime.of(2015, 3, 29, 2, 30).atZone(ZoneId.of("Europe/Berlin"));
        formatter.format(zdt.toInstant());
    }
/*
    // for comparison: using Time4J-types is more strict
    // -> printing PlainTimestamp with Moment-formatter is not compilable
    @Test
    public void formatPlainTimestamp() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        assertThat(
            formatter.withTimezone("UTC+02:00").format(PlainTimestamp.of(2015, 3, 29, 2, 30)),
            is("2015-03-29T02:30+02:00")
        );
    }
*/
}
