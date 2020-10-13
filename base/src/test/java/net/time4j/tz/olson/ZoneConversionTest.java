package net.time4j.tz.olson;

import net.time4j.PlainTimestamp;
import net.time4j.TemporalType;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.model.TransitionModel;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;


public class ZoneConversionTest {

    @Test
    public void javaUtilTimeZoneToTime4J() {
        java.util.TimeZone jut = java.util.TimeZone.getTimeZone("Europe/Berlin");
        assertThat(
            TemporalType.JAVA_UTIL_TIMEZONE.translate(jut),
            is(Timezone.of("java.util.TimeZone~Europe/Berlin")));
        assertThat(
            TemporalType.JAVA_UTIL_TIMEZONE.translate(jut),
            not(Timezone.of("Europe/Berlin")));
    }

    @Test
    public void javaUtilTimeZoneFromTime4J() {
        java.util.TimeZone jut = java.util.TimeZone.getTimeZone("Europe/Berlin");
        java.util.TimeZone wrapped = TemporalType.JAVA_UTIL_TIMEZONE.from(Timezone.of("Europe/Berlin"));
        assertThat(wrapped, not(jut));

        long now = System.currentTimeMillis();
        assertThat(wrapped.getOffset(now), is(jut.getOffset(now)));
        assertThat(((java.util.TimeZone) wrapped.clone()).getOffset(now), is(jut.getOffset(now)));

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeZone(wrapped); // use data and rules of Time4J
        gcal.set(Calendar.YEAR, 2017);
        gcal.set(Calendar.MONTH, Calendar.OCTOBER);
        gcal.set(Calendar.DAY_OF_MONTH, 29);
        gcal.set(Calendar.HOUR_OF_DAY, 2);
        gcal.set(Calendar.MINUTE, 30);
        gcal.set(Calendar.SECOND, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.translate(gcal.getTime()), // resolves ambivalent time to later offset
            is(PlainTimestamp.of(2017, 10, 29, 2, 30).inTimezone(EUROPE.BERLIN)));
    }

    @Test
    public void keepingZoneRules() {
        ZonalTransition first =
            new ZonalTransition(7L, 1800, 7200, 3600);
        ZonalTransition second =
            new ZonalTransition(365 * 86400L, 7200, 7200, 0);
        TransitionHistory customHistory =
            TransitionModel.of(Arrays.asList(first, second));
        Timezone time4j = Timezone.of("customized-tz", customHistory);
        java.util.TimeZone wrapped = TemporalType.JAVA_UTIL_TIMEZONE.from(time4j);
        assertThat(
            TemporalType.JAVA_UTIL_TIMEZONE.translate(wrapped),
            is(time4j));
        assertThat(
            wrapped.getID(),
            is("customized-tz"));
        assertThat(
            wrapped.getOffset(6999L),
            is(1_800_000));
        assertThat(
            wrapped.getOffset(7000L),
            is(7_200_000));
        assertThat(
            wrapped.getOffset(365 * 86_400_000L - 1),
            is(7_200_000));
        assertThat(
            wrapped.getOffset(365 * 86_400_000L),
            is(7_200_000));
        assertThat(
            wrapped.getRawOffset(),
            is(7_200_000));
        assertThat(
            wrapped.getDSTSavings(),
            is(3_600_000));
    }

}
