package net.time4j.range;

import net.time4j.Moment;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.Date;

import net.time4j.Quarter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    @Test
    public void roundtripOfDateIntervalClosed()
        throws IOException, ClassNotFoundException {

        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        Object interval = DateInterval.between(start, end);
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundtripOfDateIntervalHalfOpen()
        throws IOException, ClassNotFoundException {

        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        Object interval = DateInterval.between(start, end).withOpenEnd();
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundtripOfDateIntervalSince()
        throws IOException, ClassNotFoundException {

        PlainDate start = PlainDate.of(2014, 2, 27);
        Object interval = DateInterval.since(start);
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundtripOfDateIntervalUntil()
        throws IOException, ClassNotFoundException {

        PlainDate end = PlainDate.of(2014, 2, 27);
        Object interval = DateInterval.until(end);
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundtripOfClockInterval()
        throws IOException, ClassNotFoundException {

        PlainTime start = PlainTime.of(7, 0, 45);
        PlainTime end = PlainTime.of(23, 15, 30);
        Object interval = ClockInterval.between(start, end);
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundtripOfTimestampInterval()
        throws IOException, ClassNotFoundException {

        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 3, 20);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 15, 0);
        Object interval = TimestampInterval.between(start, end);
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundTripOfMomentInterval()
        throws IOException, ClassNotFoundException {

        Moment start = PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC();
        Moment end = PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC();
        Object interval = MomentInterval.between(start, end);
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundTripOfDateIntervalCollection()
        throws IOException, ClassNotFoundException {

        DateInterval i1 =
            DateInterval.between(
                PlainDate.of(2014, 2, 28),
                PlainDate.of(2014, 5, 31));
        DateInterval i2 =
            DateInterval.between(
                PlainDate.of(2014, 4, 1),
                PlainDate.of(2014, 4, 15));
        DateInterval i3 =
            DateInterval.between(
                PlainDate.of(2014, 4, 10),
                PlainDate.of(2014, 6, 1));
        IntervalCollection<PlainDate> windows = IntervalCollection.onDateAxis();
        windows = windows.plus(i1).plus(i2).plus(i3);

        Object ser = roundtrip(windows);
        assertThat(windows, is(ser));
    }

    @Test
    public void roundTripOfClockIntervalCollection()
        throws IOException, ClassNotFoundException {

        ClockInterval i1 =
            ClockInterval.between(
                PlainTime.of(20, 45, 30),
                PlainTime.of(24));
        ClockInterval i2 =
            ClockInterval.between(
                PlainTime.of(0, 45, 30),
                PlainTime.of(17, 10));
        ClockInterval i3 =
            ClockInterval.between(
                PlainTime.of(0),
                PlainTime.of(24));
        ClockInterval i4 =
            ClockInterval.between(
                PlainTime.of(11, 59, 59),
                PlainTime.of(12));
        IntervalCollection<PlainTime> windows =
            IntervalCollection.onClockAxis();
        windows = windows.plus(i1).plus(i2).plus(i3).plus(i4);

        Object ser = roundtrip(windows);
        assertThat(windows, is(ser));
    }

    @Test
    public void roundTripOfTimestampIntervalCollection()
        throws IOException, ClassNotFoundException {

        TimestampInterval i1 =
            TimestampInterval.between(
                PlainTimestamp.of(2014, 2, 27, 0, 0),
                PlainTimestamp.of(2014, 5, 14, 0, 0));
        TimestampInterval i2 =
            TimestampInterval.between(
                PlainTimestamp.of(2013, 2, 27, 0, 0),
                PlainTimestamp.of(2014, 4, 30, 0, 0));
        IntervalCollection<PlainTimestamp> windows =
            IntervalCollection.onTimestampAxis();
        windows = windows.plus(i1).plus(i2);

        Object ser = roundtrip(windows);
        assertThat(windows, is(ser));
    }

    @Test
    public void roundTripOfMomentIntervalCollection()
        throws IOException, ClassNotFoundException {

        MomentInterval i1 =
            MomentInterval.between(
                PlainTimestamp.of(2014, 2, 27, 0, 0).atUTC(),
                PlainTimestamp.of(2014, 5, 14, 0, 0).atUTC());
        MomentInterval i2 =
            MomentInterval.between(
                PlainTimestamp.of(2013, 2, 27, 0, 0).atUTC(),
                PlainTimestamp.of(2014, 4, 30, 0, 0).atUTC());
        IntervalCollection<Moment> windows = IntervalCollection.onMomentAxis();
        windows = windows.plus(i1).plus(i2);

        Object ser = roundtrip(windows);
        assertThat(windows, is(ser));
    }

    @Test
    public void roundTripOfGenericIntervalCollection()
        throws IOException, ClassNotFoundException {

        Instant now = Instant.now();
        SimpleInterval<Instant> i1 =
            SimpleInterval.between(Instant.EPOCH, Instant.now());
        SimpleInterval<Instant> i2 =
            SimpleInterval.since(now.plusSeconds(1));
        IntervalCollection<Instant> windows = IntervalCollection.onInstantTimeLine().plus(i2).plus(i1);

        Object ser = roundtrip(windows);
        assertThat(windows, is(ser));
    }

    @Test
    public void roundTripOfEmptyIntervalCollections()
        throws IOException, ClassNotFoundException {

        IntervalCollection<PlainDate> w1 = IntervalCollection.onDateAxis();
        assertThat(w1, is(roundtrip(w1)));

        IntervalCollection<PlainTime> w2 = IntervalCollection.onClockAxis();
        assertThat(w2, is(roundtrip(w2)));

        IntervalCollection<PlainTimestamp> w3 = IntervalCollection.onTimestampAxis();
        assertThat(w3, is(roundtrip(w3)));

        IntervalCollection<Moment> w4 = IntervalCollection.onMomentAxis();
        assertThat(w4, is(roundtrip(w4)));

        IntervalCollection<Date> w5 = IntervalCollection.onTraditionalTimeLine();
        assertThat(w5, is(roundtrip(w5)));

        IntervalCollection<Instant> w6 = IntervalCollection.onInstantTimeLine();
        assertThat(w6, is(roundtrip(w6)));
    }

    @Test
    public void roundTripOfYears()
        throws IOException, ClassNotFoundException {

        roundtrip(Years.ofGregorian(12));
        roundtrip(Years.ofWeekBased(3));
    }

    @Test
    public void roundTripOfMonths()
        throws IOException, ClassNotFoundException {

        roundtrip(Months.of(5));
    }

    @Test
    public void roundTripOfQuarters()
        throws IOException, ClassNotFoundException {

        roundtrip(Quarters.of(6));
    }

    @Test
    public void roundTripOfWeeks()
        throws IOException, ClassNotFoundException {

        roundtrip(Weeks.of(3));
    }

    @Test
    public void roundTripOfCalendarYear()
        throws IOException, ClassNotFoundException {

        roundtrip(CalendarYear.of(2016));
    }

    @Test
    public void roundTripOfCalendarQuarter()
        throws IOException, ClassNotFoundException {

        roundtrip(CalendarQuarter.of(2016, Quarter.Q3));
    }

    @Test
    public void roundTripOfCalendarMonth()
        throws IOException, ClassNotFoundException {

        roundtrip(CalendarMonth.of(2016, Month.FEBRUARY));
    }

    @Test
    public void roundTripOfSpanOfWeekdays()
        throws IOException, ClassNotFoundException {

        roundtrip(SpanOfWeekdays.betweenMondayAndFriday());
        roundtrip(SpanOfWeekdays.START);
        roundtrip(SpanOfWeekdays.END);
    }

    @Test
    public void roundtripOfSimpleInterval()
        throws IOException, ClassNotFoundException {

        Object interval = SimpleInterval.between(new Date(0L), new Date());
        Object ser = roundtrip(interval);
        assertThat(interval, is(ser));
    }

    @Test
    public void roundtripOfFixedCalendarIntervalTimeLine()
        throws IOException, ClassNotFoundException {

        Object intervalY = FixedCalendarTimeLine.forYears();
        assertThat(roundtrip(intervalY), is(intervalY));

        Object intervalQ = FixedCalendarTimeLine.forQuarters();
        assertThat(roundtrip(intervalQ), is(intervalQ));

        Object intervalM = FixedCalendarTimeLine.forMonths();
        assertThat(roundtrip(intervalM), is(intervalM));

        Object intervalW = FixedCalendarTimeLine.forWeeks();
        assertThat(roundtrip(intervalW), is(intervalW));

    }

    @Test
    public void roundtripOfFixedCalendarPeriod()
        throws IOException, ClassNotFoundException {

        Object cp = CalendarPeriod.between(CalendarWeek.of(2018, 1), CalendarWeek.of(2018, 37));
        assertThat(roundtrip(cp), is(cp));

    }

    @Test
    public void roundtripOfGenericCalendarPeriod()
        throws IOException, ClassNotFoundException {

        PlainDate d1 = PlainDate.of(2018, 1, 1);
        PlainDate d2 = PlainDate.of(2018, 12, 31);
        Object cp = CalendarPeriod.on(PlainDate.axis()).between(d1, d2);
        assertThat(roundtrip(cp), is(cp));

    }

    private static Object roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object ser = ois.readObject();
        ois.close();
        return ser;
    }

}