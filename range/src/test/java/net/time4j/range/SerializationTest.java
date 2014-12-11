package net.time4j.range;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


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