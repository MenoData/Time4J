package net.time4j.calendar.bahai;

import net.time4j.Weekday;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BadiElementTest {

    @Test
    public void dayOfDivision() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.name(),
            is("DAY_OF_DIVISION"));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.getType() == Integer.class,
            is(true));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.getSymbol(),
            is('D'));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.getDefaultMinimum(),
            is(1));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.getDefaultMaximum(),
            is(19));
        assertThat(
            BadiCalendar.DAY_OF_DIVISION.getDisplayName(Locale.ENGLISH),
            is("day"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 8, BadiMonth.BAHA, 12);
        assertThat(
            bcal.getInt(BadiCalendar.DAY_OF_DIVISION),
            is(12));
        assertThat(
            bcal.getMinimum(BadiCalendar.DAY_OF_DIVISION),
            is(1));
        assertThat(
            bcal.getMaximum(BadiCalendar.DAY_OF_DIVISION),
            is(19));
        assertThat(
            bcal.contains(BadiCalendar.DAY_OF_DIVISION),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_DIVISION, 18),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION, 18),
            is(BadiCalendar.of(5, 8, BadiMonth.BAHA, 18)));

        // operators
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION.minimized()),
            is(BadiCalendar.of(5, 8, BadiMonth.BAHA, 1)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION.maximized()),
            is(BadiCalendar.of(5, 8, BadiMonth.BAHA, 19)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION.decremented()),
            is(BadiCalendar.of(5, 8, BadiMonth.BAHA, 11)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION.incremented()),
            is(BadiCalendar.of(5, 8, BadiMonth.BAHA, 13)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION.atFloor()),
            is(bcal));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_DIVISION.atCeiling()),
            is(bcal));
    }

    @Test
    public void dayOfDivisionOnIntercalary() {
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 11, 2);
        assertThat(
            bcal.contains(BadiCalendar.DAY_OF_DIVISION),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_DIVISION, 6),
            is(false));
        assertThat(
            bcal.getMinimum(BadiCalendar.DAY_OF_DIVISION),
            is(Integer.valueOf(1)));
        assertThat(
            bcal.getMaximum(BadiCalendar.DAY_OF_DIVISION),
            is(Integer.valueOf(4)));
    }

    @Test
    public void dayOfWeek() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.DAY_OF_WEEK.name(),
            is("DAY_OF_WEEK"));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.getType() == Weekday.class,
            is(true));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.getSymbol(),
            is('E'));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.getDefaultMinimum(),
            is(Weekday.SATURDAY));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.getDefaultMaximum(),
            is(Weekday.FRIDAY));
        assertThat(
            BadiCalendar.DAY_OF_WEEK.getDisplayName(Locale.ENGLISH),
            is("day of the week"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.get(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.MONDAY));
        assertThat(
            bcal.getMinimum(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            bcal.getMaximum(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.FRIDAY));
        assertThat(
            bcal.contains(BadiCalendar.DAY_OF_WEEK),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_WEEK, Weekday.THURSDAY),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK, Weekday.THURSDAY),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 16)));

        // operators
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK.minimized()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 11)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK.maximized()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 17)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK.decremented()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 12)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK.incremented()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 14)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK.atFloor()),
            is(bcal));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_WEEK.atCeiling()),
            is(bcal));
    }

    @Test
    public void dayOfWeekOnIntercalary() {
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 11, 2);
        assertThat(
            bcal.contains(BadiCalendar.DAY_OF_WEEK),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_WEEK, Weekday.SUNDAY),
            is(true));
    }

    @Test
    public void dayOfYear() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.DAY_OF_YEAR.name(),
            is("DAY_OF_YEAR"));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.getType() == Integer.class,
            is(true));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.getSymbol(),
            is('\u0000'));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.getDefaultMinimum(),
            is(1));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.getDefaultMaximum(),
            is(365));
        assertThat(
            BadiCalendar.DAY_OF_YEAR.getDisplayName(Locale.ENGLISH),
            is("DAY_OF_YEAR"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.getInt(BadiCalendar.DAY_OF_YEAR),
            is(32));
        assertThat(
            bcal.getMinimum(BadiCalendar.DAY_OF_YEAR),
            is(1));
        assertThat(
            bcal.getMaximum(BadiCalendar.DAY_OF_YEAR),
            is(365));
        assertThat(
            bcal.contains(BadiCalendar.DAY_OF_YEAR),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_YEAR, 62),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR, 62),
            is(BadiCalendar.of(5, 11, BadiMonth.AZAMAT, 5)));

        // operators
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR.minimized()),
            is(BadiCalendar.of(5, 11, BadiMonth.BAHA, 1)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR.maximized()),
            is(BadiCalendar.of(5, 11, BadiMonth.ALA, 19)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR.decremented()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 12)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR.incremented()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 14)));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR.atFloor()),
            is(bcal));
        assertThat(
            bcal.with(BadiCalendar.DAY_OF_YEAR.atCeiling()),
            is(bcal));
    }

    @Test
    public void dayOfYearOnIntercalary() {
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 11, 2);
        assertThat(
            bcal.contains(BadiCalendar.DAY_OF_YEAR),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_YEAR, 365),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.DAY_OF_YEAR, 366),
            is(false));
    }

    @Test
    public void monthOfYear() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.name(),
            is("MONTH_OF_YEAR"));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.getType() == BadiMonth.class,
            is(true));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.getSymbol(),
            is('M'));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.getDefaultMinimum(),
            is(BadiMonth.BAHA));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.getDefaultMaximum(),
            is(BadiMonth.ALA));
        assertThat(
            BadiCalendar.MONTH_OF_YEAR.getDisplayName(Locale.ENGLISH),
            is("month"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.get(BadiCalendar.MONTH_OF_YEAR),
            is(BadiMonth.JALAL));
        assertThat(
            bcal.getMinimum(BadiCalendar.MONTH_OF_YEAR),
            is(BadiMonth.BAHA));
        assertThat(
            bcal.getMaximum(BadiCalendar.MONTH_OF_YEAR),
            is(BadiMonth.ALA));
        assertThat(
            bcal.contains(BadiCalendar.MONTH_OF_YEAR),
            is(true));
        assertThat(
            bcal.hasMonth(),
            is(true));
        assertThat(
            bcal.isIntercalaryDay(),
            is(false));
        assertThat(
            bcal.isValid(BadiCalendar.MONTH_OF_YEAR, BadiMonth.MULK),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR, BadiMonth.MULK),
            is(BadiCalendar.of(5, 11, BadiMonth.MULK, 13)));

        // operators
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR.minimized()),
            is(BadiCalendar.of(5, 11, BadiMonth.BAHA, 13)));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR.maximized()),
            is(BadiCalendar.of(5, 11, BadiMonth.ALA, 13)));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR.decremented()),
            is(BadiCalendar.of(5, 11, BadiMonth.BAHA, 13)));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR.incremented()),
            is(BadiCalendar.of(5, 11, BadiMonth.JAMAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR.atFloor()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 1)));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR.atCeiling()),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 19)));
    }

    @Test
    public void monthOnIntercalary() {
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 11, 2);
        assertThat(
            bcal.contains(BadiCalendar.MONTH_OF_YEAR),
            is(false));
        assertThat(
            bcal.hasMonth(),
            is(false));
        assertThat(
            bcal.isIntercalaryDay(),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.MONTH_OF_YEAR, BadiMonth.MULK),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.MONTH_OF_YEAR, BadiMonth.MULK),
            is(BadiCalendar.of(5, 11, BadiMonth.MULK, 19)));
        assertThat(
            bcal.getMinimum(BadiCalendar.MONTH_OF_YEAR),
            is(BadiMonth.BAHA));
        assertThat(
            bcal.getMaximum(BadiCalendar.MONTH_OF_YEAR),
            is(BadiMonth.ALA));
    }

    @Test
    public void era() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.ERA.name(),
            is("ERA"));
        assertThat(
            BadiCalendar.ERA.getType() == BadiEra.class,
            is(true));
        assertThat(
            BadiCalendar.ERA.getSymbol(),
            is('G'));
        assertThat(
            BadiCalendar.ERA.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.ERA.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.ERA.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.ERA.getDefaultMinimum(),
            is(BadiEra.BAHAI));
        assertThat(
            BadiCalendar.ERA.getDefaultMaximum(),
            is(BadiEra.BAHAI));
        assertThat(
            BadiCalendar.ERA.getDisplayName(Locale.ENGLISH),
            is("era"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.get(BadiCalendar.ERA),
            is(BadiEra.BAHAI));
        assertThat(
            bcal.getMinimum(BadiCalendar.ERA),
            is(BadiEra.BAHAI));
        assertThat(
            bcal.getMaximum(BadiCalendar.ERA),
            is(BadiEra.BAHAI));
        assertThat(
            bcal.contains(BadiCalendar.ERA),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.ERA, BadiEra.BAHAI),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.ERA, BadiEra.BAHAI),
            is(bcal));
    }

    @Test
    public void yearOfEra() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.YEAR_OF_ERA.name(),
            is("YEAR_OF_ERA"));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.getType() == Integer.class,
            is(true));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.getSymbol(),
            is('Y'));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.getDefaultMinimum(),
            is(1));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.getDefaultMaximum(),
            is(3 * 361));
        assertThat(
            BadiCalendar.YEAR_OF_ERA.getDisplayName(Locale.ENGLISH),
            is("year"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.getInt(BadiCalendar.YEAR_OF_ERA),
            is(87));
        assertThat(
            bcal.getMinimum(BadiCalendar.YEAR_OF_ERA),
            is(1));
        assertThat(
            bcal.getMaximum(BadiCalendar.YEAR_OF_ERA),
            is(3 * 361));
        assertThat(
            bcal.contains(BadiCalendar.YEAR_OF_ERA),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.YEAR_OF_ERA, 14),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA, 97),
            is(BadiCalendar.of(6, 2, BadiMonth.JALAL, 13)));

        // operators
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA.minimized()),
            is(BadiCalendar.of(1, 1, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA.maximized()),
            is(BadiCalendar.ofComplete(3, 19, 19, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA.decremented()),
            is(BadiCalendar.of(5, 10, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA.incremented()),
            is(BadiCalendar.of(5, 12, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA.atFloor()),
            is(BadiCalendar.of(5, 11, BadiMonth.BAHA, 1)));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA.atCeiling()),
            is(BadiCalendar.of(5, 11, BadiMonth.ALA, 19)));
    }

    @Test
    public void yearOfEraOnIntercalary() {
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 8, 5);
        assertThat(
            bcal.contains(BadiCalendar.YEAR_OF_ERA),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.YEAR_OF_ERA, 85),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_ERA, 85),
            is(BadiCalendar.ofIntercalary(5, 9, 4)));
    }

    @Test
    public void kullishai() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.KULL_I_SHAI.name(),
            is("KULL_I_SHAI"));
        assertThat(
            BadiCalendar.KULL_I_SHAI.getType() == Integer.class,
            is(true));
        assertThat(
            BadiCalendar.KULL_I_SHAI.getSymbol(),
            is('K'));
        assertThat(
            BadiCalendar.KULL_I_SHAI.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.KULL_I_SHAI.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.KULL_I_SHAI.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.KULL_I_SHAI.getDefaultMinimum(),
            is(1));
        assertThat(
            BadiCalendar.KULL_I_SHAI.getDefaultMaximum(),
            is(3));
        assertThat(
            BadiCalendar.KULL_I_SHAI.getDisplayName(Locale.ENGLISH),
            is("Kull-i-Shay"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.getKullishai(),
            is(1));
        assertThat(
            bcal.getInt(BadiCalendar.KULL_I_SHAI),
            is(1));
        assertThat(
            bcal.getMinimum(BadiCalendar.KULL_I_SHAI),
            is(1));
        assertThat(
            bcal.getMaximum(BadiCalendar.KULL_I_SHAI),
            is(3));
        assertThat(
            bcal.contains(BadiCalendar.KULL_I_SHAI),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.KULL_I_SHAI, 2),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.KULL_I_SHAI, 2),
            is(BadiCalendar.ofComplete(2, 5, 11, BadiMonth.JALAL, 13)));
    }

    @Test
    public void vahid() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.VAHID.name(),
            is("VAHID"));
        assertThat(
            BadiCalendar.VAHID.getType() == Integer.class,
            is(true));
        assertThat(
            BadiCalendar.VAHID.getSymbol(),
            is('V'));
        assertThat(
            BadiCalendar.VAHID.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.VAHID.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.VAHID.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.VAHID.getDefaultMinimum(),
            is(1));
        assertThat(
            BadiCalendar.VAHID.getDefaultMaximum(),
            is(19));
        assertThat(
            BadiCalendar.VAHID.getDisplayName(Locale.ENGLISH),
            is("Váḥid"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.getVahid(),
            is(5));
        assertThat(
            bcal.getInt(BadiCalendar.VAHID),
            is(5));
        assertThat(
            bcal.getMinimum(BadiCalendar.VAHID),
            is(1));
        assertThat(
            bcal.getMaximum(BadiCalendar.VAHID),
            is(19));
        assertThat(
            bcal.contains(BadiCalendar.VAHID),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.VAHID, 14),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.VAHID, 14),
            is(BadiCalendar.of(14, 11, BadiMonth.JALAL, 13)));

        // operators
        assertThat(
            bcal.with(BadiCalendar.VAHID.minimized()),
            is(BadiCalendar.of(1, 11, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.VAHID.maximized()),
            is(BadiCalendar.of(19, 11, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.VAHID.decremented()),
            is(BadiCalendar.of(4, 11, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.VAHID.incremented()),
            is(BadiCalendar.of(6, 11, BadiMonth.JALAL, 13)));
        assertThat(
            bcal.with(BadiCalendar.VAHID.atFloor()),
            is(BadiCalendar.of(5, 1, BadiMonth.BAHA, 1)));
        assertThat(
            bcal.with(BadiCalendar.VAHID.atCeiling()),
            is(BadiCalendar.of(5, 19, BadiMonth.ALA, 19)));
    }

    @Test
    public void yearOfVahid() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.name(),
            is("YEAR_OF_VAHID"));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.getType() == Integer.class,
            is(true));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.getSymbol(),
            is('X'));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.getDefaultMinimum(),
            is(1));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.getDefaultMaximum(),
            is(19));
        assertThat(
            BadiCalendar.YEAR_OF_VAHID.getDisplayName(Locale.ENGLISH),
            is("year"));

        // values
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.getYearOfVahid(),
            is(11));
        assertThat(
            bcal.getInt(BadiCalendar.YEAR_OF_VAHID),
            is(11));
        assertThat(
            bcal.getMinimum(BadiCalendar.YEAR_OF_VAHID),
            is(1));
        assertThat(
            bcal.getMaximum(BadiCalendar.YEAR_OF_VAHID),
            is(19));
        assertThat(
            bcal.contains(BadiCalendar.YEAR_OF_VAHID),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.YEAR_OF_VAHID, 14),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.YEAR_OF_VAHID, 14),
            is(BadiCalendar.of(5, 14, BadiMonth.JALAL, 13)));
    }

    @Test
    public void yearOfVahidOnIntercalary() {
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 11, 2);
        assertThat(
            bcal.contains(BadiCalendar.YEAR_OF_VAHID),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.YEAR_OF_VAHID, 19),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.YEAR_OF_VAHID, 20),
            is(false));
    }

    @Test
    public void ayyamIHa() {
        // ChronoElement-interface
        assertThat(
            BadiCalendar.AYYAM_I_HA.name(),
            is("AYYAM_I_HA"));
        assertThat(
            BadiCalendar.AYYAM_I_HA.getType() == BadiIntercalaryDays.class,
            is(true));
        assertThat(
            BadiCalendar.AYYAM_I_HA.getSymbol(),
            is('A'));
        assertThat(
            BadiCalendar.AYYAM_I_HA.isDateElement(),
            is(true));
        assertThat(
            BadiCalendar.AYYAM_I_HA.isTimeElement(),
            is(false));
        assertThat(
            BadiCalendar.AYYAM_I_HA.isLenient(),
            is(false));
        assertThat(
            BadiCalendar.AYYAM_I_HA.getDefaultMinimum(),
            is(BadiIntercalaryDays.AYYAM_I_HA));
        assertThat(
            BadiCalendar.AYYAM_I_HA.getDefaultMaximum(),
            is(BadiIntercalaryDays.AYYAM_I_HA));
        assertThat(
            BadiCalendar.AYYAM_I_HA.getDisplayName(Locale.GERMAN),
            is("Aiyam-e Ha'"));

        // values
        BadiCalendar bcal = BadiCalendar.ofIntercalary(5, 11, 2);
        assertThat(
            bcal.get(BadiCalendar.AYYAM_I_HA),
            is(BadiIntercalaryDays.AYYAM_I_HA));
        assertThat(
            bcal.getMinimum(BadiCalendar.AYYAM_I_HA),
            is(BadiIntercalaryDays.AYYAM_I_HA));
        assertThat(
            bcal.getMaximum(BadiCalendar.AYYAM_I_HA),
            is(BadiIntercalaryDays.AYYAM_I_HA));
        assertThat(
            bcal.contains(BadiCalendar.AYYAM_I_HA),
            is(true));
        assertThat(
            bcal.hasMonth(),
            is(false));
        assertThat(
            bcal.isIntercalaryDay(),
            is(true));
        assertThat(
            bcal.isValid(BadiCalendar.AYYAM_I_HA, BadiIntercalaryDays.AYYAM_I_HA),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.AYYAM_I_HA, BadiIntercalaryDays.AYYAM_I_HA),
            is(bcal));
    }

    @Test
    public void ayyamIHaOnNormalDays() {
        BadiCalendar bcal = BadiCalendar.of(5, 11, BadiMonth.JALAL, 13);
        assertThat(
            bcal.contains(BadiCalendar.AYYAM_I_HA),
            is(false));
        assertThat(
            bcal.hasMonth(),
            is(true));
        assertThat(
            bcal.isIntercalaryDay(),
            is(false));
        assertThat(
            bcal.isValid(BadiCalendar.AYYAM_I_HA, BadiIntercalaryDays.AYYAM_I_HA),
            is(true));
        assertThat(
            bcal.with(BadiCalendar.AYYAM_I_HA, BadiIntercalaryDays.AYYAM_I_HA),
            is(BadiCalendar.ofIntercalary(5, 11, 4)));
        assertThat(
            bcal.getMinimum(BadiCalendar.AYYAM_I_HA),
            is(BadiIntercalaryDays.AYYAM_I_HA));
        assertThat(
            bcal.getMaximum(BadiCalendar.AYYAM_I_HA),
            is(BadiIntercalaryDays.AYYAM_I_HA));
    }

    @Test
    public void min() {
        BadiCalendar min = BadiCalendar.axis().getMinimum(); // Thursday
        assertThat(
            min,
            is(BadiCalendar.of(1, 1, 1, 1)));
        assertThat(
            min.getMinimum(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.THURSDAY));
        assertThat(
            min.getMaximum(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.FRIDAY));
        assertThat(
            min.isValid(BadiCalendar.DAY_OF_WEEK, Weekday.WEDNESDAY),
            is(false));
        assertThat(
            min.isValid(BadiCalendar.DAY_OF_WEEK, Weekday.THURSDAY),
            is(true));
    }

    @Test
    public void max() {
        BadiCalendar max = BadiCalendar.axis().getMaximum();
        assertThat(
            max,
            is(BadiCalendar.ofComplete(3, 19, 19, BadiMonth.ALA, 19)));
        assertThat(
            max.getMinimum(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.SATURDAY));
        assertThat(
            max.getMaximum(BadiCalendar.DAY_OF_WEEK),
            is(Weekday.THURSDAY));
        assertThat(
            max.isValid(BadiCalendar.DAY_OF_WEEK, Weekday.SUNDAY),
            is(true));
        assertThat(
            max.isValid(BadiCalendar.DAY_OF_WEEK, Weekday.FRIDAY),
            is(false));
    }

}
