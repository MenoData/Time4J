package net.time4j.calendar.hindu;

import net.time4j.calendar.IndianMonth;
import net.time4j.calendar.astro.SolarTime;
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
    public void serializeVariants() throws IOException, ClassNotFoundException {
        roundtrip(AryaSiddhanta.SOLAR.variant());
        roundtrip(AryaSiddhanta.LUNAR.variant());

        for (HinduRule rule : HinduRule.values()) {
            roundtrip(rule.variant());
            roundtrip(rule.variant().with(HinduEra.KALI_YUGA));
            roundtrip(rule.variant().withElapsedYears());
            roundtrip(rule.variant().withCurrentYears());
            roundtrip(rule.variant().withModernAstronomy(0.0));
            roundtrip(rule.variant().withAlternativeLocation(SolarTime.ofMecca()));
        }
    }

    @Test
    public void serializeCalendar() throws IOException, ClassNotFoundException {
        roundtrip(HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19));
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