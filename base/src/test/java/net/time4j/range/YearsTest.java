package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.Duration;
import net.time4j.PlainDate;
import net.time4j.PlainTimestamp;
import net.time4j.Weekcycle;
import net.time4j.engine.TimeSpan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class YearsTest {

    @Test
    public void integer_MIN_VALUE() {
        assertThat(
            Years.ofGregorian(Integer.MIN_VALUE).getPartialAmount(CalendarUnit.YEARS),
            is(-2147483648L));
        assertThat(
            Years.ofGregorian(Integer.MIN_VALUE).getAmount(),
            is(-2147483648));
        assertThat(
            Years.ofGregorian(Integer.MIN_VALUE).getTotalLength().size(),
            is(1));
        assertThat(
            Years.ofGregorian(Integer.MIN_VALUE).getTotalLength().get(0),
            is(TimeSpan.Item.of(-((long) Integer.MIN_VALUE), CalendarUnit.YEARS)));
        assertThat(
            Years.ofGregorian(Integer.MIN_VALUE).isNegative(),
            is(true));
        assertThat(
            Years.ofGregorian(Integer.MIN_VALUE).toString(),
            is("-P2147483648Y"));
    }

    @Test
    public void zero() {
        assertThat(
            Years.ZERO == Years.ofGregorian(0),
            is(true));
        assertThat(
            Years.ZERO.getPartialAmount(CalendarUnit.YEARS),
            is(0L));
        assertThat(
            Years.ZERO.getAmount(),
            is(0));
        assertThat(
            Years.ZERO.getTotalLength().size(),
            is(0));
        assertThat(
            Years.ZERO.isEmpty(),
            is(true));
        assertThat(
            Years.ZERO.toString(),
            is("P0Y"));
    }

    @Test
    public void one() {
        assertThat(
            Years.ONE == Years.ofGregorian(1),
            is(true));
        assertThat(
            Years.ONE.getPartialAmount(CalendarUnit.YEARS),
            is(1L));
        assertThat(
            Years.ONE.getAmount(),
            is(1));
        assertThat(
            Years.ONE.getTotalLength().size(),
            is(1));
        assertThat(
            Years.ONE.isPositive(),
            is(true));
        assertThat(
            Years.ONE.toString(),
            is("P1Y"));
    }

    @Test
    public void signQuery() {
        assertThat(
            Years.ofGregorian(2).isPositive(),
            is(true));
        assertThat(
            Years.ofGregorian(2).isEmpty(),
            is(false));
        assertThat(
            Years.ofGregorian(2).isNegative(),
            is(false));
        assertThat(
            Years.ofGregorian(-2).isNegative(),
            is(true));
        assertThat(
            Years.ofGregorian(-2).isEmpty(),
            is(false));
        assertThat(
            Years.ofGregorian(-2).isPositive(),
            is(false));
    }

    @Test
    public void contains() {
        assertThat(Years.ZERO.contains(CalendarUnit.YEARS), is(false));
        assertThat(Years.ONE.contains(CalendarUnit.YEARS), is(true));
    }

    @Test
    public void addToPlainDate() {
        assertThat(
            PlainDate.of(1984, 2, 29).plus(Years.ofGregorian(5)),
            is(PlainDate.of(1989, 2, 28)));
        assertThat(
            Years.ofGregorian(5).addTo(PlainDate.of(1984, 2, 29)),
            is(PlainDate.of(1989, 2, 28)));
    }

    @Test
    public void subtractFromPlainDate() {
        assertThat(
            PlainDate.of(1984, 2, 29).minus(Years.ofGregorian(5)),
            is(PlainDate.of(1979, 2, 28)));
        assertThat(
            Years.ofGregorian(5).subtractFrom(PlainDate.of(1984, 2, 29)),
            is(PlainDate.of(1979, 2, 28)));
    }

    @Test
    public void betweenPlainDatesOrTimestamps() {
        assertThat(
            Years.between(PlainDate.of(1979, 2, 28), PlainDate.of(1985, 2, 27)),
            is(Years.ofGregorian(5)));
        assertThat(
            Years.between(PlainTimestamp.of(1979, 2, 28, 9, 15), PlainTimestamp.of(1985, 2, 28, 9, 15)),
            is(Years.ofGregorian(6)));
    }

    @Test
    public void betweenCalendarYears() {
        CalendarYear y1 = CalendarYear.of(2013);
        CalendarYear y2 = CalendarYear.of(2017);
        assertThat(Years.between(y1, y2), is(Years.ofGregorian(4)));
    }

    @Test
    public void abs() {
        assertThat(
            Years.ofGregorian(-24).abs(),
            is(Years.ofGregorian(24)));
        assertThat(
            Years.ofGregorian(24).abs(),
            is(Years.ofGregorian(24)));
        assertThat(
            Years.ZERO.abs(),
            is(Years.ofGregorian(0)));
    }

    @Test(expected=ArithmeticException.class)
    public void absOverflow() {
        Years.ofGregorian(Integer.MIN_VALUE).abs();
    }

    @Test
    public void inverse() {
        assertThat(
            Years.ofGregorian(-24).inverse(),
            is(Years.ofGregorian(24)));
        assertThat(
            Years.ofGregorian(24).inverse(),
            is(Years.ofGregorian(-24)));
        assertThat(
            Years.ZERO.inverse(),
            is(Years.ofGregorian(0)));
    }

    @Test(expected=ArithmeticException.class)
    public void inverseOverflow() {
        Years.ofGregorian(Integer.MIN_VALUE).inverse();
    }

    @Test
    public void plus() {
        assertThat(
            Years.ofGregorian(-24).plus(Years.ofGregorian(3)),
            is(Years.ofGregorian(-21)));
        assertThat(
            Years.ofGregorian(24).plus(3),
            is(Years.ofGregorian(27)));
        assertThat(
            Years.ZERO.plus(0),
            is(Years.ofGregorian(0)));
    }

    @Test(expected=ArithmeticException.class)
    public void plusOverflow() {
        Years.ofGregorian(2).plus(Integer.MAX_VALUE);
    }

    @Test
    public void minus() {
        assertThat(
            Years.ofGregorian(-24).minus(Years.ofGregorian(3)),
            is(Years.ofGregorian(-27)));
        assertThat(
            Years.ofGregorian(24).minus(3),
            is(Years.ofGregorian(21)));
        assertThat(
            Years.ZERO.minus(0),
            is(Years.ofGregorian(0)));
    }

    @Test(expected=ArithmeticException.class)
    public void minusOverflow() {
        Years.ofGregorian(-2).minus(Integer.MAX_VALUE);
    }

    @Test
    public void multipliedBy() {
        assertThat(
            Years.ofGregorian(-24).multipliedBy(-3),
            is(Years.ofGregorian(72)));
        assertThat(
            Years.ZERO.multipliedBy(10),
            is(Years.ofGregorian(0)));
    }

    @Test(expected=ArithmeticException.class)
    public void multipliedByOverflow() {
        Years.ofGregorian(-2).multipliedBy(Integer.MAX_VALUE);
    }

    @Test
    public void parse() throws ParseException {
        assertThat(
            Years.parseGregorian("-P35Y"),
            is(Years.ofGregorian(-35)));
        assertThat(
            Years.parseGregorian("P35Y"),
            is(Years.ofGregorian(35)));
        assertThat(
            Years.parseGregorian("P0Y"),
            is(Years.ZERO));
    }

    @Test(expected=ParseException.class)
    public void parseNoLiteralP() throws ParseException {
        Years.parseGregorian("-35Y");
    }

    @Test(expected=ParseException.class)
    public void parseNoUnitY() throws ParseException {
        Years.parseGregorian("P35");
    }

    @Test(expected=ParseException.class)
    public void parseNoDigits() throws ParseException {
        Years.parseGregorian("PY");
    }

    @Test(expected=ParseException.class)
    public void parseTrailingChars() throws ParseException {
        Years.parseGregorian("P2Yx");
    }

    @Test
    public void parseWeekBased() throws ParseException {
        assertThat(
            Years.parseWeekBased("-P35Y"),
            is(Years.ofWeekBased(-35)));
        assertThat(
            Years.parseWeekBased("P35Y"),
            is(Years.ofWeekBased(35)));
    }

    @Test
    public void compareTo() throws ParseException {
        assertThat(
            Years.parseWeekBased("-P35Y").compareTo(Years.parseWeekBased("-P35Y")),
            is(0));
        assertThat(
            Years.parseWeekBased("-P35Y").compareTo(Years.parseWeekBased("-P34Y")),
            is(-1));
        assertThat(
            Years.parseWeekBased("-P35Y").compareTo(Years.parseWeekBased("-P36Y")),
            is(1));
    }

    @Test(expected=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToGregorianWeekBased() throws ParseException {
        Years y1 = Years.parseGregorian("-P35Y");
        Years y2 = Years.parseWeekBased("-P35Y");
        y1.compareTo(y2);
    }

    @Test
    public void getUnit() {
        assertThat(Years.ofGregorian(15).getUnit(), is(CalendarUnit.YEARS));
        assertThat(Years.ofWeekBased(15).getUnit(), is(CalendarUnit.weekBasedYears()));
    }

    @Test
    public void toDuration() {
        assertThat(Years.ofGregorian(15).toStdDuration(), is(Duration.ofCalendarUnits(15, 0, 0)));
        assertThat(Years.ofWeekBased(-15).toStdDuration(), is(Duration.of(-15, Weekcycle.YEARS)));
    }

}