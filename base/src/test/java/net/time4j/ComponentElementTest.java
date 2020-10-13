package net.time4j;

import net.time4j.engine.RuleNotFoundException;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.tz.ZonalOffset;

import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ComponentElementTest {

    @Test
    public void dateComponent() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 21, 14, 30);
        tsp = tsp.with(PlainDate.COMPONENT, PlainDate.of(2015, 1, 1));
        assertThat(tsp, is(PlainTimestamp.of(2015, 1, 1, 14, 30)));
    }

    @Test
    public void timeComponent1() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 21, 14, 30);
        tsp = tsp.with(PlainTime.COMPONENT, PlainTime.of(23, 59));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 21, 23, 59)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void timeComponent2() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 21, 14, 30);
        tsp.with(PlainTime.COMPONENT, PlainTime.midnightAtEndOfDay());
    }

    @Test(expected=RuleNotFoundException.class)
    public void dateFromMoment() {
        Moment moment = Moment.UNIX_EPOCH;
        moment.get(PlainDate.COMPONENT);
    }

    @Test(expected=RuleNotFoundException.class)
    public void timeFromMoment() {
        Moment moment = Moment.UNIX_EPOCH;
        moment.get(PlainTime.COMPONENT);
    }

    @Test
    public void setToNextTimeOnSameDay() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp = tsp.with(PlainTime.COMPONENT.setToNext(PlainTime.of(21, 45)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 19, 21, 45)));
    }

    @Test
    public void setToNextTimeOnNextDay1() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp = tsp.with(PlainTime.COMPONENT.setToNext(PlainTime.of(13, 45)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 20, 13, 45)));
    }

    @Test
    public void setToNextTimeOnNextDay2() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp = tsp.with(PlainTime.COMPONENT.setToNext(PlainTime.of(14, 30)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 20, 14, 30)));
    }

    @Test
    public void setToNextOrSameTimeOnSameDay() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(PlainTime.COMPONENT.setToNextOrSame(PlainTime.of(14, 30)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 19, 14, 30)));
    }

    @Test
    public void setToNextOrSameTimeOnNextDay() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(PlainTime.COMPONENT.setToNextOrSame(PlainTime.of(13, 45)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 20, 13, 45)));
    }

    @Test
    public void setToNextTime2400() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(
                PlainTime.COMPONENT.setToNext(PlainTime.midnightAtEndOfDay()));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 20, 0, 0)));
    }

    @Test
    public void setToPreviousOrSameTimeOnSameDay() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(
                PlainTime.COMPONENT.setToPreviousOrSame(PlainTime.of(14, 30)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 19, 14, 30)));
        tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(
                PlainTime.COMPONENT.setToPreviousOrSame(PlainTime.of(12, 30)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 19, 12, 30)));
    }

    @Test
    public void setToPreviousOrSameTimeOnPreviousDay() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(
                PlainTime.COMPONENT.setToPreviousOrSame(PlainTime.of(16, 45)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 18, 16, 45)));
    }

    @Test
    public void setToPreviousTimeOnPreviousDay() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 8, 19, 14, 30);
        tsp =
            tsp.with(
                PlainTime.COMPONENT.setToPrevious(PlainTime.of(14, 30)));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 18, 14, 30)));
    }

    @Test
    public void setToNextTimeOnMoment() {
        Moment tsp = PlainTimestamp.of(2014, 8, 19, 14, 30).atUTC();
        tsp =
            tsp.with(
                PlainTime.COMPONENT
                .setToNext(PlainTime.of(21, 45))
                .inTimezone(ZonalOffset.UTC));
        assertThat(tsp, is(PlainTimestamp.of(2014, 8, 19, 21, 45).atUTC()));
    }

    @Test
    public void roundedToFullHourOnTime2329() {
        PlainTime time = PlainTime.of(23, 29, 59, 999999999);
        assertThat(
            time.with(PlainTime.COMPONENT.roundedToFullHour()),
            is(PlainTime.of(23)));
    }

    @Test
    public void roundedToFullHourOnTime2330() {
        PlainTime time = PlainTime.of(23, 30);
        assertThat(
            time.with(PlainTime.COMPONENT.roundedToFullHour()),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void roundedToFullMinuteOnTime235929() {
        PlainTime time = PlainTime.of(23, 59, 29, 999999999);
        assertThat(
            time.with(PlainTime.COMPONENT.roundedToFullMinute()),
            is(PlainTime.of(23, 59)));
    }

    @Test
    public void roundedToFullMinuteOnTime235930() {
        PlainTime time = PlainTime.of(23, 59, 30);
        assertThat(
            time.with(PlainTime.COMPONENT.roundedToFullMinute()),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void roundedToFullHourOnTime24() {
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            time.with(PlainTime.COMPONENT.roundedToFullHour()),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void roundedToFullMinuteOnTime24() {
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            time.with(PlainTime.COMPONENT.roundedToFullMinute()),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void roundedToFullHourOnTimestamp2329() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 31, 23, 29, 59);
        assertThat(
            ts.with(PlainTime.COMPONENT.roundedToFullHour()),
            is(PlainTimestamp.of(2014, 12, 31, 23, 0)));
    }

    @Test
    public void roundedToFullHourOnTimestamp2330() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 31, 23, 30);
        assertThat(
            ts.with(PlainTime.COMPONENT.roundedToFullHour()),
            is(PlainTimestamp.of(2015, 1, 1, 0, 0)));
    }

    @Test
    public void roundedToFullMinuteOnTimestamp235929() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 31, 23, 59, 29);
        assertThat(
            ts.with(PlainTime.COMPONENT.roundedToFullMinute()),
            is(PlainTimestamp.of(2014, 12, 31, 23, 59)));
    }

    @Test
    public void roundedToFullMinuteOnTimestamp235930() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 31, 23, 59, 30);
        assertThat(
            ts.with(PlainTime.COMPONENT.roundedToFullMinute()),
            is(PlainTimestamp.of(2015, 1, 1, 0, 0)));
    }

    @Test
    public void setToNextFullHourOnTime2329() {
        PlainTime time = PlainTime.of(23, 29, 59, 999999999);
        assertThat(
            time.with(PlainTime.COMPONENT.setToNextFullHour()),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void setToNextFullMinuteOnTime235929() {
        PlainTime time = PlainTime.of(23, 59, 29, 999999999);
        assertThat(
            time.with(PlainTime.COMPONENT.setToNextFullMinute()),
            is(PlainTime.midnightAtEndOfDay()));
    }

    @Test
    public void setToNextFullHourOnTime24() {
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            time.with(PlainTime.COMPONENT.setToNextFullHour()),
            is(PlainTime.of(1)));
    }

    @Test
    public void setToNextFullMinuteOnTime24() {
        PlainTime time = PlainTime.midnightAtEndOfDay();
        assertThat(
            time.with(PlainTime.COMPONENT.setToNextFullMinute()),
            is(PlainTime.of(0, 1)));
    }

    @Test
    public void setToNextFullHourOnTimestamp2329() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 31, 23, 1, 0);
        assertThat(
            ts.with(PlainTime.COMPONENT.setToNextFullHour()),
            is(PlainTimestamp.of(2015, 1, 1, 0, 0)));
    }

    @Test
    public void setToNextFullMinuteOnTimestamp235929() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 31, 23, 59, 1);
        assertThat(
            ts.with(PlainTime.COMPONENT.setToNextFullMinute()),
            is(PlainTimestamp.of(2015, 1, 1, 0, 0)));
    }

    @Test
    public void setToFirstDayOfNextMonth() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 12, 27, 23, 59, 1);
        assertThat(
            ts.with(PlainDate.COMPONENT.firstDayOfNextMonth()),
            is(PlainTimestamp.of(2015, 1, 1, 23, 59, 1)));

    }

    @Test
    public void setToFirstDayOfNextQuarter() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 11, 27, 23, 59, 1);
        assertThat(
            ts.with(PlainDate.COMPONENT.firstDayOfNextQuarter()),
            is(PlainTimestamp.of(2015, 1, 1, 23, 59, 1)));

    }

    @Test
    public void setToFirstDayOfNextYear() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 3, 27, 23, 59, 1);
        assertThat(
            ts.with(PlainDate.COMPONENT.firstDayOfNextYear()),
            is(PlainTimestamp.of(2015, 1, 1, 23, 59, 1)));

    }

    @Test
    public void setToLastDayOfPreviousMonth() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 3, 27, 23, 59, 1);
        assertThat(
            ts.with(PlainDate.COMPONENT.lastDayOfPreviousMonth()),
            is(PlainTimestamp.of(2014, 2, 28, 23, 59, 1)));

    }

    @Test
    public void setToLastDayOfPreviousQuarter() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 11, 27, 23, 59, 1);
        assertThat(
            ts.with(PlainDate.COMPONENT.lastDayOfPreviousQuarter()),
            is(PlainTimestamp.of(2014, 9, 30, 23, 59, 1)));

    }

    @Test
    public void setToLastDayOfPreviousYear() {
        PlainTimestamp ts = PlainTimestamp.of(2014, 3, 27, 23, 59, 1);
        assertThat(
            ts.with(PlainDate.COMPONENT.lastDayOfPreviousYear()),
            is(PlainTimestamp.of(2013, 12, 31, 23, 59, 1)));

    }

    @Test
    public void combinedFormat() throws ParseException {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.setUp(PlainTimestamp.axis(), Locale.ROOT)
                .addCustomized(PlainDate.CALENDAR_DATE, Iso8601Format.BASIC_CALENDAR_DATE)
                .addLiteral('-')
                .addCustomized(PlainTime.WALL_TIME, Iso8601Format.EXTENDED_WALL_TIME)
                .build();
        assertThat(
            f.parse("20180302-17:45:21"),
            is(PlainTimestamp.of(2018, 3, 2, 17, 45, 21))
        ); // see also: JDK-issue 8199412
    }

}