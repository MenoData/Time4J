package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.Leniency;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ThreetenFormatTest {

    @Test
    public void formatTime() {
        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter.ofTimePattern("hh:mm a", PatternType.CLDR, Locale.US);
        assertThat(
            formatter.format(LocalTime.of(17, 45)),
            is("05:45 PM")
        );
    }

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
            ChronoFormatter.ofTimestampPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR, Locale.ROOT);
        assertThat(
            formatter.withTimezone("UTC+2").format(LocalDateTime.of(2015, 3, 29, 2, 30)),
            is("2015-03-29T02:30+02:00")
        );
    }

    @Test(expected=IllegalArgumentException.class) // use offset instead of timezone id
    public void formatLocalDateTime4() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.ofTimestampPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR, Locale.ROOT);
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
            ChronoFormatter.ofDatePattern("yyyy-MM-dd", PatternType.CLDR, Locale.ROOT);
        HijrahDate date = HijrahDate.from(LocalDate.of(2015, 8, 21));
        formatter.with(Leniency.STRICT).format(date);
    }

    @Test
    public void formatHijrahDate2() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("yyyy-MM-dd", PatternType.CLDR, Locale.ROOT);
        HijrahDate date = HijrahDate.from(LocalDate.of(2015, 8, 21));
        assertThat(
            formatter.format(date),
            is("2015-08-21"));
    }

    @Test
    public void formatParsedTime() throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        TemporalAccessor ta = dtf.parse("24:00");
        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
                .addFixedInteger(PlainTime.ISO_HOUR, 2)
                .addPattern(":mm", PatternType.CLDR)
                .build();
        StringBuilder buffer = new StringBuilder();
        Set<ElementPosition> positions = formatter.print(ta, buffer);
        assertThat(buffer.toString(), is("24:00"));
        assertThat(positions.size(), is(2));
        assertThat(positions.stream().findFirst().get().getElement(), is(PlainTime.ISO_HOUR));
    }

    @Test
    public void formatParsedLS() throws IOException {
        String pattern = "yyyy-MM-dd HH:mm:ssXXX";
        DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT; // other DTF-objects cannot handle leap seconds at all
        TemporalAccessor ta = dtf.parse("2012-06-30T23:59:60Z");
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.ofMomentPattern(pattern, PatternType.CLDR, Locale.ROOT, () -> "Europe/Berlin");
        assertThat(formatter.format(ta), is("2012-07-01 01:59:60+02:00"));
    }

}
