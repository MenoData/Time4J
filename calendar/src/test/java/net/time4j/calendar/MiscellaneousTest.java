package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.engine.CalendarDays;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MiscellaneousTest {

    @Test
    public void genericIslamicPattern() {
        String pattern = GenericDatePatterns.get("islamic", DisplayMode.FULL, new Locale("ar"));
        assertThat(pattern, is("EEEE، d MMMM، y G"));
        pattern = GenericDatePatterns.get("islamic", DisplayMode.FULL, Locale.GERMANY);
        assertThat(pattern, is("EEEE, d. MMMM y G"));
    }

    @Test
    public void copticCalendarProperties() {
        CopticCalendar date = CopticCalendar.of(1720, CopticMonth.AMSHIR, 9);
        assertThat(
            date.getDayOfMonth(),
            is(9));
        assertThat(
            date.getMonth(),
            is(CopticMonth.AMSHIR));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
        assertThat(
            date.lengthOfYear(),
            is(365)
        );
    }

    @Test
    public void copticCalendarBetween() {
        CopticCalendar start = CopticCalendar.of(1723, CopticMonth.AMSHIR, 6);
        CopticCalendar end = CopticCalendar.of(1723, CopticMonth.NASIE, 6);
        assertThat(CopticCalendar.Unit.MONTHS.between(start, end), is(7));
        start = start.plus(CalendarDays.ONE);
        assertThat(CopticCalendar.Unit.MONTHS.between(start, end), is(6));
    }

    @Test
    public void persianCalendarProperties() {
        PersianCalendar date = PersianCalendar.of(1394, PersianMonth.ABAN, 14);
        assertThat(
            date.getDayOfMonth(),
            is(14));
        assertThat(
            date.getMonth(),
            is(PersianMonth.ABAN));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
    }

    @Test
    public void persianCalendarBetween() {
        PersianCalendar start = PersianCalendar.of(1394, PersianMonth.ABAN, 14);
        PersianCalendar end = PersianCalendar.of(1394, PersianMonth.ESFAND, 13);
        assertThat(PersianCalendar.Unit.MONTHS.between(start, end), is(3));
        end = end.plus(CalendarDays.ONE);
        assertThat(PersianCalendar.Unit.MONTHS.between(start, end), is(4));
    }

    @Test
    public void khayam() {
        for (int pyear = 1178; pyear <= 1633; pyear++) {
            int m = pyear % 33;
            boolean leapKhayam = (m == 1 || m == 5 || m == 9 || m == 13 || m == 17 || m == 22 || m == 26 || m == 30);
            assertThat(
                PersianCalendar.of(pyear, 1, 1).isLeapYear(),
                is(leapKhayam));
        }
    }

    @Test
    public void executeCodeDemo() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
                .addPattern("EEE, d. MMMM yy", PatternType.NON_ISO_DATE).build()
                .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
                .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
        HijriCalendar hijri = formatter.parse("Thu, 29. Ramadan 36");
        PlainDate date = hijri.transform(PlainDate.class);
        System.out.println(date); // 2015-07-16
    }

    @Test
    public void executeICU() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
                .addPattern("y-MM-dd", PatternType.NON_ISO_DATE).build()
                .withCalendarVariant(HijriCalendar.VARIANT_ICU4J);
        HijriCalendar hijri = formatter.parse("1-01-01");
        PlainDate date = hijri.transform(PlainDate.class);
        System.out.println(date); // 622-07-18
    }

    @Test
    public void serializeHijri() throws IOException, ClassNotFoundException {
        roundtrip(HijriCalendar.ofUmalqura(1437, 3, 17));
    }

    @Test
    public void serializePersian() throws IOException, ClassNotFoundException {
        roundtrip(PersianCalendar.of(1425, 1, 7));
    }

    @Test
    public void serializeCoptic() throws IOException, ClassNotFoundException {
        roundtrip(CopticCalendar.of(1723, 13, 6));
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