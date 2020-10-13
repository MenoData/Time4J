package net.time4j.format.expert;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import org.junit.Test;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThreetenFormatTest {

    @Test
    public void formatAndParseInstant1() throws ParseException {
        ChronoFormatter<Instant> formatter =
            ChronoFormatter.setUp(Moment.threeten(), Locale.ROOT)
                .addPattern("uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR).build().withTimezone("Europe/Berlin");
        ZonedDateTime zdt = LocalDateTime.of(2015, 3, 29, 2, 30).atZone(ZoneId.of("Europe/Berlin"));
        assertThat(
            formatter.format(zdt.toInstant()),
            is("2015-03-29T03:30+02:00")
        );
        assertThat(
            formatter.parse("2015-03-29T03:30+02:00"),
            is(zdt.toInstant())
        );
    }

    @Test
    public void formatAndParseInstant2() throws ParseException {
        ChronoFormatter<Instant> formatter =
            ChronoFormatter.ofPattern(
                "uuuu-MM-dd'T'HH:mmXXX", PatternType.CLDR, Locale.ROOT, Moment.threeten()
            ).withTimezone("Europe/Berlin");
        ZonedDateTime zdt = LocalDateTime.of(2015, 3, 29, 2, 30).atZone(ZoneId.of("Europe/Berlin"));
        assertThat(
            formatter.format(zdt.toInstant()),
            is("2015-03-29T03:30+02:00")
        );
        assertThat(
            formatter.parse("2015-03-29T03:30+02:00"),
            is(zdt.toInstant())
        );
    }

    @Test
    public void formatLocalDateWithThreetenPattern1() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G yyyy-MM-dd (QQQ)", PatternType.THREETEN, Locale.ENGLISH);
        assertThat(
            formatter.format(PlainDate.of(1582, 10, 14)),
            is("AD 1582-10-14 (Q4)")
        );
    }

    @Test
    public void formatLocalDateWithThreetenPattern2() {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("G yyyy-MM-dd (QQQ)", PatternType.THREETEN, Locale.ENGLISH);
        assertThat(
            formatter.format(PlainDate.of(0, 1, 1)),
            is("BC 0001-01-01 (Q1)")
        );
    }

    @Test
    public void formatTwoDigitYearWithThreetenPattern() throws ParseException {
        ChronoFormatter<PlainDate> formatter =
            ChronoFormatter.ofDatePattern("yy-MM-dd", PatternType.THREETEN, Locale.ENGLISH);
        assertThat(
            formatter.parse("70-01-01"),
            is(PlainDate.of(2070, 1, 1))
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void formatShortDayOfWeekWithThreetenPattern() {
        ChronoFormatter.ofDatePattern("EEEEEE", PatternType.THREETEN, Locale.ENGLISH);
    }

    @Test
    public void formatLocalTimeWithThreetenPattern() {
        ChronoFormatter<PlainTime> formatter =
            ChronoFormatter.ofTimePattern("pph:mm:ss a n", PatternType.THREETEN, Locale.ENGLISH);
        assertThat(
            formatter.format(PlainTime.of(14, 10, 14, 123456789)),
            is(" 2:10:14 pm 123456789")
        );
    }

    @Test
    public void formatLocalDateTimeWithThreetenPattern() {
        ChronoFormatter<PlainTimestamp> formatter =
            ChronoFormatter.ofTimestampPattern("EEE, uuuu-MM-dd HH:mm:ss.SSSSSS", PatternType.THREETEN, Locale.ENGLISH);
        assertThat(
            formatter.format(PlainTimestamp.of(2015, 5, 16, 17, 45, 30).plus(123456789, ClockUnit.NANOS)),
            is("Sat, 2015-05-16 17:45:30.123456")
        );
    }

}
