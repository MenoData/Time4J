package net.time4j.calendar;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.calendar.service.GenericDatePatterns;
import net.time4j.engine.CalendarDays;
import net.time4j.format.Attributes;
import net.time4j.format.DisplayMode;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
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
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.chrono.MinguoDate;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MiscellaneousTest {

    @Test
    public void ethiopicNumeralFormat() throws ParseException {
        Locale amharic = new Locale("am");
        ChronoFormatter<EthiopianCalendar> formatter =
            ChronoFormatter.setUp(EthiopianCalendar.class, amharic)
                .addPattern("MMMM d ", PatternType.NON_ISO_DATE)
                .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ETHIOPIC)
                .addInteger(EthiopianCalendar.YEAR_OF_ERA, 1, 9)
                .endSection()
                .addLiteral(" (")
                .addText(EthiopianCalendar.EVANGELIST)
                .addPattern(") G", PatternType.NON_ISO_DATE)
                .build()
                .with(Leniency.STRICT);
        String input = "ጥቅምት 11 ፲፱፻፺፯ (ማቴዎስ) ዓ/ም";
        EthiopianCalendar ethio = formatter.parse(input);
        assertThat(
            ethio,
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1997, 2, 11)));

        // roundtrip test
        String output = formatter.format(ethio);
        assertThat(output, is(input));

        // test of default number system for years
        ChronoFormatter<EthiopianCalendar> f2 =
            ChronoFormatter.setUp(EthiopianCalendar.class, amharic)
                .addPattern("MMMM d yyyy G", PatternType.NON_ISO_DATE).build();
        assertThat(f2.parse("ጥቅምት 11 ፲፱፻፺፯ ዓ/ም"), is(ethio));
    }

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
        start = start.minus(3, CopticCalendar.Unit.YEARS);
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(3));
        start = start.plus(6, CopticCalendar.Unit.YEARS).minus(CalendarDays.of(2)); // A.M.-1726-06-05
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-2));
        start = start.with(CopticCalendar.MONTH_OF_YEAR, CopticMonth.NASIE); // A.M.-1726-13-05
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-2));
        start = start.plus(1, CopticCalendar.Unit.YEARS); // A.M.-1727-06-05
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-3));
        start = start.plus(CalendarDays.ONE); // A.M.-1727-06-06
        assertThat(CopticCalendar.Unit.YEARS.between(start, end), is(-4));
    }

    @Test
    public void ethiopianCalendarProperties() {
        EthiopianCalendar date = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2008, EthiopianMonth.YEKATIT, 9);
        assertThat(
            date.getEra(),
            is(EthiopianEra.AMETE_MIHRET));
        assertThat(
            date.getDayOfMonth(),
            is(9));
        assertThat(
            date.getMonth(),
            is(EthiopianMonth.YEKATIT));
        assertThat(
            date.lengthOfMonth(),
            is(30));
        assertThat(
            date.lengthOfYear(),
            is(365)
        );
    }

    @Test
    public void ethiopianCalendarBetween() {
        EthiopianCalendar start = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, EthiopianMonth.YEKATIT, 6);
        EthiopianCalendar end = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, EthiopianMonth.PAGUMEN, 6);
        assertThat(EthiopianCalendar.Unit.MONTHS.between(start, end), is(7));
        start = start.plus(CalendarDays.ONE);
        assertThat(EthiopianCalendar.Unit.MONTHS.between(start, end), is(6));
        start = start.minus(3, EthiopianCalendar.Unit.YEARS);
        assertThat(EthiopianCalendar.Unit.YEARS.between(start, end), is(3));
        end = end.with(EthiopianCalendar.MONTH_OF_YEAR, EthiopianMonth.YEKATIT);
        assertThat(EthiopianCalendar.Unit.YEARS.between(start, end), is(2));
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

        start = PersianCalendar.of(1360, 2, 20);
        end = PersianCalendar.of(1394, 11, 25);

        for (int i = 0; i < 15; i++) {
            start = start.plus(1, PersianCalendar.Unit.DAYS);
            assertThat(
                PersianCalendar.Unit.YEARS.between(start, end),
                is(34));
        }

        start = PersianCalendar.of(1360, 2, 20);
        end = PersianCalendar.of(1394, 2, 20);

        assertThat(
            PersianCalendar.Unit.YEARS.between(start, end),
            is(34));
        start = PersianCalendar.of(1360, 2, 21);
        assertThat(
            PersianCalendar.Unit.YEARS.between(start, end),
            is(33));
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
    public void formatPersianCalendar() throws ParseException {
        ChronoFormatter<PersianCalendar> formatter = // pattern => G y MMMM d, EEEE
            ChronoFormatter.ofStyle(DisplayMode.FULL, new Locale("fa"), PersianCalendar.axis());
        PersianCalendar jalali = PersianCalendar.of(1393, 2, 10);

        String expected = "ه\u200D.ش."; // era
        expected += " ";
        expected += "۱۳۹۳"; // year
        expected += " ";
        expected += "اردیبهشت"; // month
        expected += " ";
        expected += "۱۰"; // day-of-month
        expected += ", ";
        expected += "چهارشنبه"; // day-of-week
        PersianCalendar parsed = formatter.parse(expected);
        assertThat(parsed, is(jalali));

        String formatted = formatter.format(jalali); //         ه‍.ش. ۱۳۹۳ اردیبهشت۱۰,چهارشنبه
        assertThat(formatted, is(expected));

        assertThat(jalali.transform(PlainDate.class), is(PlainDate.of(2014, 4, 30)));
    }

    @Test
    public void formatHijrahDate() {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.setUp(HijriCalendar.class, Locale.ROOT)
            .addPattern("yyyy-MM-dd", PatternType.CLDR).build();
        HijrahDate date = HijrahDate.from(LocalDate.of(2015, 8, 21));
        assertThat(
            formatter.formatThreeten(date),
            is("1436-11-06"));
        assertThat(
            PlainDate.of(2015, 8, 21).transform(HijriCalendar.class, HijriCalendar.VARIANT_UMALQURA),
            is(HijriCalendar.ofUmalqura(1436, 11, 6)));
    }

    @Test
    public void formatMinguoDate() {
        ChronoFormatter<MinguoCalendar> formatter =
            ChronoFormatter.setUp(MinguoCalendar.axis(), Locale.ROOT)
                .addPattern("y-MM-dd", PatternType.CLDR).build();
        MinguoDate date = MinguoDate.from(LocalDate.of(2015, 8, 21));
        assertThat(
            formatter.formatThreeten(date),
            is("104-08-21"));
        assertThat(
            PlainDate.of(2015, 8, 21).transform(MinguoCalendar.class),
            is(MinguoCalendar.of(MinguoEra.ROC, 104, 8, 21)));
    }

    @Test
    public void executeCodeDemo() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.ofPattern(
                "EEE, d. MMMM yy", PatternType.NON_ISO_DATE, Locale.ENGLISH, HijriCalendar.family())
            .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
            .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
        HijriCalendar hijri = formatter.parse("Thu, 29. Ramadan 36");
        PlainDate date = hijri.transform(PlainDate.class);
        assertThat(date, is(PlainDate.of(2015, 7, 16)));
    }

    @Test
    public void executeICU() throws ParseException {
        ChronoFormatter<HijriCalendar> formatter =
            ChronoFormatter.ofPattern(
                "y-MM-dd", PatternType.NON_ISO_DATE, Locale.ENGLISH, HijriCalendar.family())
            .withCalendarVariant(HijriCalendar.VARIANT_ICU4J);
        HijriCalendar hijri = formatter.parse("1-01-01");
        PlainDate date = hijri.transform(PlainDate.class);
        assertThat(date, is(PlainDate.of(622, 7, 18)));
    }

    @Test
    public void minguoCalendarProperties() {
        MinguoCalendar date = MinguoCalendar.of(MinguoEra.ROC, 89, Month.FEBRUARY, 14);
        assertThat(
            date.getDayOfMonth(),
            is(14));
        assertThat(
            date.getMonth(),
            is(Month.FEBRUARY));
        assertThat(
            date.lengthOfMonth(),
            is(29));
        assertThat(
            date.lengthOfYear(),
            is(366));
        assertThat(
            date.atTime(12, 0).toDate(),
            is(date));
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
    public void serializeMinguo() throws IOException, ClassNotFoundException {
        roundtrip(MinguoCalendar.of(MinguoEra.ROC, 105, 1, 7));
    }

    @Test
    public void serializeCoptic() throws IOException, ClassNotFoundException {
        roundtrip(CopticCalendar.of(1723, 13, 6));
    }

    @Test
    public void serializeEthiopianDate() throws IOException, ClassNotFoundException {
        roundtrip(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, 13, 6));
    }

    @Test
    public void serializeEthiopianTime() throws IOException, ClassNotFoundException {
        roundtrip(EthiopianTime.ofDay(4, 45, 23));
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