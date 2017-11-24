package net.time4j;

import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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
            is((Calendar) cal));
    }

}
