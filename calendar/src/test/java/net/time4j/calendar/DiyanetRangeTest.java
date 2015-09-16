package net.time4j.calendar;

import net.time4j.engine.CalendarDays;
import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DiyanetRangeTest {

    @Test
    public void minimum() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1318, 1, 1);
        assertThat(
            HijriCalendar.family().getCalendarSystem(hijri.getVariant()).getMinimumSinceUTC(),
            is(hijri.getDaysSinceEpochUTC()));
    }

    @Test
    public void maximum() {
        HijriCalendar hijri = HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1444, 5, 29);
        assertThat(
            HijriCalendar.family().getCalendarSystem(hijri.getVariant()).getMaximumSinceUTC(),
            is(hijri.getDaysSinceEpochUTC()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void beyondMaximum() {
        HijriCalendar.of(HijriCalendar.VARIANT_DIYANET, 1444, 5, 29).plus(CalendarDays.ONE);
    }

}