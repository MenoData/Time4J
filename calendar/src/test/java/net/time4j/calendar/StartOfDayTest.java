package net.time4j.calendar;

import net.time4j.GeneralTimestamp;
import net.time4j.Moment;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.engine.StartOfDay;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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

}