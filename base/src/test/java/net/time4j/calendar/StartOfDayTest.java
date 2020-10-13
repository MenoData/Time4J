package net.time4j.calendar;

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.StartOfDay;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class StartOfDayTest {

    @Test
    public void startAtMidnight() {
        Moment moment = PlainTimestamp.of(2015, 7, 17, 18, 1).atUTC();
        HijriCalendar hijri = HijriCalendar.ofUmalqura(1436, 10, 1);

        assertThat(
            hijri.atTime(18, 1).at(ZonalOffset.UTC, StartOfDay.MIDNIGHT),
            is(moment));
        assertThat(
            moment.toGeneralTimestamp(
                HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, ZonalOffset.UTC, StartOfDay.MIDNIGHT),
            is(hijri.atTime(18, 1)));
    }

    @Test
    public void startInEvening() {
        Moment moment = PlainTimestamp.of(2015, 7, 17, 18, 1).atUTC();
        HijriCalendar hijri = HijriCalendar.ofUmalqura(1436, 10, 2);

        assertThat(
            hijri.atTime(18, 1).at(ZonalOffset.UTC, StartOfDay.EVENING),
            is(moment));
        GeneralTimestamp<HijriCalendar> tsp =
            moment.toGeneralTimestamp(
                HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, ZonalOffset.UTC, StartOfDay.EVENING);
        assertThat(tsp.toDate(), is(hijri));
        assertThat(tsp.toTime(), is(PlainTime.of(18, 1)));
    }

    @Test
    public void startAtSunsetInMecca() {
        Moment moment = PlainTimestamp.of(2015, 7, 17, 16, 9).atUTC();
        HijriCalendar hijri = HijriCalendar.ofUmalqura(1436, 10, 2);
        ZonalOffset saudiArabia = ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 3);
        StartOfDay startOfDay = StartOfDay.definedBy(SolarTime.ofMecca().sunset());

        // after sunset (2015-07-17T16:08:13Z)
        assertThat(
            hijri.atTime(19, 9).at(saudiArabia, startOfDay),
            is(moment));
        GeneralTimestamp<HijriCalendar> tsp =
            moment.toGeneralTimestamp(
                HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, saudiArabia, startOfDay);
        assertThat(tsp.toDate(), is(hijri));
        assertThat(tsp.toTime(), is(PlainTime.of(19, 9)));

        // before sunset (2015-07-17T16:08:13Z)
        hijri = hijri.minus(CalendarDays.ONE);
        moment = moment.minus(1, TimeUnit.MINUTES);
        assertThat(
            hijri.atTime(19, 8).at(saudiArabia, startOfDay),
            is(moment));
        tsp =
            moment.toGeneralTimestamp(
                HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, saudiArabia, startOfDay);
        assertThat(tsp.toDate(), is(hijri));
        assertThat(tsp.toTime(), is(PlainTime.of(19, 8)));

        // after sunset on next day (2015-07-18T16:07:59Z)
        moment = PlainTimestamp.of(2015, 7, 18, 16, 8).atUTC();
        hijri = HijriCalendar.ofUmalqura(1436, 10, 3);
        assertThat(
            hijri.atTime(19, 8).at(saudiArabia, startOfDay),
            is(moment));
        tsp =
            moment.toGeneralTimestamp(
                HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, saudiArabia, startOfDay);
        assertThat(tsp.toDate(), is(hijri));
        assertThat(tsp.toTime(), is(PlainTime.of(19, 8)));

        // before sunset on next day (2015-07-18T16:07:59Z)
        hijri = hijri.minus(CalendarDays.ONE);
        moment = moment.minus(1, TimeUnit.MINUTES);
        assertThat(
            hijri.atTime(19, 7).at(saudiArabia, startOfDay),
            is(moment));
        tsp =
            moment.toGeneralTimestamp(
                HijriCalendar.family(), HijriCalendar.VARIANT_UMALQURA, saudiArabia, startOfDay);
        assertThat(tsp.toDate(), is(hijri));
        assertThat(tsp.toTime(), is(PlainTime.of(19, 7)));
    }

}