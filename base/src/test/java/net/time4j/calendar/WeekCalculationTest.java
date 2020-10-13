package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoElement;
import net.time4j.format.Leniency;
import net.time4j.format.NumericalElement;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class WeekCalculationTest {

    @Test
    public void name() {
        assertThat(
            CommonElements.weekOfYear(HijriCalendar.family(), Weekmodel.ISO).name(),
            is("WEEK_OF_YEAR"));
        assertThat(
            CommonElements.weekOfMonth(HijriCalendar.family(), Weekmodel.ISO).name(),
            is("WEEK_OF_MONTH"));
        assertThat(
            CommonElements.localDayOfWeek(HijriCalendar.family(), Weekmodel.ISO).name(),
            is("LOCAL_DAY_OF_WEEK"));
    }

    @Test
    public void type() {
        assertThat(
            CommonElements.weekOfYear(HijriCalendar.family(), Weekmodel.ISO).getType().equals(Integer.class),
            is(true));
        assertThat(
            CommonElements.weekOfMonth(HijriCalendar.family(), Weekmodel.ISO).getType().equals(Integer.class),
            is(true));
        assertThat(
            CommonElements.localDayOfWeek(HijriCalendar.family(), Weekmodel.ISO).getType().equals(Weekday.class),
            is(true));
    }

    @Test
    public void getWeekOfYearISO() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11);
        ChronoElement<Integer> element = CommonElements.weekOfYear(HijriCalendar.family(), Weekmodel.ISO);
        assertThat(
            hijri.get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 10).get(element),
                is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 9).get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 8).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 7).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 6).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 5).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 4).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 3).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 2).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 1).get(element),
            is(51));
    }

    @Test
    public void getWeekOfYearIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        ChronoElement<Integer> element = CommonElements.weekOfYear(HijriCalendar.family(), model);
        assertThat(
            hijri.get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 10).get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 9).get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 8).get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 7).get(element),
            is(2));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 6).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 5).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 4).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 3).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 2).get(element),
            is(1));
        assertThat(
            hijri.with(HijriCalendar.DAY_OF_MONTH, 1).get(element),
            is(1));
    }

    @Test
    public void getWeekOfYearThai() {
        ThaiSolarCalendar thai = PlainDate.of(1940, 4, 11).transform(ThaiSolarCalendar.class); // Thursday
        Weekmodel model = ThaiSolarCalendar.getDefaultWeekmodel();
        ChronoElement<Integer> element = CommonElements.weekOfYear(ThaiSolarCalendar.axis(), model);
        assertThat(
            thai.get(element),
            is(2));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 10).get(element),
            is(2));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 9).get(element),
            is(2));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 8).get(element),
            is(2));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 7).get(element), // Sunday
            is(2));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 6).get(element),
            is(1));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 5).get(element),
            is(1));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 4).get(element),
            is(1));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 3).get(element),
            is(1));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 2).get(element),
            is(1));
        assertThat(
            thai.with(ThaiSolarCalendar.DAY_OF_MONTH, 1).get(element),
            is(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getLocalDayOfWeekIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            NumericalElement.class.cast(CommonElements.localDayOfWeek(HijriCalendar.family(), model))
                .numerical(hijri.getDayOfWeek()),
            is(5));
        assertThat(
            hijri.get(CommonElements.localDayOfWeek(HijriCalendar.family(), model)),
            is(Weekday.WEDNESDAY));
    }

    @Test
    public void minimizeLocalDayOfWeekIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.localDayOfWeek(HijriCalendar.family(), model).minimized()),
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 7)));
    }
    @Test
    public void maximizeLocalDayOfWeekIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.localDayOfWeek(HijriCalendar.family(), model).maximized()),
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 13)));
    }

    @Test
    public void minimizeWeekOfYearISO() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.ISO;
        HijriCalendar expected = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 4);
        assertThat(
            hijri.with(CommonElements.weekOfYear(HijriCalendar.family(), model).minimized()),
            is(expected));
        assertThat(
            hijri.getDayOfWeek(),
            is(expected.getDayOfWeek()));
    }

    @Test
    public void maximizeWeekOfYearISO() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1437, 1, 11); // Saturday
        Weekmodel model = Weekmodel.ISO;
        HijriCalendar expected = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1437, 12, 29);
        assertThat(
            hijri.with(CommonElements.weekOfYear(HijriCalendar.family(), model).maximized()),
            is(expected));
        assertThat(
            hijri.getDayOfWeek(),
            is(expected.getDayOfWeek()));
        HijriCalendar hijri2 = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1436, 12, 29); // Monday
        assertThat(
            hijri2.with(CommonElements.weekOfYear(HijriCalendar.family(), model).maximized()),
            is(HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1437, 12, 24)));
    }

    @Test
    public void getMaximumWeekOfYearThai() {
        ThaiSolarCalendar thai = PlainDate.of(1940, 4, 11).transform(ThaiSolarCalendar.class); // Thursday
        Weekmodel model = ThaiSolarCalendar.getDefaultWeekmodel();
        StdCalendarElement<Integer, ThaiSolarCalendar> element =
            CommonElements.weekOfYear(ThaiSolarCalendar.axis(), model);
        assertThat(
            thai.getMaximum(element),
            is(39));
        assertThat(
            thai.with(element.maximized()),
            is(PlainDate.of(1940, 12, 26).transform(ThaiSolarCalendar.class)));
        ThaiSolarCalendar thai2 = PlainDate.of(1940, 12, 31).transform(ThaiSolarCalendar.class); // Tuesday
        assertThat(
            thai2.get(element),
            is(1));
        assertThat(
            thai2.with(ThaiSolarCalendar.DAY_OF_MONTH, 30).get(element),
            is(1));
        assertThat(
            thai2.with(ThaiSolarCalendar.DAY_OF_MONTH, 29).get(element),
            is(1));
        assertThat(
            thai2.with(ThaiSolarCalendar.DAY_OF_MONTH, 28).get(element),
            is(39));
    }

    @Test
    public void floorWeekOfYearISO() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        HijriCalendar expected = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 7);
        assertThat(
            hijri.with(CommonElements.weekOfYear(HijriCalendar.family(), model).atFloor()),
            is(expected));
    }

    @Test
    public void ceilingWeekOfYearISO() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        HijriCalendar expected = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 13);
        assertThat(
            hijri.with(CommonElements.weekOfYear(HijriCalendar.family(), model).atCeiling()),
            is(expected));
    }

    @Test
    public void incrementLocalDayOfWeekIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.localDayOfWeek(HijriCalendar.family(), model).incremented()),
            is(hijri.nextDay()));
    }

    @Test
    public void incrementWeekOfMonthIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.weekOfMonth(HijriCalendar.family(), model).incremented()),
            is(hijri.with(HijriCalendar.DAY_OF_MONTH, 18)));
    }

    @Test
    public void incrementWeekOfYearIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.weekOfYear(HijriCalendar.family(), model).incremented()),
            is(hijri.with(HijriCalendar.DAY_OF_MONTH, 18)));
    }

    @Test
    public void decrementLocalDayOfWeekIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.localDayOfWeek(HijriCalendar.family(), model).decremented()),
            is(hijri.with(HijriCalendar.DAY_OF_MONTH, 10)));
    }

    @Test
    public void decrementWeekOfMonthIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.weekOfMonth(HijriCalendar.family(), model).decremented()),
            is(hijri.with(HijriCalendar.DAY_OF_MONTH, 4)));
    }

    @Test
    public void decrementWeekOfYearIR() {
        HijriCalendar hijri = HijriCalendar.of(HijriAlgorithm.WEST_ISLAMIC_ASTRO, 1438, 1, 11); // Wednesday
        Weekmodel model = Weekmodel.of(Weekday.SATURDAY, 1);
        assertThat(
            hijri.with(CommonElements.weekOfYear(HijriCalendar.family(), model).decremented()),
            is(hijri.with(HijriCalendar.DAY_OF_MONTH, 4)));
    }

    @Test
    public void getDisplayNameOfWeekOfYear() {
        assertThat(
            CommonElements.weekOfYear(
                HijriCalendar.family(),
                Weekmodel.ISO
            ).getDisplayName(Locale.GERMAN),
            is("Woche"));
    }

    @Test
    public void getDisplayNameOfWeekOfMonth() {
        assertThat(
            CommonElements.weekOfMonth(
                HijriCalendar.family(),
                Weekmodel.ISO
            ).getDisplayName(Locale.GERMAN),
            is("Woche"));
    }

    @Test
    public void getDisplayNameOfLocalDayOfWeek() {
        assertThat(
            CommonElements.localDayOfWeek(
                HijriCalendar.family(),
                Weekmodel.ISO
            ).getDisplayName(Locale.GERMAN),
            is("Wochentag"));
    }

    @Test
    public void printWeekElementsThai() {
        ThaiSolarCalendar thai = PlainDate.of(1940, 12, 28).transform(ThaiSolarCalendar.class); // Saturday
        ChronoFormatter<ThaiSolarCalendar> cf =
            ChronoFormatter.ofPattern("y w W ee c", PatternType.CLDR_DATE, Locale.ENGLISH, ThaiSolarCalendar.axis());
        assertThat(cf.format(thai), is("2483 39 4 07 7"));
        ChronoFormatter<ThaiSolarCalendar> cf2 =
            ChronoFormatter.ofPattern("y ww W ee c", PatternType.CLDR, Locale.ENGLISH, ThaiSolarCalendar.axis());
        assertThat(cf2.format(thai), is("2483 39 4 07 7"));
    }

    @Test
    public void parseWeekElementsThai() throws ParseException {
        ThaiSolarCalendar thai = PlainDate.of(1940, 12, 28).transform(ThaiSolarCalendar.class); // Saturday
        ChronoFormatter<ThaiSolarCalendar> cf =
            ChronoFormatter.ofPattern(
                "G-yyyy-MM-dd w W ee c", PatternType.CLDR_DATE, Locale.ENGLISH, ThaiSolarCalendar.axis());
        assertThat(
            cf.with(Leniency.STRICT).parse("BE-2483-12-28 39 4 07 7"),
            is(thai));
        try {
            cf.with(Leniency.STRICT).parse("BE-2483-12-28 39 4 07 6");
            fail("Should fail due to parse exception because of ambivalent local-day-of-week.");
        } catch (ParseException pe) {
            // expected
        }
    }

    @Test
    public void ceilingBoundedWeekOfMonthISO() {
        CopticCalendar coptic = CopticCalendar.of(1723, 13, 5);
        assertThat(
            coptic.getDayOfWeek(),
            is(Weekday.MONDAY));
        assertThat(
            coptic.with(CommonElements.boundedWeekOfMonth(CopticCalendar.axis(), Weekmodel.ISO).atCeiling()),
            is(CopticCalendar.of(1723, 13, 6)));
    }

    @Test
    public void floorBoundedWeekOfMonthISO() {
        CopticCalendar coptic = CopticCalendar.of(1724, 1, 3);
        assertThat(
            coptic.getDayOfWeek(),
            is(Weekday.FRIDAY));
        assertThat(
            coptic.with(CommonElements.boundedWeekOfMonth(CopticCalendar.axis(), Weekmodel.ISO).atFloor()),
            is(CopticCalendar.of(1724, 1, 1)));
    }

    @Test
    public void persianBoundedWeekOfMonth() {
        PersianCalendar pcal = PersianCalendar.of(1396, 10, 4); // month with 30 days beginning on Friday
        assertThat(
            pcal.getDayOfWeek(),
            is(Weekday.MONDAY));
        assertThat(
            pcal.getMaximum(
                CommonElements.boundedWeekOfMonth(PersianCalendar.axis(), PersianCalendar.getDefaultWeekmodel())),
            // persian default week model has Saturday as first day of week and only needs one day in first week
            is(6));
        assertThat(
            pcal.getMaximum(PersianCalendar.BOUNDED_WEEK_OF_MONTH), // convenience
            is(6));
    }

    @Test
    public void hebrewBoundedWeekOfMonth() {
        HebrewCalendar hcal = HebrewCalendar.of(5778, HebrewMonth.NISAN, 2); // month with 30 days beginning on Saturday
        assertThat(
            hcal.getDayOfWeek(),
            is(Weekday.SUNDAY));
        assertThat(
            hcal.getMaximum(
                CommonElements.boundedWeekOfMonth(HebrewCalendar.axis(), HebrewCalendar.getDefaultWeekmodel())),
            is(6));
        assertThat(
            hcal.getMaximum(HebrewCalendar.BOUNDED_WEEK_OF_MONTH), // convenience
            is(6));
    }

    @Test
    public void hebrewWeekElements() {
        assertThat(
            CommonElements.localDayOfWeek(HebrewCalendar.axis(), HebrewCalendar.getDefaultWeekmodel()),
            is(HebrewCalendar.LOCAL_DAY_OF_WEEK));
        assertThat(
            CommonElements.weekOfYear(HebrewCalendar.axis(), HebrewCalendar.getDefaultWeekmodel()),
            is(HebrewCalendar.WEEK_OF_YEAR));
        assertThat(
            CommonElements.weekOfMonth(HebrewCalendar.axis(), HebrewCalendar.getDefaultWeekmodel()),
            is(HebrewCalendar.WEEK_OF_MONTH));
        assertThat(
            CommonElements.boundedWeekOfYear(HebrewCalendar.axis(), HebrewCalendar.getDefaultWeekmodel()),
            is(HebrewCalendar.BOUNDED_WEEK_OF_YEAR));
        assertThat(
            CommonElements.boundedWeekOfMonth(HebrewCalendar.axis(), HebrewCalendar.getDefaultWeekmodel()),
            is(HebrewCalendar.BOUNDED_WEEK_OF_MONTH));
    }

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        roundtrip(CommonElements.localDayOfWeek(HijriCalendar.family(), Weekmodel.ISO));
        roundtrip(CommonElements.localDayOfWeek(PersianCalendar.axis(), Weekmodel.of(new Locale("fa", "IR"))));
        roundtrip(CommonElements.weekOfMonth(HijriCalendar.family(), Weekmodel.ISO));
        roundtrip(CommonElements.weekOfMonth(PersianCalendar.axis(), Weekmodel.of(new Locale("fa", "IR"))));
        roundtrip(CommonElements.weekOfYear(HijriCalendar.family(), Weekmodel.ISO));
        roundtrip(CommonElements.weekOfYear(PersianCalendar.axis(), Weekmodel.of(new Locale("fa", "IR"))));
        roundtrip(CommonElements.boundedWeekOfYear(HijriCalendar.family(), Weekmodel.ISO));
        roundtrip(CommonElements.boundedWeekOfMonth(PersianCalendar.axis(), Weekmodel.of(Locale.CHINA)));
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