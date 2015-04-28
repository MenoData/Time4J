package net.time4j.history;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    @Test
    public void roundTripVar0()
        throws IOException, ClassNotFoundException {

        roundtrip(ChronoHistory.ofGregorianReform(PlainDate.of(1752, 3, 1)));
    }

    @Test
    public void roundTripOfFirstGregorianReform()
        throws IOException, ClassNotFoundException {

        roundtrip(ChronoHistory.ofFirstGregorianReform());
    }

    @Test
    public void roundTripOfProlepticGregorian()
        throws IOException, ClassNotFoundException {

        roundtrip(ChronoHistory.PROLEPTIC_GREGORIAN);
    }

    @Test
    public void roundTripOfProlepticJulian()
        throws IOException, ClassNotFoundException {

        roundtrip(ChronoHistory.PROLEPTIC_JULIAN);
    }

    @Test
    public void roundTripOfSweden()
        throws IOException, ClassNotFoundException {

        roundtrip(ChronoHistory.ofSweden());
    }

    private static int roundtrip(Object obj)
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
        return data.length;
    }

}