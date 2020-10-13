package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HebrewOperatorTest {

    @Test
    public void nextMonth() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 29).with(HebrewCalendar.MONTH_OF_YEAR.incremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.KISLEV, 29)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.KISLEV, 30).with(HebrewCalendar.MONTH_OF_YEAR.decremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 29)));
    }

    @Test
    public void nextYear() {
        assertThat(
            HebrewCalendar.of(5780, HebrewMonth.KISLEV, 30).with(HebrewCalendar.YEAR_OF_ERA.incremented()),
            is(HebrewCalendar.of(5781, HebrewMonth.KISLEV, 29)));
    }

    @Test
    public void previousYear() {
        assertThat(
            HebrewCalendar.of(5779, HebrewMonth.KISLEV, 29).with(HebrewCalendar.YEAR_OF_ERA.decremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.KISLEV, 29)));
    }

    @Test
    public void nextDay() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.AV, 30).with(HebrewCalendar.DAY_OF_YEAR.incremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.ELUL, 1)));
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.AV, 30).with(HebrewCalendar.DAY_OF_MONTH.incremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.ELUL, 1)));
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.AV, 30).with(HebrewCalendar.DAY_OF_WEEK.incremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.ELUL, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.ELUL, 1).with(HebrewCalendar.DAY_OF_YEAR.decremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.AV, 30)));
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.ELUL, 1).with(HebrewCalendar.DAY_OF_MONTH.decremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.AV, 30)));
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.ELUL, 1).with(HebrewCalendar.DAY_OF_WEEK.decremented()),
            is(HebrewCalendar.of(5778, HebrewMonth.AV, 30)));
    }

    @Test
    public void minMonth() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 21).with(HebrewCalendar.MONTH_OF_YEAR.minimized()),
            is(HebrewCalendar.of(5778, HebrewMonth.TISHRI, 21)));
    }

    @Test
    public void maxMonth() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 21).with(HebrewCalendar.MONTH_OF_YEAR.maximized()),
            is(HebrewCalendar.of(5778, HebrewMonth.ELUL, 21)));
    }

    @Test
    public void maxDay() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 21).with(HebrewCalendar.DAY_OF_MONTH.maximized()),
            is(HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 29)));
        assertThat(
            HebrewCalendar.of(5779, HebrewMonth.HESHVAN, 21).with(HebrewCalendar.DAY_OF_MONTH.maximized()),
            is(HebrewCalendar.of(5779, HebrewMonth.HESHVAN, 30)));
    }

    @Test
    public void minDay() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 21).with(HebrewCalendar.DAY_OF_MONTH.minimized()),
            is(HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.SHEVAT, 2).with(HebrewCalendar.YEAR_OF_ERA.atFloor()),
            is(HebrewCalendar.of(5778, HebrewMonth.TISHRI, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.SHEVAT, 2).with(HebrewCalendar.YEAR_OF_ERA.atCeiling()),
            is(HebrewCalendar.of(5778, HebrewMonth.ELUL, 29)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.KISLEV, 2).with(HebrewCalendar.MONTH_OF_YEAR.atFloor()),
            is(HebrewCalendar.of(5778, HebrewMonth.KISLEV, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.KISLEV, 2).with(HebrewCalendar.MONTH_OF_YEAR.atCeiling()),
            is(HebrewCalendar.of(5778, HebrewMonth.KISLEV, 30)));
    }

    @Test
    public void nextHour() {
        assertThat(
            HebrewTime.ofNight(11, 0).with(HebrewTime.CLOCK_HOUR.incremented()),
            is(HebrewTime.ofDay(12, 0)));
        assertThat(
            HebrewTime.ofNight(12, 0).with(HebrewTime.CLOCK_HOUR.incremented()),
            is(HebrewTime.ofNight(1, 0)));
        assertThat(
            HebrewTime.ofDay(11, 0).with(HebrewTime.DIGITAL_HOUR.incremented()),
            is(HebrewTime.ofNight(12, 0)));
    }

    @Test
    public void previousHour() {
        assertThat(
            HebrewTime.ofDay(12, 0).with(HebrewTime.CLOCK_HOUR.decremented()),
            is(HebrewTime.ofNight(11, 0)));
        assertThat(
            HebrewTime.ofNight(1, 0).with(HebrewTime.CLOCK_HOUR.decremented()),
            is(HebrewTime.ofNight(12, 0)));
        assertThat(
            HebrewTime.ofNight(12, 0).with(HebrewTime.DIGITAL_HOUR.decremented()),
            is(HebrewTime.ofDay(11, 0)));
    }

    @Test
    public void nextPart() {
        assertThat(
            HebrewTime.ofNight(11, 1079).with(HebrewTime.PART_OF_HOUR.incremented()),
            is(HebrewTime.ofDay(12, 0)));
    }

    @Test
    public void previousPart() {
        assertThat(
            HebrewTime.ofDay(12, 0).with(HebrewTime.PART_OF_HOUR.decremented()),
            is(HebrewTime.ofNight(11, 1079)));
    }

    @Test
    public void minHour() {
        assertThat(
            HebrewTime.ofDay(11, 0).with(HebrewTime.CLOCK_HOUR.minimized()),
            is(HebrewTime.ofDay(1, 0)));
        assertThat(
            HebrewTime.ofDay(11, 0).with(HebrewTime.DIGITAL_HOUR.minimized()),
            is(HebrewTime.ofNight(12, 0)));
    }

    @Test
    public void maxHour() {
        assertThat(
            HebrewTime.ofNight(5, 0).with(HebrewTime.CLOCK_HOUR.maximized()),
            is(HebrewTime.ofNight(12, 0)));
        assertThat(
            HebrewTime.ofNight(5, 0).with(HebrewTime.DIGITAL_HOUR.maximized()),
            is(HebrewTime.ofDay(11, 0)));
    }

    @Test
    public void minPart() {
        assertThat(
            HebrewTime.ofDay(12, 1000).with(HebrewTime.PART_OF_HOUR.minimized()),
            is(HebrewTime.ofDay(12, 0)));
    }

    @Test
    public void maxPart() {
        assertThat(
            HebrewTime.ofDay(12, 1000).with(HebrewTime.PART_OF_HOUR.maximized()),
            is(HebrewTime.ofDay(12, 1079)));
    }

    @Test
    public void hourAtFloor() {
        assertThat(
            HebrewTime.ofDay(4, 1000).with(HebrewTime.CLOCK_HOUR.atFloor()),
            is(HebrewTime.ofDay(4, 0)));
        assertThat(
            HebrewTime.ofDay(4, 1000).with(HebrewTime.DIGITAL_HOUR.atFloor()),
            is(HebrewTime.ofDay(4, 0)));
    }

    @Test
    public void hourAtCeiling() {
        assertThat(
            HebrewTime.ofDay(4, 0).with(HebrewTime.CLOCK_HOUR.atCeiling()),
            is(HebrewTime.ofDay(4, 1079)));
        assertThat(
            HebrewTime.ofDay(4, 0).with(HebrewTime.DIGITAL_HOUR.atCeiling()),
            is(HebrewTime.ofDay(4, 1079)));
    }

    @Test
    public void withStartOfYear() {
        HebrewCalendar hc = HebrewCalendar.axis().getMaximum();
        HebrewCalendar expected = HebrewCalendar.of(9999, HebrewMonth.TISHRI, 1);

        assertThat(hc.with(HebrewCalendar.DAY_OF_YEAR.minimized()), is(expected));
        assertThat(hc.with(HebrewCalendar.DAY_OF_YEAR, 1), is(expected));
        assertThat(hc.getDayOfYear(), is(353));
        assertThat(hc.getDayOfYear(), is(hc.lengthOfYear()));
        assertThat(expected.getDayOfYear(), is(1));
        assertThat(hc.getMinimum(HebrewCalendar.DAY_OF_YEAR), is(1));
        assertThat(expected.getMaximum(HebrewCalendar.DAY_OF_YEAR), is(353));
    }

}