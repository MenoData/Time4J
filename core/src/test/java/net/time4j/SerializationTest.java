package net.time4j;

import net.time4j.format.ParseLog;
import net.time4j.scale.TimeScale;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    private static final int N = 1000;
    private static final int GRAPH_SIZE = 50;

    private boolean output = false;

    @Before
    public void setUp() {
        try {
            this.output = false;
            this.sizeOfDate();
            this.sizeOfTime();
        } catch (Exception ex) {
            // ignore exceptions in warm-up phase
        } finally {
            this.output = true;
        }
    }

    @Test
    public void roundtripOfDate()
        throws IOException, ClassNotFoundException {

        int len1 = roundtrip(PlainDate.of(2100, 4, 30));
        int len2 = roundtrip(PlainDate.of(1849, 4, 30));
        int len3 = roundtrip(PlainDate.of(-20000, 4, 30));
        assertThat(len2 - len1, is(1));
        assertThat(len3 - len2, is(2));
    }

    @Test
    public void sizeOfDate()
        throws IOException, ClassNotFoundException {

        Object[] sers = new Object[GRAPH_SIZE];
        for (int i = 0; i < sers.length; i++) {
            sers[i] = PlainDate.of(2012 + i, 2, 28);
        }
        Info info = analyze("[PlainDate]", sers);
        assertThat(info.first, is(41));
        assertThat(info.next, is(12));
    }

    @Test
    public void roundtripOfTime()
        throws IOException, ClassNotFoundException {

        roundtrip(PlainTime.of(12));
        roundtrip(PlainTime.of(12, 24));
        roundtrip(PlainTime.of(12, 24, 52));
        roundtrip(PlainTime.of(12, 24, 52, 123456789));
    }

    @Test
    public void sizeOfTime()
        throws IOException, ClassNotFoundException {

        Object[] sers = new Object[GRAPH_SIZE];
        for (int i = 0; i < sers.length; i++) {
            sers[i] = PlainTime.of(13, 52, i % 10, 0);
        }
        Info info = analyze("[PlainTime]", sers);
        assertThat(info.first, is(41));
        assertThat(info.next, is(12));
    }

    @Test
    public void roundtripOfTimestamp()
        throws IOException, ClassNotFoundException {

        roundtrip(PlainTimestamp.of(2012, 2, 29, 15, 59, 30));
    }

    @Test
    public void sizeOfTimestamp()
        throws IOException, ClassNotFoundException {

        Object[] sers = new Object[GRAPH_SIZE];
        for (int i = 0; i < sers.length; i++) {
            sers[i] = PlainTimestamp.of(2012 + i, 12, 30, 13, i % 60);
        }
        Info info = analyze("[PlainTimestamp]", sers);
        assertThat(info.first, is(42)); // old = 62
        assertThat(info.next, is(14)); // old = 34
    }

    @Test
    public void roundtripOfWeekmodel()
        throws IOException, ClassNotFoundException {

        int result =
            roundtrip(
                Weekmodel.ISO,
                Weekmodel.of(Locale.US),
                Weekmodel.of(new Locale("", "YE")));
        assertThat(
            (result == 63 || result == 62), // depends on if i18n-module exists
            is(true)); // erstes, zweites und drittes Datenpaket
    }

    @Test
    public void roundTripOfMoment()
        throws IOException, ClassNotFoundException {

        roundtrip(
            Moment.UNIX_EPOCH,
            Moment.of(1341100824, 987654321, TimeScale.UTC));
    }

    @Test
    public void sizeOfMoment()
        throws IOException, ClassNotFoundException {

        Object[] sers = new Object[GRAPH_SIZE];
        int offset = 2 * 365 * 86400;
        for (int i = 0; i < sers.length; i++) {
            sers[i] =
                Moment.of(1341100798 + i - offset, 123456789, TimeScale.UTC);
        }
        Info info = analyze("[MOMENT]", sers);
        assertThat(info.first, is(51));
        assertThat(info.next, is(22));
    }

    @Test
    public void roundTripOfDuration()
        throws IOException, ClassNotFoundException {

        roundtrip(
            Duration.ofZero(),
            Duration.of(4, ClockUnit.HOURS),
            Duration.ofCalendarUnits(0, 13, 5));
    }

    @Test
    public void roundTripOfMachineTime()
        throws IOException, ClassNotFoundException {

        roundtrip(
            MachineTime.ofPosixSeconds(123.5),
            MachineTime.ofSIUnits(123, 987654321));
    }

    @Test
    public void sizeOfDuration()
        throws IOException, ClassNotFoundException {

        Object[] sers = new Object[GRAPH_SIZE];
        for (int i = 0; i < sers.length; i++) {
            sers[i] = Duration.ofCalendarUnits(i, i % 12, 30);
        }
        Info info = analyze("[Duration]", sers);
        assertThat(info.first, is(126));
        assertThat(info.next, is(47));
    }

    @Test
    public void roundTripOfParsedValues()
        throws IOException, ClassNotFoundException {

        ParseLog plog = new ParseLog();
        Iso8601Format.EXTENDED_CALENDAR_DATE.parse("2012-02-29", plog);
        Object obj = plog.getRawValues();
        roundtrip(obj);
    }

    private Info analyze(String msg, Object[] sers)
        throws IOException, ClassNotFoundException {

        // Maßzahlen für Netzlaufzeit
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(sers[0]);
        int first = baos.size();

        for (int i = 1; i < sers.length; i++) {
            oos.writeObject(sers[i]);
        }
        oos.close();
        int next = ((baos.size() - first) / (sers.length - 1));

        if (this.output) {
            System.out.println("FIRST: " + first);
            System.out.println("NEXT: " + next);
            System.out.println("AVG: " + (baos.size() / sers.length));
        } else {
            return null;
        }

        // Maßzahl für Serialisierungsaufwand
        baos = new ByteArrayOutputStream(sers.length * (next + 1));
        long t0 = System.nanoTime();
        for (int i = 0; i < N; i++) {
            baos.reset();
            oos = new ObjectOutputStream(baos);
            for (int j = 0; j < sers.length; j++) {
                oos.writeObject(sers[j]);
            }
            oos.close();
        }
        long t1 = System.nanoTime();
        System.out.printf(msg + " Writing: %d%n", (t1 - t0) / 1000000);

        // Maßzahl für Deserialisierungsaufwand
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        t0 = System.nanoTime();
        for (int i = 0; i < N; i++) {
            bais.reset();
            ObjectInputStream ois = new ObjectInputStream(bais);
            for (int j = 0; j < sers.length; j++) {
                ois.readObject();
            }
            ois.close();
        }
        t1 = System.nanoTime();
        System.out.printf(msg + " Reading: %d%n", (t1 - t0) / 1000000);

        return new Info(first, next);
    }

    private static int roundtrip(Object... obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        for (int i = 0; i < obj.length; i++) {
            oos.writeObject(obj[i]);
        }
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        for (int i = 0; i < obj.length; i++) {
            assertThat(ois.readObject(), is(obj[i]));
        }
        ois.close();
        return data.length;
    }

    private static class Info {
        final int first;
        final int next;

        Info(int first, int next) {
            super();
            this.first = first;
            this.next = next;
        }
    }
}