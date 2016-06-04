package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class MonthsTest {

    @Test
    public void zeroMonths() {
        assertThat(
            Months.ZERO == Months.of(0),
            is(true));
        assertThat(
            Months.ZERO.getPartialAmount(CalendarUnit.MONTHS),
            is(0L));
        assertThat(
            Months.ZERO.getAmount(),
            is(0));
        assertThat(
            Months.ZERO.getTotalLength().size(),
            is(0));
        assertThat(
            Months.ZERO.isEmpty(),
            is(true));
        assertThat(
            Months.ZERO.toString(),
            is("P0M"));
    }

    @Test
    public void oneMonth() {
        assertThat(
            Months.ONE == Months.of(1),
            is(true));
        assertThat(
            Months.ONE.getPartialAmount(CalendarUnit.MONTHS),
            is(1L));
        assertThat(
            Months.ONE.getAmount(),
            is(1));
        assertThat(
            Months.ONE.getTotalLength().size(),
            is(1));
        assertThat(
            Months.ONE.isPositive(),
            is(true));
        assertThat(
            Months.ONE.toString(),
            is("P1M"));
    }

    @Test
    public void signQuery() {
        assertThat(
            Months.of(2).isPositive(),
            is(true));
        assertThat(
            Months.of(2).isEmpty(),
            is(false));
        assertThat(
            Months.of(2).isNegative(),
            is(false));
        assertThat(
            Months.of(-2).isNegative(),
            is(true));
        assertThat(
            Months.of(-2).isEmpty(),
            is(false));
        assertThat(
            Months.of(-2).isPositive(),
            is(false));
    }

    @Test
    public void contains() {
        assertThat(Months.ZERO.contains(CalendarUnit.MONTHS), is(false));
        assertThat(Months.ONE.contains(CalendarUnit.MONTHS), is(true));
    }

    @Test
    public void addToPlainDate() {
        assertThat(
            PlainDate.of(1984, 2, 29).plus(Months.of(5)),
            is(PlainDate.of(1984, 7, 29)));
        assertThat(
            Months.of(5).addTo(PlainDate.of(1984, 2, 29)),
            is(PlainDate.of(1984, 7, 29)));
    }

    @Test
    public void subtractFromPlainDate() {
        assertThat(
            PlainDate.of(1984, 2, 29).minus(Months.of(5)),
            is(PlainDate.of(1983, 9, 29)));
        assertThat(
            Months.of(5).subtractFrom(PlainDate.of(1984, 2, 29)),
            is(PlainDate.of(1983, 9, 29)));
    }

    @Test
    public void betweenPlainDates() {
        assertThat(
            Months.between(PlainDate.of(1979, 2, 28), PlainDate.of(1985, 2, 27)),
            is(Months.of(71)));
        assertThat(
            Months.between(PlainTimestamp.of(1979, 2, 28, 9, 15), PlainTimestamp.of(1985, 2, 28, 9, 15)),
            is(Months.of(72)));
    }

    @Test
    public void parseMonths() throws ParseException {
        assertThat(
            Months.parsePeriod("-P35M"),
            is(Months.of(-35)));
        assertThat(
            Months.parsePeriod("P0M"),
            is(Months.ZERO));
    }

    @Test(expected=ParseException.class)
    public void parseMonthsWithWrongUnit() throws ParseException {
        Months.parsePeriod("P35Y");
    }

    @Test
    public void getUnit() {
        assertThat(Months.of(15).getUnit(), is(CalendarUnit.MONTHS));
    }

    @Test
    public void toDuration() {
        assertThat(Months.of(-15).toStdDuration(), is(Duration.of(-15, CalendarUnit.MONTHS)));
    }

}