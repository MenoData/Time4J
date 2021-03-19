package net.time4j.calendar;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarSystem;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import net.time4j.history.YearDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class HistoricCalendarTest {

    @Test
    public void formatPattern() {
        String pattern = HistoricCalendar.family().getFormatPattern(FormatStyle.FULL, Locale.GERMAN);
        assertThat(pattern, is("EEEE, d. MMMM y G"));
        pattern = HistoricCalendar.family().getFormatPattern(FormatStyle.FULL, Locale.FRENCH);
        assertThat(pattern, is("EEEE d MMMM y G"));
        pattern = HistoricCalendar.family().getFormatPattern(FormatStyle.FULL, Locale.ENGLISH);
        assertThat(pattern, is("EEEE, MMMM d, y G"));
    }

    @Test
    public void rome() {
        ChronoHistory history = ChronoHistory.ofFirstGregorianReform();
        HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 17);
        assertThat(cal.getHistory(), is(history));
        assertThat(cal.getVariant(), is(history.getVariant()));

        assertThat(cal.getEra(), is(HistoricEra.AD));
        assertThat(cal.getCentury(), is(16));
        assertThat(cal.getYear(), is(1582));
        assertThat(cal.getMonth(), is(Month.OCTOBER));
        assertThat(cal.getDayOfMonth(), is(17));
        assertThat(cal.getDayOfYear(), is(280));
        assertThat(cal.getDayOfWeek(), is(Weekday.SUNDAY));

        assertThat(cal.lengthOfMonth(), is(21));
        assertThat(cal.lengthOfYear(), is(355));

        assertThat(cal.get(HistoricCalendar.ERA), is(HistoricEra.AD));
        assertThat(cal.get(HistoricCalendar.CENTURY_OF_ERA), is(16));
        assertThat(cal.get(HistoricCalendar.RELATED_STANDARD_YEAR), is(1582));
        assertThat(cal.get(HistoricCalendar.MONTH_OF_YEAR), is(Month.OCTOBER));
        assertThat(cal.get(HistoricCalendar.DAY_OF_MONTH), is(17));
        assertThat(cal.get(HistoricCalendar.DAY_OF_YEAR), is(280));
        assertThat(cal.get(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal.get(HistoricCalendar.WEEKDAY_IN_MONTH), is(1));

        assertThat(cal.getMinimum(HistoricCalendar.ERA), is(HistoricEra.BC));
        assertThat(cal.getMinimum(HistoricCalendar.CENTURY_OF_ERA), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.RELATED_STANDARD_YEAR), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.MONTH_OF_YEAR), is(Month.JANUARY));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_MONTH), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_YEAR), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal.getMinimum(HistoricCalendar.WEEKDAY_IN_MONTH), is(1));

        assertThat(cal.getMaximum(HistoricCalendar.ERA), is(HistoricEra.AD));
        assertThat(cal.getMaximum(HistoricCalendar.CENTURY_OF_ERA), is(100));
        assertThat(cal.getMaximum(HistoricCalendar.RELATED_STANDARD_YEAR), is(9999));
        assertThat(cal.getMaximum(HistoricCalendar.MONTH_OF_YEAR), is(Month.DECEMBER));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_MONTH), is(31));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_YEAR), is(355));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(cal.getMaximum(HistoricCalendar.WEEKDAY_IN_MONTH), is(3));

        assertThat(
            cal.with(HistoricCalendar.WEEKDAY_IN_MONTH.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 31)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 1)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 1, 1)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 31)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 12, 31)));

        cal = HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 4);
        assertThat(cal.nextDay(), is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 15)));
        assertThat(
            PlainDate.of(1582, 10, 5).transform(HistoricCalendar.family(), history),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 9, 25)));

        cal = HistoricCalendar.of(history, HistoricEra.AD, 1582, 9, 10);
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.incremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1582, 10, 4)));
    }

    @Test
    public void deathOfQueenElizabethI() {
        ChronoHistory history = ChronoHistory.of(Locale.UK);
        HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 24);
        assertThat(cal.getHistory(), is(history));
        assertThat(cal.getVariant(), is(history.getVariant()));

        assertThat(cal.getEra(), is(HistoricEra.AD));
        assertThat(cal.getCentury(), is(17));
        assertThat(cal.getYear(), is(1602));
        assertThat(cal.getMonth(), is(Month.MARCH));
        assertThat(cal.getDayOfMonth(), is(24));
        assertThat(cal.getDayOfYear(), is(365));
        assertThat(cal.getDayOfWeek(), is(Weekday.THURSDAY));

        assertThat(cal.lengthOfMonth(), is(31));
        assertThat(cal.lengthOfYear(), is(365));

        assertThat(cal.get(HistoricCalendar.ERA), is(HistoricEra.AD));
        assertThat(cal.get(HistoricCalendar.CENTURY_OF_ERA), is(17));
        assertThat(cal.get(HistoricCalendar.RELATED_STANDARD_YEAR), is(1603));
        assertThat(cal.get(HistoricCalendar.MONTH_OF_YEAR), is(Month.MARCH));
        assertThat(cal.get(HistoricCalendar.DAY_OF_MONTH), is(24));
        assertThat(cal.get(HistoricCalendar.DAY_OF_YEAR), is(365));
        assertThat(cal.get(HistoricCalendar.DAY_OF_WEEK), is(Weekday.THURSDAY));
        assertThat(cal.get(HistoricCalendar.WEEKDAY_IN_MONTH), is(4));

        assertThat(cal.getMinimum(HistoricCalendar.ERA), is(HistoricEra.BC));
        assertThat(cal.getMinimum(HistoricCalendar.CENTURY_OF_ERA), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.RELATED_STANDARD_YEAR), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.MONTH_OF_YEAR), is(Month.JANUARY));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_MONTH), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_YEAR), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal.getMinimum(HistoricCalendar.WEEKDAY_IN_MONTH), is(1));

        assertThat(cal.getMaximum(HistoricCalendar.ERA), is(HistoricEra.AD));
        assertThat(cal.getMaximum(HistoricCalendar.CENTURY_OF_ERA), is(100));
        assertThat(cal.getMaximum(HistoricCalendar.RELATED_STANDARD_YEAR), is(9999));
        assertThat(cal.getMaximum(HistoricCalendar.MONTH_OF_YEAR), is(Month.DECEMBER));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_MONTH), is(31));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_YEAR), is(365));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(cal.getMaximum(HistoricCalendar.WEEKDAY_IN_MONTH), is(5));

        assertThat(
            cal.withNewYear(),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1602, YearDefinition.DUAL_DATING, 3, 25)));
        assertThat(
            cal.withNewYear(),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1602, YearDefinition.AFTER_NEW_YEAR, 3, 25)));

        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 1)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1602, 3, 25)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 31)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 24)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.decremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 23)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.incremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1603, 3, 25)));

        ChronoFormatter<HistoricCalendar> f =
            ChronoFormatter
                .ofStyle(FormatStyle.FULL, Locale.ENGLISH, HistoricCalendar.family())
                .with(history);
        String text = "Thursday, March 24, 1602/03 AD";
        try {
            assertThat(f.format(cal), is(text));
            assertThat(f.parse(text), is(cal));
        } catch (ParseException pe) {
            fail(pe.getMessage());
        }
    }

    @Test
    public void preussen() {
        ChronoHistory history = ChronoHistory.of(new Locale("de", "DE", "PREUSSEN"));
        HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 4);

        assertThat(cal.getEra(), is(HistoricEra.AD));
        assertThat(cal.getCentury(), is(17));
        assertThat(cal.getYear(), is(1610));
        assertThat(cal.getMonth(), is(Month.SEPTEMBER));
        assertThat(cal.getDayOfMonth(), is(4));
        assertThat(cal.getDayOfYear(), is(237));
        assertThat(cal.getDayOfWeek(), is(Weekday.SATURDAY));

        assertThat(cal.lengthOfMonth(), is(29));
        assertThat(cal.lengthOfYear(), is(355));

        assertThat(cal.get(HistoricCalendar.ERA), is(HistoricEra.AD));
        assertThat(cal.get(HistoricCalendar.CENTURY_OF_ERA), is(17));
        assertThat(cal.get(HistoricCalendar.RELATED_STANDARD_YEAR), is(1610));
        assertThat(cal.get(HistoricCalendar.MONTH_OF_YEAR), is(Month.SEPTEMBER));
        assertThat(cal.get(HistoricCalendar.DAY_OF_MONTH), is(4));
        assertThat(cal.get(HistoricCalendar.DAY_OF_YEAR), is(237));
        assertThat(cal.get(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(cal.get(HistoricCalendar.WEEKDAY_IN_MONTH), is(1));

        assertThat(cal.getMinimum(HistoricCalendar.ERA), is(HistoricEra.BC));
        assertThat(cal.getMinimum(HistoricCalendar.CENTURY_OF_ERA), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.RELATED_STANDARD_YEAR), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.MONTH_OF_YEAR), is(Month.JANUARY));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_MONTH), is(2));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_YEAR), is(1));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SUNDAY));
        assertThat(cal.getMinimum(HistoricCalendar.WEEKDAY_IN_MONTH), is(1));

        assertThat(cal.getMaximum(HistoricCalendar.ERA), is(HistoricEra.AD));
        assertThat(cal.getMaximum(HistoricCalendar.CENTURY_OF_ERA), is(100));
        assertThat(cal.getMaximum(HistoricCalendar.RELATED_STANDARD_YEAR), is(9999));
        assertThat(cal.getMaximum(HistoricCalendar.MONTH_OF_YEAR), is(Month.DECEMBER));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_MONTH), is(30));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_YEAR), is(355));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_WEEK), is(Weekday.SATURDAY));
        assertThat(cal.getMaximum(HistoricCalendar.WEEKDAY_IN_MONTH), is(4));

        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 1, 4)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 12, 4)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.atFloor()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 2)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.atCeiling()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 30)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 2)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 1, 1)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 30)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 12, 31)));

        cal = cal.minus(CalendarDays.of(5)); // before cutover (AD-1610-09-02)
        assertThat(cal.lengthOfMonth(), is(22));
        assertThat(cal.get(HistoricCalendar.MONTH_OF_YEAR), is(Month.AUGUST));
        assertThat(cal.get(HistoricCalendar.DAY_OF_MONTH), is(20));
        assertThat(cal.getMinimum(HistoricCalendar.DAY_OF_MONTH), is(1));
        assertThat(cal.getMaximum(HistoricCalendar.DAY_OF_MONTH), is(22));
        assertThat(cal.getMaximum(HistoricCalendar.WEEKDAY_IN_MONTH), is(3));

        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.atFloor()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 1)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.atCeiling()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 22)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.decremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 7, 20)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.incremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 20)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 1)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.decremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 19)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.decremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 19)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.incremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 21)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.incremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 21)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.minimized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 1, 1)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 22)));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_YEAR.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 12, 31)));

        cal = HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 22);
        assertThat(cal.nextDay(), is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 2)));

        cal = HistoricCalendar.of(history, HistoricEra.AD, 1610, 8, 1);
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.incremented()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1610, 9, 2)));
    }

    @Test
    public void sweden() {
        ChronoHistory history = ChronoHistory.ofSweden();
        HistoricCalendar cal = HistoricCalendar.of(history, HistoricEra.AD, 1712, 2, 15);
        assertThat(
            cal.getMaximum(HistoricCalendar.DAY_OF_MONTH),
            is(30));
        assertThat(
            cal.with(HistoricCalendar.DAY_OF_MONTH.maximized()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1712, 2, 30)));
        assertThat(
            cal.with(HistoricCalendar.MONTH_OF_YEAR.atCeiling()),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1712, 2, 30)));

        cal = HistoricCalendar.of(history, HistoricEra.AD, 1712, 3, 1);
        assertThat(
            cal.minus(CalendarDays.ONE),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1712, 2, 30)));
        cal = HistoricCalendar.of(history, HistoricEra.AD, 1712, 2, 29);
        assertThat(
            cal.plus(CalendarDays.ONE),
            is(HistoricCalendar.of(history, HistoricEra.AD, 1712, 2, 30)));
        assertThat(cal.lengthOfMonth(), is(30));
        assertThat(cal.lengthOfYear(), is(367));
    }

    @Test
    public void russia() {
        ChronoHistory history = ChronoHistory.of(new Locale("ru", "RU"));
        PlainDate gregorian = PlainDate.of(1700, 1, 1);
        HistoricCalendar cal = gregorian.transform(HistoricCalendar.family(), history);
        assertThat(cal.getEra(), is(HistoricEra.BYZANTINE));
        assertThat(cal.getYear(), is(7208));
        assertThat(cal.getInt(HistoricCalendar.RELATED_STANDARD_YEAR), is(7207));
        assertThat(cal.getMonth(), is(Month.DECEMBER));
        assertThat(cal.getDayOfMonth(), is(22));
    }

    @Test
    public void prolepticByzantine() {
        ChronoHistory h = ChronoHistory.PROLEPTIC_BYZANTINE;
        CalendarSystem<HistoricCalendar> cs =
            HistoricCalendar.family().getCalendarSystem(h);
        HistoricCalendar min = cs.transform(cs.getMinimumSinceUTC());
        HistoricCalendar max = cs.transform(cs.getMaximumSinceUTC());
        System.out.println(min); // BYZANTINE-0000-09-01
        System.out.println(max); // BYZANTINE-999984973-08-31

        assertThat(min.getEra(), is(HistoricEra.BYZANTINE));
        assertThat(min.getYear(), is(1));
        assertThat(min.getMonth().getValue(), is(9));
        assertThat(min.getDayOfMonth(), is(1));
        assertThat(min.getDayOfYear(), is(1));

        assertThat(max.getEra(), is(HistoricEra.BYZANTINE));
        assertThat(max.getYear(), is(999984973));
        assertThat(max.getMonth().getValue(), is(8));
        assertThat(max.getDayOfMonth(), is(31));
        assertThat(max.getDayOfYear(), is(365));

        assertThat(h.isValid(max.get(h.date())), is(true));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.BYZANTINE, 999_984_973, 8, 31)), is(true));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.BYZANTINE, 999_984_974, 9, 1)), is(false));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.BYZANTINE, 0, 9, 1)), is(true));

        assertThat(min.getMaximum(h.yearOfEra()), is(999984973));
        assertThat(max.getMaximum(h.yearOfEra()), is(999984973));
    }

    @Test
    public void prolepticJulian() {
        ChronoHistory h = ChronoHistory.PROLEPTIC_JULIAN;
        CalendarSystem<HistoricCalendar> cs =
            HistoricCalendar.family().getCalendarSystem(h);
        HistoricCalendar min = cs.transform(cs.getMinimumSinceUTC());
        HistoricCalendar max = cs.transform(cs.getMaximumSinceUTC());
        System.out.println(min); // BC-999979466-01-01
        System.out.println(max); // AD-999979465-12-31

        assertThat(min.getEra(), is(HistoricEra.BC));
        assertThat(min.getYear(), is(999_979_466));
        assertThat(min.getMonth().getValue(), is(1));
        assertThat(min.getDayOfMonth(), is(1));
        assertThat(min.getDayOfYear(), is(1));

        assertThat(max.getEra(), is(HistoricEra.AD));
        assertThat(max.getYear(), is(999_979_465));
        assertThat(max.getMonth().getValue(), is(12));
        assertThat(max.getDayOfMonth(), is(31));
        assertThat(max.getDayOfYear(), is(365));

        assertThat(h.isValid(max.get(h.date())), is(true));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.AD, 999_979_465, 12, 31)), is(true));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.AD, 999_979_466, 1, 1)), is(false));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.BC, 999_979_467, 12, 31)), is(false));
        assertThat(h.isValid(HistoricDate.of(HistoricEra.BC, 999_979_466, 1, 1)), is(true));

        assertThat(min.getMaximum(h.yearOfEra()), is(999_979_466));
        assertThat(max.getMaximum(h.yearOfEra()), is(999_979_465));
    }

    @Test
    public void transformMin() {
        ChronoHistory[] histories = {
            ChronoHistory.PROLEPTIC_BYZANTINE,
            ChronoHistory.of(new Locale("", "ES")), // Spain
            ChronoHistory.of(new Locale("", "PT")) // Portugal
        };
        for (ChronoHistory history : histories) {
            CalendarSystem<HistoricCalendar> calsys =
                HistoricCalendar.family().getCalendarSystem(history.getVariant());
            try {
                calsys.transform(calsys.getMinimumSinceUTC());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }

        ChronoHistory history = ChronoHistory.of(new Locale("", "IT", ""));
        CalendarSystem<HistoricCalendar> calsys = HistoricCalendar.family().getCalendarSystem(history.getVariant());
        HistoricCalendar date = calsys.transform(calsys.getMinimumSinceUTC());
        System.out.println(date); // BC-0045-01-01
        assertThat(date.getDayOfYear(), is(1));
        assertThat(date.lengthOfMonth(), is(31));
        assertThat(date.lengthOfYear(), is(366));
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("de-DE-PREUSSEN-u-ca-historic");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(1610, 9, 1)),
            is("Mittwoch, 22. August 1610 n. Chr."));
    }

}