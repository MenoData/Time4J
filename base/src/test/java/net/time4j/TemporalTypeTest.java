package net.time4j;

import net.time4j.base.TimeSource;
import net.time4j.engine.ChronoException;
import net.time4j.scale.LeapSeconds;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TemporalTypeTest {

    @Test
    public void javaUtilDateToTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.translate(jud),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

    @Test
    public void javaUtilDateFromTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.from(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud));
    }

    @Test
    public void millisSinceUnixToTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.MILLIS_SINCE_UNIX.translate(jud.getTime()),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

    @Test
    public void millisSinceUnixFromTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.MILLIS_SINCE_UNIX.from(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud.getTime()));
    }

    @Test
    public void oldApiTimezone() {
        Timezone tz = Timezone.of("Europe/Rome");
        assertThat(
            TemporalType.JAVA_UTIL_TIMEZONE.from(tz).getOffset(
                GregorianCalendar.AD, 2020, Calendar.JULY, 4, Calendar.SATURDAY, 0),
            is(2 * 3600 * 1000));
    }

    @Test
    public void javaUtilCalendarToTime4J() {
        TimeZone jut = TimeZone.getTimeZone("Europe/Rome");
        Timezone tz = TemporalType.JAVA_UTIL_TIMEZONE.translate(jut);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(jut);
        cal.set(1582, Calendar.OCTOBER, 4, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertThat(
            TemporalType.JAVA_UTIL_CALENDAR.translate(cal).toMoment(),
            is(PlainTimestamp.of(1582, 10, 14, 0, 0).in(tz)));
        assertThat(
            TemporalType.JAVA_UTIL_CALENDAR.translate(cal).getTimezone0(),
            is(tz));
    }

    @Test
    public void javaUtilCalendarFromTime4J() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setGregorianChange(new java.util.Date(Long.MIN_VALUE));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(4);
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTimeInMillis(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_CALENDAR.from(
                Moment.of(1341100800L, TimeScale.POSIX).inZonalView("java.util.TimeZone~Europe/Paris")),
            is(cal));
    }

    @Test
    public void localDateToTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE.translate(LocalDate.of(2015, java.time.Month.APRIL, 30)),
            is(PlainDate.of(2015, 4, 30))
        );
        assertThat(
            TemporalType.LOCAL_DATE.translate(LocalDate.MIN),
            is(PlainDate.axis().getMinimum())
        );
        assertThat(
            TemporalType.LOCAL_DATE.translate(LocalDate.MAX),
            is(PlainDate.axis().getMaximum())
        );
    }

    @Test
    public void localDateFromTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE.from(PlainDate.of(2015, 4, 30)),
            is(LocalDate.of(2015, java.time.Month.APRIL, 30))
        );
        assertThat(
            TemporalType.LOCAL_DATE.from(PlainDate.axis().getMinimum()),
            is(LocalDate.MIN)
        );
        assertThat(
            TemporalType.LOCAL_DATE.from(PlainDate.axis().getMaximum()),
            is(LocalDate.MAX)
        );
    }

    @Test
    public void localTimeToTime4J() {
        assertThat(
            TemporalType.LOCAL_TIME.translate(LocalTime.of(17, 45, 11, 4)),
            is(PlainTime.of(17, 45, 11, 4))
        );
        assertThat(
            TemporalType.LOCAL_TIME.translate(LocalTime.MIN),
            is(PlainTime.axis().getMinimum())
        );
        assertThat(
            TemporalType.LOCAL_TIME.translate(LocalTime.MAX),
            is(PlainTime.of(23, 59, 59, 999_999_999))
        );
    }

    @Test
    public void localTimeFromTime4J() {
        assertThat(
            TemporalType.LOCAL_TIME.from(PlainTime.of(17, 45, 11, 4)),
            is(LocalTime.of(17, 45, 11, 4))
        );
        assertThat(
            TemporalType.LOCAL_TIME.from(PlainTime.axis().getMinimum()),
            is(LocalTime.MIN)
        );
    }

    @Test
    public void localTimeFromTime24() {
        assertThat(
            TemporalType.LOCAL_TIME.from(PlainTime.axis().getMaximum()),
            is(LocalTime.MIDNIGHT));
    }

    @Test
    public void localDateTimeToTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE_TIME.translate(
                LocalDateTime.of(2015, java.time.Month.APRIL, 30, 17, 45)),
            is(PlainTimestamp.of(2015, 4, 30, 17, 45))
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.translate(LocalDateTime.MIN),
            is(PlainTimestamp.axis().getMinimum())
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.translate(LocalDateTime.MAX),
            is(PlainTimestamp.axis().getMaximum())
        );
    }

    @Test
    public void localDateTimeFromTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE_TIME.from(PlainTimestamp.of(2015, 4, 30, 17, 45)),
            is(LocalDateTime.of(2015, java.time.Month.APRIL, 30, 17, 45))
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.from(PlainTimestamp.axis().getMinimum()),
            is(LocalDateTime.MIN)
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.from(PlainTimestamp.axis().getMaximum()),
            is(LocalDateTime.MAX)
        );
    }

    @Test
    public void instantToTime4J() {
        Moment expected = Moment.of(86401, 123_456_789, TimeScale.POSIX);
        assertThat(
            TemporalType.INSTANT.translate(Instant.ofEpochSecond(86401, 123_456_789)),
            is(expected)
        );
    }

    @Test
    public void instantFromTime4J() {
        assertThat(
            TemporalType.INSTANT.from(Moment.of(86401, 123_456_789, TimeScale.POSIX)),
            is(Instant.ofEpochSecond(86401, 123_456_789))
        );
    }

    @Test
    public void instantFromTime4JLS() {
        if (LeapSeconds.getInstance().isEnabled()) {
            Moment ls = PlainDate.of(2012, 7, 1).atStartOfDay().atUTC().minus(1, SI.SECONDS);
            assertThat(
                TemporalType.INSTANT.from(ls),
                is(LocalDateTime.of(2012, 6, 30, 23, 59, 59).atOffset(ZoneOffset.UTC).toInstant()));
        }
    }

    @Test
    public void zdtToTime4J() {
        Timezone tz = Timezone.of("Europe/Berlin");
        Moment moment = PlainDate.of(2015, 3, 29).atTime(2, 30).in(tz);
        Instant instant = TemporalType.INSTANT.from(moment);
        assertThat(
            TemporalType.ZONED_DATE_TIME.translate(instant.atZone(ZoneId.of("Europe/Berlin"))),
            is(moment.inZonalView(tz.getID()))
        );
    }

    @Test
    public void zdtFromTime4J() {
        Timezone tz = Timezone.of("Europe/Berlin");
        Moment moment = PlainDate.of(2015, 3, 29).atTime(2, 30).in(tz);
        Instant instant = TemporalType.INSTANT.from(moment);
        assertThat(
            TemporalType.ZONED_DATE_TIME.from(moment.inZonalView(tz.getID())),
            is(instant.atZone(ZoneId.of("Europe/Berlin")))
        );
    }

    @Test
    public void durationToTime4J() {
        assertThat(
            TemporalType.THREETEN_DURATION.translate(java.time.Duration.ofSeconds(7265)),
            is(Duration.ofClockUnits(2, 1, 5))
        );
        assertThat(
            TemporalType.THREETEN_DURATION.translate(java.time.Duration.ofSeconds(-1).plusNanos(999_999_999)),
            is(Duration.of(-1, ClockUnit.NANOS))
        );
    }

    @Test
    public void durationFromTime4J() {
        assertThat(
            TemporalType.THREETEN_DURATION.from(Duration.ofClockUnits(2, 1, 5)),
            is(java.time.Duration.ofSeconds(7265))
        );
        assertThat(
            TemporalType.THREETEN_DURATION.from(Duration.of(-1, ClockUnit.NANOS)),
            is(java.time.Duration.ofSeconds(-1).plusNanos(999_999_999))
        );
        assertThat(
            TemporalType.THREETEN_DURATION.from(Duration.of(5, ClockUnit.SECONDS).plus(234500, ClockUnit.MICROS)),
            is(java.time.Duration.ofSeconds(5).plusNanos(234_500_000))
        );
    }

    @Test
    public void periodToTime4J1() {
        assertThat(
            TemporalType.THREETEN_PERIOD.translate(Period.of(3, 13, 45)),
            is(Duration.ofCalendarUnits(4, 1, 45))
        );
    }

    @Test(expected=ChronoException.class)
    public void periodToTime4J2() {
        TemporalType.THREETEN_PERIOD.translate(Period.of(0, 1, -30));
    }

    @Test
    public void periodFromTime4J1() {
        assertThat(
            TemporalType.THREETEN_PERIOD.from(Duration.ofCalendarUnits(3, 8, 45)),
            is(Period.of(3, 8, 45))
        );
    }

    @Test
    public void periodFromTime4J2() {
        assertThat(
            TemporalType.THREETEN_PERIOD.from(Duration.of(3, CalendarUnit.WEEKS).inverse()),
            is(Period.ofDays(-21))
        );
    }

    @Test
    public void clockToTime4J() {
        TimeSource<?> expected = () -> PlainTimestamp.of(2012, 6, 30, 23, 59, 59).atUTC();

        assertThat(
            TemporalType.CLOCK.translate(
                Clock.fixed(
                    LocalDateTime.of(2012, 6, 30, 23, 59, 59).atOffset(ZoneOffset.UTC).toInstant(),
                    ZoneId.systemDefault())
            ).currentTime(),
            is(expected.currentTime())
        );
    }

    @Test
    public void clockFromTime4J() {
        Moment moment = PlainTimestamp.of(2012, 6, 30, 23, 59, 59).atUTC();
        if (LeapSeconds.getInstance().isEnabled()) {
            moment = moment.plus(1, SI.SECONDS);
        }
        Moment currentTime = moment;
        TimeSource<?> time4j = () -> currentTime;
        assertThat(
            TemporalType.CLOCK.from(time4j).instant(),
            is(
                Clock.fixed(
                    LocalDateTime.of(2012, 6, 30, 23, 59, 59).atOffset(ZoneOffset.UTC).toInstant(),
                    ZoneId.systemDefault()
                ).instant())
        );
    }

    @Test
    public void durationFromTemporalAmount1() {
        Period period = Period.of(0, 1, 1);
        Duration<CalendarUnit> duration = Duration.ofCalendarUnits(0, 1, 1);
        assertThat(Duration.from(period), is(duration));
        LocalDate threeten = LocalDate.of(2015, 7, 1).minus(period);
        assertThat(threeten, is(LocalDate.of(2015, 5, 31)));
        PlainDate time4j = PlainDate.of(2015, 7, 1).minus(duration);
        assertThat(time4j, is(PlainDate.of(2015, 5, 30)));
    }

    @Test
    public void durationFromTemporalAmount2() {
        TemporalAmount ta = new CustomTemporalAmount();
        Duration<IsoUnit> duration = Duration.from(ta);
        LocalDateTime ldt = LocalDateTime.of(2015, 5, 12, 17, 45);
        PlainTimestamp tsp = PlainTimestamp.from(ldt);
        assertThat(
            duration,
            is(Duration.compose(
                Duration.of(1, CalendarUnit.DAYS),
                Duration.of(12, ClockUnit.HOURS)))
        );
        assertThat(tsp.plus(duration).toTemporalAccessor(), is(ldt.plus(ta)));
    }

    @Test
    public void displayTemporalAmount1() {
        assertThat(
            Duration.formatter("[DD:]hh:mm").format(java.time.Duration.ofMinutes(1565)),
            is("01:02:05")
        );
    }

    @Test
    public void displayTemporalAmount2() {
        assertThat(
            Duration.formatter("'P'YYYY'-'MM'-'DD").format(java.time.Period.of(4, 13, 35)),
            is("P0005-01-35")
        );
    }

    @Test
    public void displayTemporalAmount3() {
        assertThat(
            Duration.formatter("[DD:]hh").format(new CustomTemporalAmount()),
            is("01:12")
        );
    }

    private static class CustomTemporalAmount
        implements TemporalAmount {

        @Override
        public long get(TemporalUnit unit) {
            if (unit.equals(ChronoUnit.DAYS)) {
                return 1;
            } else if (unit.equals(ChronoUnit.HALF_DAYS)) {
                return 1;
            }
            return 0;
        }

        @Override
        public List<TemporalUnit> getUnits() {
            return Arrays.asList(ChronoUnit.DAYS, ChronoUnit.HALF_DAYS);
        }

        @Override
        public Temporal addTo(Temporal temporal) {
            LocalDateTime ldt = (LocalDateTime) temporal;
            return ldt.plus(1, ChronoUnit.DAYS).plus(1, ChronoUnit.HALF_DAYS);
        }

        @Override
        public Temporal subtractFrom(Temporal temporal) {
            LocalDateTime ldt = (LocalDateTime) temporal;
            return ldt.minus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.HALF_DAYS);
        }
    }

}