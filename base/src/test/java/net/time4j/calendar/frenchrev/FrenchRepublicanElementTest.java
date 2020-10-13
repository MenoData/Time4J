package net.time4j.calendar.frenchrev;

import net.time4j.Weekday;
import net.time4j.engine.ChronoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class FrenchRepublicanElementTest {

    @Test
    public void dayOfMonth() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.name(),
            is("DAY_OF_MONTH"));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.getType() == Integer.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.getSymbol(),
            is('D'));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.getDefaultMinimum(),
            is(1));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.getDefaultMaximum(),
            is(30));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_MONTH.getDisplayName(Locale.ENGLISH),
            is("day"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.getInt(FrenchRepublicanCalendar.DAY_OF_MONTH),
            is(18));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.DAY_OF_MONTH),
            is(1));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.DAY_OF_MONTH),
            is(30));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_MONTH),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_MONTH, 25),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH, 25),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 25)));

        // operators
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH.minimized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 1)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH.maximized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 30)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH.decremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 17)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH.incremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 19)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH.atFloor()),
            is(fcal));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_MONTH.atCeiling()),
            is(fcal));
    }

    @Test
    public void dayOfMonthOnSansculottides() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_MONTH),
            is(false));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_MONTH, 25),
            is(false));
        try {
            fcal.getMinimum(FrenchRepublicanCalendar.DAY_OF_MONTH);
            fail("Expected exception did not happen.");
        } catch (ChronoException ex) {
            // ok
        } catch (Throwable th) {
            fail("Unexpected exception type.");
        }
    }

    @Test
    public void dayOfDecade() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.name(),
            is("DAY_OF_DECADE"));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.getType() == DayOfDecade.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.getSymbol(),
            is('C'));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.getDefaultMinimum(),
            is(DayOfDecade.PRIMIDI));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.getDefaultMaximum(),
            is(DayOfDecade.DECADI));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_DECADE.getDisplayName(Locale.ENGLISH),
            is("day of decade"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.get(FrenchRepublicanCalendar.DAY_OF_DECADE),
            is(DayOfDecade.OCTIDI));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.DAY_OF_DECADE),
            is(DayOfDecade.PRIMIDI));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.DAY_OF_DECADE),
            is(DayOfDecade.DECADI));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_DECADE),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_DECADE, DayOfDecade.DUODI),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_DECADE, DayOfDecade.DUODI),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 12)));
    }

    @Test
    public void dayOfDecadeOnSansculottides() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_DECADE),
            is(false));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_DECADE, DayOfDecade.PRIMIDI),
            is(false));
        try {
            fcal.getMinimum(FrenchRepublicanCalendar.DAY_OF_DECADE);
            fail("Expected exception did not happen.");
        } catch (ChronoException ex) {
            // ok
        } catch (Throwable th) {
            fail("Unexpected exception type.");
        }
    }

    @Test
    public void dayOfWeek() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.name(),
            is("DAY_OF_WEEK"));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.getType() == Weekday.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.getSymbol(),
            is('E'));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.getDefaultMinimum(),
            is(Weekday.SUNDAY));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.getDefaultMaximum(),
            is(Weekday.SATURDAY));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_WEEK.getDisplayName(Locale.ENGLISH),
            is("day of the week"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.get(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SUNDAY));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.THURSDAY),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.THURSDAY),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 16)));

        // operators
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK.minimized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 12)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK.maximized()),
            is(fcal));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK.decremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 17)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK.incremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 19)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK.atFloor()),
            is(fcal));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_WEEK.atCeiling()),
            is(fcal));
    }

    @Test
    public void dayOfWeekOnSansculottides() {
        FrenchRepublicanCalendar fcal = // wednesday
            FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.SUNDAY),
            is(true));
    }

    @Test
    public void dayOfYear() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.name(),
            is("DAY_OF_YEAR"));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.getType() == Integer.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.getSymbol(),
            is('\u0000'));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.getDefaultMinimum(),
            is(1));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.getDefaultMaximum(),
            is(365));
        assertThat(
            FrenchRepublicanCalendar.DAY_OF_YEAR.getDisplayName(Locale.ENGLISH),
            is("DAY_OF_YEAR"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.getInt(FrenchRepublicanCalendar.DAY_OF_YEAR),
            is(48));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.DAY_OF_YEAR),
            is(1));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.DAY_OF_YEAR),
            is(365));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_YEAR),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_YEAR, 62),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR, 62),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FRIMAIRE, 2)));

        // operators
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR.minimized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.VENDEMIAIRE, 1)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR.maximized()),
            is(FrenchRepublicanCalendar.of(8, Sansculottides.COMPLEMENTARY_DAY_5)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR.decremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 17)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR.incremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 19)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR.atFloor()),
            is(fcal));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DAY_OF_YEAR.atCeiling()),
            is(fcal));
    }

    @Test
    public void dayOfYearOnSansculottides() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DAY_OF_YEAR),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_YEAR, 365),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DAY_OF_YEAR, 366),
            is(false));
    }

    @Test
    public void decadeOfMonth() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.name(),
            is("DECADE_OF_MONTH"));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.getType() == Integer.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.getSymbol(),
            is('\u0000'));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.getDefaultMinimum(),
            is(1));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.getDefaultMaximum(),
            is(3));
        assertThat(
            FrenchRepublicanCalendar.DECADE_OF_MONTH.getDisplayName(Locale.ENGLISH),
            is("DECADE_OF_MONTH"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.getInt(FrenchRepublicanCalendar.DECADE_OF_MONTH),
            is(2));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.DECADE_OF_MONTH),
            is(1));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.DECADE_OF_MONTH),
            is(3));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DECADE_OF_MONTH),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DECADE_OF_MONTH, 3),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH, 3),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 28)));

        // operators
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH.minimized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 8)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH.maximized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 28)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH.decremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 8)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH.incremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 28)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH.atFloor()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 11)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.DECADE_OF_MONTH.atCeiling()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 20)));
    }

    @Test
    public void decadeOnSansculottides() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.DECADE_OF_MONTH),
            is(false));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.DECADE_OF_MONTH, 2),
            is(false));
        try {
            fcal.getMinimum(FrenchRepublicanCalendar.DECADE_OF_MONTH);
            fail("Expected exception did not happen.");
        } catch (ChronoException ex) {
            // ok
        } catch (Throwable th) {
            fail("Unexpected exception type.");
        }
    }

    @Test
    public void monthOfYear() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.name(),
            is("MONTH_OF_YEAR"));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.getType() == FrenchRepublicanMonth.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.getSymbol(),
            is('M'));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.getDefaultMinimum(),
            is(FrenchRepublicanMonth.VENDEMIAIRE));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.getDefaultMaximum(),
            is(FrenchRepublicanMonth.FRUCTIDOR));
        assertThat(
            FrenchRepublicanCalendar.MONTH_OF_YEAR.getDisplayName(Locale.ENGLISH),
            is("month"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.get(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(FrenchRepublicanMonth.BRUMAIRE));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(FrenchRepublicanMonth.VENDEMIAIRE));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(FrenchRepublicanMonth.FRUCTIDOR));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.MONTH_OF_YEAR, FrenchRepublicanMonth.FLOREAL),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR, FrenchRepublicanMonth.FLOREAL),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FLOREAL, 18)));

        // operators
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR.minimized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.VENDEMIAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR.maximized()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FRUCTIDOR, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR.decremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.VENDEMIAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR.incremented()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.FRIMAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR.atFloor()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 1)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.MONTH_OF_YEAR.atCeiling()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 30)));
    }

    @Test
    public void monthOnSansculottides() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(false));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.MONTH_OF_YEAR, FrenchRepublicanMonth.FLOREAL),
            is(true));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(FrenchRepublicanMonth.VENDEMIAIRE));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.MONTH_OF_YEAR),
            is(FrenchRepublicanMonth.FRUCTIDOR));
    }

    @Test
    public void era() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.ERA.name(),
            is("ERA"));
        assertThat(
            FrenchRepublicanCalendar.ERA.getType() == FrenchRepublicanEra.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.ERA.getSymbol(),
            is('G'));
        assertThat(
            FrenchRepublicanCalendar.ERA.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.ERA.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.ERA.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.ERA.getDefaultMinimum(),
            is(FrenchRepublicanEra.REPUBLICAN));
        assertThat(
            FrenchRepublicanCalendar.ERA.getDefaultMaximum(),
            is(FrenchRepublicanEra.REPUBLICAN));
        assertThat(
            FrenchRepublicanCalendar.ERA.getDisplayName(Locale.ENGLISH),
            is("era"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.get(FrenchRepublicanCalendar.ERA),
            is(FrenchRepublicanEra.REPUBLICAN));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.ERA),
            is(FrenchRepublicanEra.REPUBLICAN));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.ERA),
            is(FrenchRepublicanEra.REPUBLICAN));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.ERA),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.ERA, FrenchRepublicanEra.REPUBLICAN),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.ERA, FrenchRepublicanEra.REPUBLICAN),
            is(fcal));
    }

    @Test
    public void yearOfEra() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.name(),
            is("YEAR_OF_ERA"));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.getType() == Integer.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.getSymbol(),
            is('Y'));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.getDefaultMinimum(),
            is(1));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.getDefaultMaximum(),
            is(1202));
        assertThat(
            FrenchRepublicanCalendar.YEAR_OF_ERA.getDisplayName(Locale.ENGLISH),
            is("year"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.getInt(FrenchRepublicanCalendar.YEAR_OF_ERA),
            is(8));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.YEAR_OF_ERA),
            is(1));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.YEAR_OF_ERA),
            is(1202));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.YEAR_OF_ERA),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.YEAR_OF_ERA, 79),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA, 79),
            is(FrenchRepublicanCalendar.of(79, FrenchRepublicanMonth.BRUMAIRE, 18)));

        // operators
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA.minimized()),
            is(FrenchRepublicanCalendar.of(1, FrenchRepublicanMonth.BRUMAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA.maximized()),
            is(FrenchRepublicanCalendar.of(1202, FrenchRepublicanMonth.BRUMAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA.decremented()),
            is(FrenchRepublicanCalendar.of(7, FrenchRepublicanMonth.BRUMAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA.incremented()),
            is(FrenchRepublicanCalendar.of(9, FrenchRepublicanMonth.BRUMAIRE, 18)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA.atFloor()),
            is(FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.VENDEMIAIRE, 1)));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.YEAR_OF_ERA.atCeiling()),
            is(FrenchRepublicanCalendar.of(8, Sansculottides.COMPLEMENTARY_DAY_5)));
    }

    @Test
    public void yearOnSansculottides() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(1, Sansculottides.COMPLEMENTARY_DAY_2);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.YEAR_OF_ERA),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.YEAR_OF_ERA, 1202),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.YEAR_OF_ERA, 1203),
            is(false));
    }

    @Test
    public void sansculottides() {
        // ChronoElement-interface
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.name(),
            is("SANSCULOTTIDES"));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.getType() == Sansculottides.class,
            is(true));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.getSymbol(),
            is('S'));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.isDateElement(),
            is(true));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.isTimeElement(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.isLenient(),
            is(false));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.getDefaultMinimum(),
            is(Sansculottides.COMPLEMENTARY_DAY_1));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.getDefaultMaximum(),
            is(Sansculottides.COMPLEMENTARY_DAY_5));
        assertThat(
            FrenchRepublicanCalendar.SANSCULOTTIDES.getDisplayName(Locale.GERMAN),
            is("Sansculottiden"));

        // values
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(11, Sansculottides.COMPLEMENTARY_DAY_3);
        assertThat(
            fcal.get(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(Sansculottides.COMPLEMENTARY_DAY_3));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(Sansculottides.COMPLEMENTARY_DAY_1));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(Sansculottides.LEAP_DAY));
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(true));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.SANSCULOTTIDES, Sansculottides.LEAP_DAY),
            is(true));
        assertThat(
            fcal.with(FrenchRepublicanCalendar.SANSCULOTTIDES, Sansculottides.LEAP_DAY),
            is(FrenchRepublicanCalendar.of(11, Sansculottides.LEAP_DAY)));
    }

    @Test
    public void sansculottidesOnNormalDays() {
        FrenchRepublicanCalendar fcal = FrenchRepublicanCalendar.of(8, FrenchRepublicanMonth.BRUMAIRE, 18);
        assertThat(
            fcal.contains(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(false));
        assertThat(
            fcal.isValid(FrenchRepublicanCalendar.SANSCULOTTIDES, Sansculottides.COMPLEMENTARY_DAY_2),
            is(true));
        assertThat(
            fcal.getMinimum(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(Sansculottides.COMPLEMENTARY_DAY_1));
        assertThat(
            fcal.getMaximum(FrenchRepublicanCalendar.SANSCULOTTIDES),
            is(Sansculottides.COMPLEMENTARY_DAY_5));
    }

    @Test
    public void min() {
        FrenchRepublicanCalendar min = FrenchRepublicanCalendar.axis().getMinimum(); // Saturday
        assertThat(
            min,
            is(FrenchRepublicanCalendar.of(1, FrenchRepublicanMonth.VENDEMIAIRE, 1)));
        assertThat(
            min.getMinimum(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            min.getMaximum(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            min.isValid(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.SATURDAY),
            is(true));
        assertThat(
            min.isValid(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.FRIDAY),
            is(false));
    }

    @Test
    public void max() {
        FrenchRepublicanCalendar max = FrenchRepublicanCalendar.axis().getMaximum(); // Sunday
        assertThat(
            max, // 2994-09-21
            is(FrenchRepublicanCalendar.of(1202, Sansculottides.LEAP_DAY)));
        assertThat(
            max.getMinimum(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SUNDAY));
        assertThat(
            max.getMaximum(FrenchRepublicanCalendar.DAY_OF_WEEK),
            is(Weekday.SUNDAY));
        assertThat(
            max.isValid(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.SUNDAY),
            is(true));
        assertThat(
            max.isValid(FrenchRepublicanCalendar.DAY_OF_WEEK, Weekday.MONDAY),
            is(false));
    }

}
