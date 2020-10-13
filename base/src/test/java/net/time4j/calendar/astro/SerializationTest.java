package net.time4j.calendar.astro;

import net.time4j.SystemClock;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    @Test
    public void serializeSolarObjects() throws IOException, ClassNotFoundException {
        roundtrip(SolarTime.ofMecca());
        roundtrip(SunPosition.at(SystemClock.currentMoment(), SolarTime.ofMecca()));
    }

    @Test
    public void serializeLunarObjects() throws IOException, ClassNotFoundException {
        roundtrip(LunarTime.ofLocation(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 1), 53.5, 10.0));
        roundtrip(MoonPosition.at(SystemClock.currentMoment(), SolarTime.ofJerusalem()));
    }

    private static void roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        assertThat(ois.readObject(), is(obj));
        ois.close();
    }

}