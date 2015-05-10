package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.format.Leniency;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
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

    @Test(expected=IllegalArgumentException.class) // no offset
    public void formatLocalDateTime2() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        formatter.format(LocalDateTime.of(2015, 3, 29, 2, 30));
    }

    @Test
    public void formatLocalDateTime3() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        assertThat(
            formatter.withTimezone("UTC+2").format(LocalDateTime.of(2015, 3, 29, 2, 30)),
            is("2015-03-29T02:30+02:00")
        );
    }

    @Test(expected=IllegalArgumentException.class) // use offset instead of timezone id
    public void formatLocalDateTime4() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.setUp(PlainTimestamp.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        formatter.withTimezone("Europe/Berlin").format(LocalDateTime.of(2015, 3, 29, 2, 30));
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

    @Test(expected=IllegalArgumentException.class) // no timezone id
    public void formatInstant2() {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build();
        ZonedDateTime zdt = LocalDateTime.of(2015, 3, 29, 2, 30).atZone(ZoneId.of("Europe/Berlin"));
        formatter.format(zdt.toInstant());
    }

    @Test(expected=IllegalArgumentException.class) // non-iso in strict mode
    public void formatHijrahDate1() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("yyyy-MM-dd", PatternType.CLDR).build();
        HijrahDate date = HijrahDate.from(LocalDate.of(2015, 8, 21));
        formatter.with(Leniency.STRICT).format(date);
    }

    @Test
    public void formatHijrahDate2() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("yyyy-MM-dd", PatternType.CLDR).build();
        HijrahDate date = HijrahDate.from(LocalDate.of(2015, 8, 21));
        assertThat(
            formatter.format(date),
            is("2015-08-21"));
    }

}
