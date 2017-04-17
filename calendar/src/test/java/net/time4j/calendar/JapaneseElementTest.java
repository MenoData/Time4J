package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class JapaneseElementTest {

    @Test
    public void generalTimestamp() {
        JapaneseCalendar jcal =
            JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 8, Leniency.SMART);
        assertThat(jcal.atTime(17, 45).toDate(), is(jcal));
        assertThat(jcal.atTime(17, 45).toTime(), is(PlainTime.of(17, 45)));
    }

    @Test
    public void nowInSystemTime() {
        System.out.println(JapaneseCalendar.nowInSystemTime());
    }

    @Test
    public void minDate() {
        JapaneseCalendar min =
            JapaneseCalendar.of(Nengo.ofRelatedGregorianYear(701), 1, EastAsianMonth.valueOf(1), 1);
        assertThat(min, is(JapaneseCalendar.axis().getMinimum()));
        try {
            JapaneseCalendar.axis().getCalendarSystem().transform(min.getDaysSinceEpochUTC() - 1);
            fail("Expected exception of type \'IllegalArgumentException\' is missing.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
    }

    @Test
    public void maxDate() {
        JapaneseCalendar max = PlainDate.axis().getMaximum().transform(JapaneseCalendar.class);
        assertThat(max, is(JapaneseCalendar.axis().getMaximum()));
        try {
            JapaneseCalendar.axis().getCalendarSystem().transform(max.getDaysSinceEpochUTC() + 1);
            fail("Expected exception of type \'IllegalArgumentException\' is missing.");
        } catch (IllegalArgumentException iae) {
            // ok
        }
    }

    @Test
    public void eraElement() {
        JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 13);
        assertThat(jcal.get(JapaneseCalendar.NENGO), is(jcal.getEra()));
        assertThat(jcal.getMinimum(JapaneseCalendar.NENGO), is(JapaneseCalendar.NENGO.getDefaultMinimum()));
        assertThat(jcal.getMaximum(JapaneseCalendar.NENGO), is(JapaneseCalendar.NENGO.getDefaultMaximum()));
        assertThat(jcal.isValid(JapaneseCalendar.NENGO, null), is(false));
        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        assertThat(jcal.isValid(JapaneseCalendar.NENGO, ansei), is(true));
        assertThat(
            jcal.with(JapaneseCalendar.NENGO, ansei),
            is(JapaneseCalendar.of(ansei, 7, EastAsianMonth.valueOf(4), 13)));
        assertThat(JapaneseCalendar.NENGO.getDisplayName(Locale.ENGLISH), is("era"));
        assertThat(JapaneseCalendar.NENGO.getDisplayName(Locale.ROOT), is("NENGO"));
    }

    @Test
    public void yearOfNengoElement() {
        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        JapaneseCalendar jcal = JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 30);
        assertThat(jcal.getMinimum(JapaneseCalendar.YEAR_OF_NENGO), is(1));
        assertThat(jcal.getMaximum(JapaneseCalendar.YEAR_OF_NENGO), is(7));
        assertThat(jcal.getInt(JapaneseCalendar.YEAR_OF_NENGO), is(5));
        assertThat(jcal.isValid(JapaneseCalendar.YEAR_OF_NENGO, 7), is(true));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO, 6),
            is(JapaneseCalendar.of(ansei, 6, EastAsianMonth.valueOf(8), 29)));
        assertThat(JapaneseCalendar.YEAR_OF_NENGO.getDisplayName(Locale.ENGLISH), is("year"));
        assertThat(JapaneseCalendar.YEAR_OF_NENGO.getDisplayName(Locale.ROOT), is("YEAR_OF_NENGO"));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO.maximized()),
            is(JapaneseCalendar.of(ansei.findNext().get(), 1, EastAsianMonth.valueOf(8), 29)));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO.minimized()),
            is(JapaneseCalendar.of(ansei, 1, EastAsianMonth.valueOf(8), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO.incremented()),
            is(JapaneseCalendar.of(ansei, 6, EastAsianMonth.valueOf(8), 29)));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO.decremented()),
            is(JapaneseCalendar.of(ansei, 4, EastAsianMonth.valueOf(8), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO.atFloor()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(1), 1)));
        assertThat(
            jcal.with(JapaneseCalendar.YEAR_OF_NENGO.atCeiling()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(12), 30)));
    }

    @Test
    public void monthOfYearElement() {
        Nengo enkyo = Nengo.ofRelatedGregorianYear(1745);
        JapaneseCalendar jcal = JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(3), 30);
        assertThat(jcal.getMinimum(JapaneseCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(1)));
        assertThat(jcal.getMaximum(JapaneseCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(12).withLeap()));
        assertThat(jcal.get(JapaneseCalendar.MONTH_OF_YEAR), is(EastAsianMonth.valueOf(3)));
        assertThat(jcal.isValid(JapaneseCalendar.MONTH_OF_YEAR, EastAsianMonth.valueOf(12).withLeap()), is(true));
        assertThat(
            jcal.with(JapaneseCalendar.MONTH_OF_YEAR, EastAsianMonth.valueOf(12)),
            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(12), 30)));
        assertThat(JapaneseCalendar.MONTH_OF_YEAR.getDisplayName(Locale.ENGLISH), is("month"));
        assertThat(JapaneseCalendar.MONTH_OF_YEAR.getDisplayName(Locale.ROOT), is("MONTH_OF_YEAR"));
    }

    @Test
    public void monthAsOrdinalElement() {
//        assertThat(
//            jcal.with(JapaneseCalendar.MONTH_AS_ORDINAL.maximized()),
//            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(12).withLeap(), 29)));
//        assertThat(
//            jcal.with(JapaneseCalendar.MONTH_AS_ORDINAL.minimized()),
//            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(1), 30)));
//        assertThat(
//            jcal.with(JapaneseCalendar.MONTH_AS_ORDINAL.incremented()),
//            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(4), 29)));
//        assertThat(
//            jcal.with(JapaneseCalendar.MONTH_AS_ORDINAL.decremented()),
//            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(2), 30)));
//        assertThat(
//            jcal.with(JapaneseCalendar.MONTH_AS_ORDINAL.atFloor()),
//            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(3), 1)));
//        assertThat(
//            jcal.with(JapaneseCalendar.MONTH_AS_ORDINAL.atCeiling()),
//            is(JapaneseCalendar.of(enkyo, 2, EastAsianMonth.valueOf(3), 30)));
    }

    @Test
    public void dayOfMonthElement() {
        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        JapaneseCalendar jcal = JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 29);
        assertThat(jcal.getMinimum(JapaneseCalendar.DAY_OF_MONTH), is(1));
        assertThat(jcal.getMaximum(JapaneseCalendar.DAY_OF_MONTH), is(30));
        assertThat(jcal.getInt(JapaneseCalendar.DAY_OF_MONTH), is(29));
        assertThat(jcal.isValid(JapaneseCalendar.DAY_OF_MONTH, 30), is(true));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH, 6),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 6)));
        assertThat(JapaneseCalendar.DAY_OF_MONTH.getDisplayName(Locale.ENGLISH), is("day"));
        assertThat(JapaneseCalendar.DAY_OF_MONTH.getDisplayName(Locale.ROOT), is("DAY_OF_MONTH"));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH.maximized()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH.minimized()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 1)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH.incremented()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH.decremented()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 28)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH.atFloor()),
            is(jcal));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_MONTH.atCeiling()),
            is(jcal));
    }

    @Test
    public void dayOfYearElement() {
        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        JapaneseCalendar jcal = JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 29);
        assertThat(jcal.getMinimum(JapaneseCalendar.DAY_OF_YEAR), is(1));
        assertThat(jcal.getMaximum(JapaneseCalendar.DAY_OF_YEAR), is(354));
        assertThat(jcal.getInt(JapaneseCalendar.DAY_OF_YEAR), is(234));
        assertThat(jcal.isValid(JapaneseCalendar.DAY_OF_YEAR, 31), is(true));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR, 31),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(2), 2)));
        assertThat(JapaneseCalendar.DAY_OF_YEAR.getDisplayName(Locale.ENGLISH), is("DAY_OF_YEAR"));
        assertThat(JapaneseCalendar.DAY_OF_YEAR.getDisplayName(Locale.ROOT), is("DAY_OF_YEAR"));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR.maximized()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(12), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR.minimized()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(1), 1)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR.incremented()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR.decremented()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 28)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR.atFloor()),
            is(jcal));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_YEAR.atCeiling()),
            is(jcal));
    }

    @Test
    public void dayOfWeekElement() {
        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        JapaneseCalendar jcal = JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 29);
        assertThat(jcal.getMinimum(JapaneseCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(jcal.getMaximum(JapaneseCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(jcal.get(JapaneseCalendar.DAY_OF_WEEK), is(Weekday.TUESDAY));
        assertThat(jcal.isValid(JapaneseCalendar.DAY_OF_WEEK, Weekday.WEDNESDAY), is(true));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK, Weekday.THURSDAY),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(9), 1)));
        assertThat(JapaneseCalendar.DAY_OF_WEEK.getDisplayName(Locale.ENGLISH), is("day of the week"));
        assertThat(JapaneseCalendar.DAY_OF_WEEK.getDisplayName(Locale.ROOT), is("DAY_OF_WEEK"));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK.maximized()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(9), 3)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK.minimized()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 27)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK.incremented()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 30)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK.decremented()),
            is(JapaneseCalendar.of(ansei, 5, EastAsianMonth.valueOf(8), 28)));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK.atFloor()),
            is(jcal));
        assertThat(
            jcal.with(JapaneseCalendar.DAY_OF_WEEK.atCeiling()),
            is(jcal));
    }

    @Test
    public void formatGregorian() {
        ChronoFormatter<JapaneseCalendar> formatter =
            ChronoFormatter.ofPattern(
                "MMMM/dd, G y",
                PatternType.NON_ISO_DATE,
                Locale.ENGLISH,
                JapaneseCalendar.axis());
        JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 14);
        assertThat(formatter.format(jcal), is("April/14, Heisei 29"));
    }

    @Test
    public void formatLunisolar() {
        ChronoFormatter<JapaneseCalendar> formatter =
            ChronoFormatter.ofPattern(
                "Gy年M月d日",
                PatternType.NON_ISO_DATE,
                Locale.JAPANESE,
                JapaneseCalendar.axis());

        Nengo kaei = Nengo.ofRelatedGregorianYear(1848);
        JapaneseCalendar jcal = JapaneseCalendar.of(kaei, 7, EastAsianMonth.valueOf(7).withLeap(), 14);
        assertThat(formatter.format(jcal), is("嘉永7年閏7月14日"));

        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        JapaneseCalendar jcal2 = JapaneseCalendar.of(ansei, 1, EastAsianMonth.valueOf(7).withLeap(), 14, Leniency.LAX);
        assertThat(formatter.format(jcal2), is("安政1年閏7月14日"));

        assertThat(jcal.isSimultaneous(jcal2), is(true));
    }

    @Test
    public void parseGregorian() throws ParseException {
        ChronoFormatter<JapaneseCalendar> formatter =
            ChronoFormatter.ofPattern(
                "MMMM/dd, G y",
                PatternType.NON_ISO_DATE,
                Locale.ENGLISH,
                JapaneseCalendar.axis());
        JapaneseCalendar expected = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 14);
        assertThat(formatter.parse("April/14, Heisei 29"), is(expected));
    }

    @Test
    public void parseDayOfYear() throws ParseException {
        ChronoFormatter<JapaneseCalendar> formatter =
            ChronoFormatter.ofPattern(
                "DDD, G y",
                PatternType.NON_ISO_DATE,
                Locale.ENGLISH,
                JapaneseCalendar.axis());
        JapaneseCalendar expected = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 14);
        assertThat(formatter.parse("104, Heisei 29"), is(expected));
    }

    @Test
    public void parseLunisolar() throws ParseException {
        ChronoFormatter<JapaneseCalendar> formatter =
            ChronoFormatter.ofPattern(
                "Gy年M月d日",
                PatternType.NON_ISO_DATE,
                Locale.JAPANESE,
                JapaneseCalendar.axis()
            ).with(Leniency.LAX);

        Nengo kaei = Nengo.ofRelatedGregorianYear(1848);
        JapaneseCalendar jcal = JapaneseCalendar.of(kaei, 7, EastAsianMonth.valueOf(7).withLeap(), 14);
        assertThat(formatter.parse("嘉永7年閏7月14日"), is(jcal));

        Nengo ansei = Nengo.ofRelatedGregorianYear(1854);
        JapaneseCalendar jcal2 = JapaneseCalendar.of(ansei, 1, EastAsianMonth.valueOf(7).withLeap(), 14, Leniency.LAX);
        assertThat(formatter.parse("安政1年閏7月14日"), is(jcal2));
    }

    @Test
    public void commonElements() {
        JapaneseCalendar jcal = JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 1, 14);
        assertThat(jcal.getInt(CommonElements.RELATED_GREGORIAN_YEAR), is(2017));

        StdCalendarElement<Weekday, JapaneseCalendar> localDayOfWeek =
            CommonElements.localDayOfWeek(JapaneseCalendar.axis(), JapaneseCalendar.getDefaultWeekmodel());
        assertThat(jcal.get(localDayOfWeek), is(Weekday.SATURDAY));
        assertThat(localDayOfWeek.getDefaultMinimum(), is(Weekday.SUNDAY));
        assertThat(jcal.getMinimum(localDayOfWeek), is(Weekday.SUNDAY));

        StdCalendarElement<Integer, JapaneseCalendar> weekOfYear =
            CommonElements.weekOfYear(JapaneseCalendar.axis(), JapaneseCalendar.getDefaultWeekmodel());
        assertThat(jcal.get(weekOfYear), is(2));
        assertThat(jcal.plus(1, JapaneseCalendar.Unit.DAYS).get(weekOfYear), is(3));
    }

}