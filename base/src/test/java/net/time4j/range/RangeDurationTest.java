package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.Duration;
import net.time4j.IsoUnit;
import net.time4j.MachineTime;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.SI;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;

import java.util.concurrent.TimeUnit;

import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RangeDurationTest {

    @Test
    public void lengthInDaysOfDateInterval() {
        DateInterval interval =
            DateInterval.between(
                PlainDate.of(2012, 1, 1),
                PlainDate.of(2012, 3, 31));
        assertThat(interval.getLengthInDays(), is(91L));
        assertThat(interval.withOpenEnd().getLengthInDays(), is(90L));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void lengthInDaysOfInfiniteDateInterval() {
        DateInterval interval = DateInterval.since(PlainDate.of(2012, 1, 1));
        interval.getLengthInDays();
    }

    @Test
    public void durationInYearsMonthsDaysOfDateInterval() {
        DateInterval interval =
            DateInterval.between(
                PlainDate.of(2012, 1, 1),
                PlainDate.of(2012, 3, 31));
        assertThat(
            interval.getDurationInYearsMonthsDays(),
            is(Duration.ofCalendarUnits(0, 3, 0)));
        assertThat(
            interval.withOpenEnd().getDurationInYearsMonthsDays(),
            is(Duration.ofCalendarUnits(0, 2, 30)));
    }

    @Test
    public void durationInUnitsOfDateInterval() {
        DateInterval interval =
            DateInterval.between(
                PlainDate.of(2012, 1, 1),
                PlainDate.of(2012, 3, 31));
        CalendarUnit[] units = {CalendarUnit.WEEKS, CalendarUnit.DAYS};
        assertThat(
            interval.getDuration(units),
            is(Duration.of(13, CalendarUnit.WEEKS)));
        assertThat(
            interval.withOpenEnd().getDuration(units),
            is(Duration.of(12, CalendarUnit.WEEKS).plus(6, CalendarUnit.DAYS)));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void durationOfInfiniteDateInterval() {
        DateInterval interval = DateInterval.since(PlainDate.of(2012, 1, 1));
        interval.getDurationInYearsMonthsDays();
    }

    @Test
    public void durationFromMidnightToMidnightExclusive() {
        assertThat(
            ClockInterval.FULL_DAY.getDuration(),
            is(Duration.of(24, ClockUnit.HOURS)));
    }

    @Test
    public void durationFromMidnightToMidnightInclusive() {
        ClockInterval interval =
            ClockInterval.between(
                PlainTime.midnightAtStartOfDay(),
                PlainTime.midnightAtEndOfDay()
            ).withClosedEnd();
        assertThat(
            interval.getDuration(),
            is(Duration.of(24, ClockUnit.HOURS).plus(1, ClockUnit.NANOS)));
    }

    @Test
    public void durationOfAtomicClockInterval() {
        ClockInterval interval =
            ClockInterval.between(
                PlainTime.midnightAtEndOfDay(),
                PlainTime.midnightAtEndOfDay()
            ).withClosedEnd();
        assertThat(
            interval.getDuration(),
            is(Duration.of(1, ClockUnit.NANOS)));
    }

    @Test
    public void durationOfEmptyClockInterval() {
        ClockInterval interval =
            ClockInterval.between(
                PlainTime.midnightAtEndOfDay(),
                PlainTime.midnightAtEndOfDay()
            );
        Duration<ClockUnit> expected = Duration.ofZero();
        assertThat(interval.getDuration(), is(expected));
    }

    @Test
    public void durationOfClockInterval() {
        ClockInterval interval =
            ClockInterval.between(
                PlainTime.of(7, 45),
                PlainTime.of(21, 0)
            );
        assertThat(
            interval.getDuration(),
            is(Duration.ofClockUnits(13, 15, 0)));
    }

    @Test
    public void durationOfTimestampInterval() {
        TimestampInterval interval =
            TimestampInterval.between(
                PlainTimestamp.of(2013, 12, 31, 21, 45),
                PlainTimestamp.of(2014, 3, 4, 7, 0)
            );
        IsoUnit[] units = {
            CalendarUnit.MONTHS, CalendarUnit.DAYS,
            ClockUnit.HOURS, ClockUnit.MINUTES};
        Duration<?> p = Duration.ofPositive().months(2).days(3).hours(9).minutes(15).build();
        Duration<?> r = Duration.ofPositive().months(1).days(31).hours(9).minutes(15).build();
        assertThat(
            interval.getDuration(units),
            is(p));
        assertThat(interval.getStartAsTimestamp().plus(p), is(interval.getEndAsTimestamp()));
        assertThat(interval.getEndAsTimestamp().minus(p), not(interval.getStartAsTimestamp()));
        assertThat(interval.getEndAsTimestamp().minus(r), is(interval.getStartAsTimestamp()));
    }

    @Test
    public void durationOfTimestampIntervalWithZonalCorrection() {
        TimestampInterval interval =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 1, 1, 21, 45),
                PlainTimestamp.of(2014, 3, 31, 7, 0)
            );
        IsoUnit[] units = {
            CalendarUnit.MONTHS, CalendarUnit.DAYS,
            ClockUnit.HOURS, ClockUnit.MINUTES};
        assertThat(
            interval.getDuration(Timezone.of("Europe/Berlin"), units),
            is(
                Duration.ofPositive().months(2).days(29)
                .hours(8).minutes(15).build()));
    }

    @Test
    public void getNominalDurationOfMomentInterval() {
        Moment m1 = Moment.of(1278028823, TimeScale.UTC);
        Moment m2 = Moment.of(1278028826, 1, TimeScale.UTC);
        Duration<?> duration =
            MomentInterval.between(m1, m2).getNominalDuration(Timezone.of(ZonalOffset.UTC), ClockUnit.SECONDS);
        Duration<?> expected = Duration.of(2, ClockUnit.SECONDS);
        assertThat(duration, is(expected));
    }

    @Test
	public void getRealDurationOfMomentInterval() {
	    Moment m1 = Moment.of(1278028823, TimeScale.UTC);
	    Moment m2 = Moment.of(1278028826, 1, TimeScale.UTC);
	    MachineTime<SI> duration =
            MomentInterval.between(m1, m2).getRealDuration();
	    MachineTime<SI> expected = MachineTime.ofSIUnits(3L, 1);
	    assertThat(duration, is(expected));
	}

    @Test
	public void getSimpleDurationOfMomentInterval() {
	    Moment m1 = Moment.of(1278028823, TimeScale.UTC);
	    Moment m2 = Moment.of(1278028826, 1, TimeScale.UTC);
	    MachineTime<TimeUnit> duration =
            MomentInterval.between(m1, m2).getSimpleDuration();
	    MachineTime<TimeUnit> expected = MachineTime.ofPosixUnits(2L, 1);
	    assertThat(duration, is(expected));
	}

}
