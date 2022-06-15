package net.time4j.calendar;

import net.time4j.calendar.astro.JulianDay;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class SerializationTest {

    @Test
    public void serializeHijri() throws IOException, ClassNotFoundException {
        roundtrip(HijriCalendar.ofUmalqura(1437, 3, 17));
    }

    @Test
    public void serializePersian() throws IOException, ClassNotFoundException {
        roundtrip(PersianCalendar.of(1425, 1, 7));
    }

    @Test
    public void serializeMinguo() throws IOException, ClassNotFoundException {
        roundtrip(MinguoCalendar.of(MinguoEra.ROC, 105, 1, 7));
    }

    @Test
    public void serializeCoptic() throws IOException, ClassNotFoundException {
        roundtrip(CopticCalendar.of(1723, 13, 6));
    }

    @Test
    public void serializeEthiopian() throws IOException, ClassNotFoundException {
        roundtrip(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, 13, 6));
        roundtrip(EthiopianTime.ofDay(4, 45, 23));
    }

    @Test
    public void serializeJulian() throws IOException, ClassNotFoundException {
        roundtrip(JulianCalendar.of(HistoricEra.AD, 1752, 9, 14));
        roundtrip(JulianCalendar.of(HistoricEra.BC, 46, 2, 13));
    }

    @Test
    public void serializeThaiSolar() throws IOException, ClassNotFoundException {
        roundtrip(ThaiSolarCalendar.of(ThaiSolarEra.BUDDHIST, 2482, 2, 7));
    }

    @Test
    public void serializeJapanese() throws IOException, ClassNotFoundException {
        roundtrip(JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 12));
        roundtrip(JapaneseCalendar.ofGregorian(Nengo.SHOWA, 64, 1, 7)); // would fail in lax mode
        roundtrip(JapaneseCalendar.of(Nengo.ofRelatedGregorianYear(1857), 4, EastAsianMonth.valueOf(5).withLeap(), 1));
    }

    @Test
    public void serializeIndian() throws IOException, ClassNotFoundException {
        roundtrip(IndianCalendar.of(1912, 5, 31));
    }

    @Test
    public void serializeJulianDay() throws IOException, ClassNotFoundException {
        roundtrip(JulianDay.ofEphemerisTime(2451545.0));
    }

    @Test
    public void serializeHistoric() throws IOException, ClassNotFoundException {
        roundtrip(HistoricCalendar.of(ChronoHistory.of(Locale.UK), HistoricEra.AD, 1603, 3, 24));
    }

    @Test
    public void serializeHebrew() throws IOException, ClassNotFoundException {
        roundtrip(HebrewCalendar.of(5779, HebrewMonth.ADAR_I, 1));
        roundtrip(HebrewTime.ofDay(12, 540));
    }

    @Test
    public void serializeChinese() throws IOException, ClassNotFoundException {
        roundtrip(ChineseCalendar.ofNewYear(2018));
    }

    @Test
    public void serializeKorean() throws IOException, ClassNotFoundException {
        roundtrip(KoreanCalendar.ofNewYear(2018));
    }

    @Test
    public void serializeVietnamese() throws IOException, ClassNotFoundException {
        roundtrip(VietnameseCalendar.ofTet(2018));
    }

    @Test
    public void serializeJuche() throws IOException, ClassNotFoundException {
        roundtrip(JucheCalendar.of(105, 1, 7));
    }

    @Test
    public void serializeSexagesimalName() throws IOException, ClassNotFoundException {
        roundtrip(SexagesimalName.of(11));
    }

    @Test
    public void serializeCyclicYear() throws IOException, ClassNotFoundException {
        roundtrip(CyclicYear.of(11));
    }

    private static int roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            data = baos.toByteArray();
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            assertThat(ois.readObject(), is(obj));
        }
        
        return data.length;
    }

}