package net.time4j.calendar;

import net.time4j.engine.CalendarDays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class EthiopianOperatorTest {

    @Test
    public void plusYears() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)
                .plus(3, EthiopianCalendar.Unit.YEARS),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1738, EthiopianMonth.PAGUMEN, 5)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1738, 1, 1).isLeapYear(),
            is(false));
    }

    @Test
    public void plusMonths() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1732, EthiopianMonth.GENBOT, 30)
                .plus(17, EthiopianCalendar.Unit.MONTHS),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1733, EthiopianMonth.PAGUMEN, 5)));
    }

    @Test
    public void plusWeeks() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.NEHASSE, 30)
                .plus(2, EthiopianCalendar.Unit.WEEKS),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1737, EthiopianMonth.MESKEREM, 9)));
    }

    @Test
    public void plusDays() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.NEHASSE, 30)
                .plus(30, EthiopianCalendar.Unit.DAYS),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1737, EthiopianMonth.MESKEREM, 25)));
    }

    @Test
    public void nextMonth() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, 13, 5)
                .with(EthiopianCalendar.MONTH_OF_YEAR.incremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1737, 1, 5)));
    }

    @Test
    public void previousMonth() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1737, 1, 5)
                .with(EthiopianCalendar.MONTH_OF_YEAR.decremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, 13, 5)));
    }

    @Test
    public void nextYear() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, 13, 6)
                .with(EthiopianCalendar.YEAR_OF_ERA.incremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, 13, 5)));
    }

    @Test
    public void previousYear() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, 3, 30)
                .with(EthiopianCalendar.YEAR_OF_ERA.decremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, 3, 30)));
    }

    @Test
    public void nextDay() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)
                .with(EthiopianCalendar.DAY_OF_YEAR.incremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)
                .with(EthiopianCalendar.DAY_OF_MONTH.incremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)
                .with(EthiopianCalendar.DAY_OF_WEEK.incremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)));
    }

    @Test
    public void previousDay() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)
                .with(EthiopianCalendar.DAY_OF_YEAR.decremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)
                .with(EthiopianCalendar.DAY_OF_MONTH.decremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)
                .with(EthiopianCalendar.DAY_OF_WEEK.decremented()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)));
    }

    @Test
    public void maxDay() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MEGABIT, 21)
                .with(EthiopianCalendar.DAY_OF_MONTH.maximized()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MEGABIT, 30)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 2)
                .with(EthiopianCalendar.DAY_OF_MONTH.maximized()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.PAGUMEN, 2)
                .with(EthiopianCalendar.DAY_OF_MONTH.maximized()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.PAGUMEN, 5)));
    }

    @Test
    public void minDay() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MEGABIT, 21)
                .with(EthiopianCalendar.DAY_OF_MONTH.minimized()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MEGABIT, 1)));
    }

    @Test
    public void yearAtFloor() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MIAZIA, 2)
                .with(EthiopianCalendar.YEAR_OF_ERA.atFloor()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MESKEREM, 1)));
    }

    @Test
    public void yearAtCeiling() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.MIAZIA, 2)
                .with(EthiopianCalendar.YEAR_OF_ERA.atCeiling()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MIAZIA, 2)
                .with(EthiopianCalendar.YEAR_OF_ERA.atCeiling()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.PAGUMEN, 5)));
    }

    @Test
    public void monthAtFloor() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MIAZIA, 2)
                .with(EthiopianCalendar.MONTH_OF_YEAR.atFloor()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MIAZIA, 1)));
    }

    @Test
    public void monthAtCeiling() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MIAZIA, 2)
                .with(EthiopianCalendar.MONTH_OF_YEAR.atCeiling()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.MIAZIA, 30)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 2)
                .with(EthiopianCalendar.YEAR_OF_ERA.atCeiling()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1735, EthiopianMonth.PAGUMEN, 6)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.PAGUMEN, 2)
                .with(EthiopianCalendar.YEAR_OF_ERA.atCeiling()),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1736, EthiopianMonth.PAGUMEN, 5)));
    }

    @Test(expected=ArithmeticException.class)
    public void beforeCreationOfWorld() {
        EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 1, 1, 1).minus(CalendarDays.of(1));
    }

    @Test(expected=ArithmeticException.class)
    public void afterYear9999() {
        EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 9999, 13, 6).plus(CalendarDays.of(1));
    }

    @Test
    public void sameMaxInTwoEpochs() {
        assertThat(
            EthiopianCalendar.axis().getMaximum(),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 9999, 13, 6))
        );
        assertThat(
            EthiopianCalendar.axis().getMaximum(),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 15499, 13, 6))
        );
    }

}