package net.time4j;

import net.time4j.engine.ChronoException;
import net.time4j.engine.TimePoint;
import net.time4j.engine.TimeSpan;
import net.time4j.engine.TimeSpan.Item;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.CalendarUnit.WEEKS;
import static net.time4j.CalendarUnit.YEARS;
import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.MICROS;
import static net.time4j.ClockUnit.MILLIS;
import static net.time4j.ClockUnit.MINUTES;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.ClockUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class DurationBasicsTest {

    @Test
    public void getPartialAmountYears() {
        assertThat(
            Duration.ofCalendarUnits(4, 3, 2).getPartialAmount(YEARS),
            is(4L));
    }

    @Test
    public void getPartialAmountMonths() {
        assertThat(
            Duration.ofCalendarUnits(4, 3, 2).getPartialAmount(MONTHS),
            is(3L));
    }

    @Test
    public void getPartialAmountWeeks() {
        assertThat(
            Duration.of(5, WEEKS).getPartialAmount(WEEKS),
            is(5L));
        assertThat(
            Duration.ofCalendarUnits(4, 3, 7).getPartialAmount(WEEKS),
            is(0L));
    }

    @Test
    public void getPartialAmountDays() {
        assertThat(
            Duration.of(5, WEEKS).getPartialAmount(DAYS),
            is(0L));
        assertThat(
            Duration.ofCalendarUnits(4, 3, 2).getPartialAmount(DAYS),
            is(2L));
    }

    @Test
    public void getPartialAmountHours() {
        assertThat(
            Duration.ofClockUnits(4, 3, 2).getPartialAmount(HOURS),
            is(4L));
    }

    @Test
    public void getPartialAmountMinutes() {
        assertThat(
            Duration.ofClockUnits(4, 3, 2).getPartialAmount(MINUTES),
            is(3L));
    }

    @Test
    public void getPartialAmountSeconds() {
        assertThat(
            Duration.ofClockUnits(4, 3, 2).getPartialAmount(SECONDS),
            is(2L));
    }

    @Test
    public void getPartialAmountMillis() {
        assertThat(
            Duration.of(123456789, NANOS).getPartialAmount(MILLIS),
            is(123L));
    }

    @Test
    public void getPartialAmountMicros() {
        assertThat(
            Duration.of(123456789, NANOS).getPartialAmount(MICROS),
            is(123456L));
    }

    @Test
    public void getPartialAmountNanos() {
        assertThat(
            Duration.of(123456789, NANOS).getPartialAmount(NANOS),
            is(123456789L));
    }

    @Test
    public void getPartialAmountWeekBasedYears() {
        IsoDateUnit unit = CalendarUnit.weekBasedYears();
        assertThat(
            Duration.of(2, unit).getPartialAmount(unit),
            is(2L));
    }

    @Test
    public void ofSingleUnitPositive7Weeks() {
        Duration<CalendarUnit> result = Duration.of(7, WEEKS);
        assertThat(result.toString(), is("P7W"));
        assertThat(result.getTotalLength().size(), is(1));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        Item<CalendarUnit> item = result.getTotalLength().get(0);
        assertThat(item.getAmount(), is(7L));
        assertThat(item.getUnit(), is(WEEKS));
    }

    @Test
    public void ofSingleUnitPositive3Days() {
        Duration<CalendarUnit> result = Duration.of(3, DAYS);
        assertThat(result.toString(), is("P3D"));
        assertThat(result.getTotalLength().size(), is(1));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        Item<CalendarUnit> item = result.getTotalLength().get(0);
        assertThat(item.getAmount(), is(3L));
        assertThat(item.getUnit(), is(DAYS));
    }

    @Test
    public void ofSingleUnitPositive6Hours() {
        Duration<ClockUnit> result = Duration.of(6, HOURS);
        assertThat(result.toString(), is("PT6H"));
        assertThat(result.getTotalLength().size(), is(1));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        Item<ClockUnit> item = result.getTotalLength().get(0);
        assertThat(item.getAmount(), is(6L));
        assertThat(item.getUnit(), is(HOURS));
    }

    @Test
    public void ofSingleUnitNegative7Weeks() {
        Duration<CalendarUnit> result = Duration.of(-7, WEEKS);
        assertThat(result.toString(), is("-P7W"));
        assertThat(result.getTotalLength().size(), is(1));
        assertThat(result.isPositive(), is(false));
        assertThat(result.isNegative(), is(true));
        assertThat(result.isEmpty(), is(false));
        Item<CalendarUnit> item = result.getTotalLength().get(0);
        assertThat(item.getAmount(), is(7L));
        assertThat(item.getUnit(), is(WEEKS));
    }

    @Test
    public void ofSingleUnitNegative3Days() {
        Duration<CalendarUnit> result = Duration.of(-3, DAYS);
        assertThat(result.toString(), is("-P3D"));
        assertThat(result.getTotalLength().size(), is(1));
        assertThat(result.isPositive(), is(false));
        assertThat(result.isNegative(), is(true));
        assertThat(result.isEmpty(), is(false));
        Item<CalendarUnit> item = result.getTotalLength().get(0);
        assertThat(item.getAmount(), is(3L));
        assertThat(item.getUnit(), is(DAYS));
    }

    @Test
    public void ofSingleUnitNegative6Hours() {
        Duration<ClockUnit> result = Duration.of(-6, HOURS);
        assertThat(result.toString(), is("-PT6H"));
        assertThat(result.getTotalLength().size(), is(1));
        assertThat(result.isPositive(), is(false));
        assertThat(result.isNegative(), is(true));
        assertThat(result.isEmpty(), is(false));
        Item<ClockUnit> item = result.getTotalLength().get(0);
        assertThat(item.getAmount(), is(6L));
        assertThat(item.getUnit(), is(HOURS));
    }

    @Test
    public void ofSingleUnitZeroWeeks() {
        Duration<CalendarUnit> result = Duration.of(0, WEEKS);
        assertThat(result.toString(), is("PT0S"));
        assertThat(result.getTotalLength().size(), is(0));
        assertThat(result.isPositive(), is(false));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void ofSingleUnitZeroDays() {
        Duration<CalendarUnit> result = Duration.of(0, DAYS);
        assertThat(result.toString(), is("PT0S"));
        assertThat(result.getTotalLength().size(), is(0));
        assertThat(result.isPositive(), is(false));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void ofSingleUnitZeroHours() {
        Duration<ClockUnit> result = Duration.of(0, HOURS);
        assertThat(result.toString(), is("PT0S"));
        assertThat(result.getTotalLength().size(), is(0));
        assertThat(result.isPositive(), is(false));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void ofCalendarUnitsInYearsMonthsDays() {
        Duration<CalendarUnit> result = Duration.ofCalendarUnits(5, 2, 45);
        assertThat(result.toString(), is("P5Y2M45D"));
        assertThat(result.getTotalLength().size(), is(3));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        for (Item<CalendarUnit> item : result.getTotalLength()) {
            switch(item.getUnit()) {
                case YEARS:
                    assertThat(item.getAmount(), is(5L));
                    break;
                case MONTHS:
                    assertThat(item.getAmount(), is(2L));
                    break;
                case DAYS:
                    assertThat(item.getAmount(), is(45L));
                    break;
                default:
                    fail("Unexpected unit: " + item.getUnit());
            }
        }
    }

    @Test
    public void ofCalendarUnitsInMonthsDays() {
        Duration<CalendarUnit> result = Duration.ofCalendarUnits(0, 2, 45);
        assertThat(result.toString(), is("P2M45D"));
        assertThat(result.getTotalLength().size(), is(2));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        for (Item<CalendarUnit> item : result.getTotalLength()) {
            switch(item.getUnit()) {
                case MONTHS:
                    assertThat(item.getAmount(), is(2L));
                    break;
                case DAYS:
                    assertThat(item.getAmount(), is(45L));
                    break;
                default:
                    fail("Unexpected unit: " + item.getUnit());
            }
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofCalendarUnitsWithNegativePart() {
        Duration.ofCalendarUnits(5, -2, 45);
    }

    @Test
    public void ofClockUnitsInHoursMinutesSeconds() {
        Duration<ClockUnit> result = Duration.ofClockUnits(2, 65, 14);
        assertThat(result.toString(), is("PT2H65M14S"));
        assertThat(result.getTotalLength().size(), is(3));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        for (Item<ClockUnit> item : result.getTotalLength()) {
            switch(item.getUnit()) {
                case HOURS:
                    assertThat(item.getAmount(), is(2L));
                    break;
                case MINUTES:
                    assertThat(item.getAmount(), is(65L));
                    break;
                case SECONDS:
                    assertThat(item.getAmount(), is(14L));
                    break;
                default:
                    fail("Unexpected unit: " + item.getUnit());
            }
        }
    }

    @Test
    public void ofClockUnitsInHoursMinutes() {
        Duration<ClockUnit> result = Duration.ofClockUnits(2, 65, 0);
        assertThat(result.toString(), is("PT2H65M"));
        assertThat(result.getTotalLength().size(), is(2));
        assertThat(result.isPositive(), is(true));
        assertThat(result.isNegative(), is(false));
        assertThat(result.isEmpty(), is(false));
        for (Item<ClockUnit> item : result.getTotalLength()) {
            switch(item.getUnit()) {
                case HOURS:
                    assertThat(item.getAmount(), is(2L));
                    break;
                case MINUTES:
                    assertThat(item.getAmount(), is(65L));
                    break;
                default:
                    fail("Unexpected unit: " + item.getUnit());
            }
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofClockUnitsWithNegativePart() {
        Duration.ofClockUnits(5, -2, 45);
    }

    @Test
    public void ofPositiveBuilder() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 4).plus(75800001, ClockUnit.NANOS);
        Duration<IsoUnit> expResult =
            Duration.ofZero().plus(datePeriod).plus(timePeriod);
        Duration<IsoUnit> result =
            Duration.ofPositive().years(12).months(4).days(3)
            .hours(150).minutes(2).seconds(4).millis(75).micros(800).nanos(1)
            .build();
        assertThat(result, is(expResult));
        assertThat(result.toString(), is("P12Y4M3DT150H2M4,075800001S"));
    }

    @Test
    public void ofPositiveBuilderWithZeroPart() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 0, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 0, 0);
        Duration<IsoUnit> expResult =
            Duration.ofZero().plus(datePeriod).plus(timePeriod);
        Duration<IsoUnit> result =
            Duration.ofPositive()
            .hours(150).years(12).months(0).days(3).build();
        assertThat(result, is(expResult));
        assertThat(result.toString(), is("P12Y3DT150H"));
    }

    @Test
    public void ofNegativeBuilder() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 4).plus(75800001, ClockUnit.NANOS);
        Duration<IsoUnit> expResult =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse();
        Duration<IsoUnit> result =
            Duration.ofNegative()
            .hours(150).months(4).seconds(4).nanos(1).millis(75).micros(800)
            .years(12).days(3).minutes(2).build();
        assertThat(result, is(expResult));
        assertThat(result.toString(), is("-P12Y4M3DT150H2M4,075800001S"));
    }

    @Test
    public void ofNegativeBuilderWithZeroPart() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 0, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 0, 0);
        Duration<IsoUnit> expResult =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse();
        Duration<IsoUnit> result =
            Duration.ofNegative()
            .hours(150).years(12).seconds(0).days(3).build();
        assertThat(result, is(expResult));
        assertThat(result.toString(), is("-P12Y3DT150H"));
    }

    @Test(expected=IllegalStateException.class)
    public void ofBuilderWithDuplicateUnit1() {
        Duration.ofNegative().years(12).months(4).days(3).days(6)
        .hours(150).minutes(2).seconds(4).millis(75).micros(800).nanos(1)
        .build();
    }

    @Test(expected=IllegalStateException.class)
    public void ofBuilderWithDuplicateUnit2() {
        Duration.ofPositive().years(12).months(4).days(3)
        .hours(150).minutes(2).seconds(4).millis(75).micros(800).nanos(1)
        .nanos(4).build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofBuilderWithNegativeAmount() {
        Duration.ofPositive().years(-12).build();
    }

    @Test
    public void containsDays() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 0, 14);
        assertThat(datePeriod.contains(WEEKS), is(false));
        assertThat(datePeriod.contains(DAYS), is(true));
    }

    @Test
    public void containsFractionalSeconds() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(0, 0, 1224).plus(770123, NANOS);
        assertThat(timePeriod.contains(MILLIS), is(true));
        assertThat(timePeriod.contains(MICROS), is(true));
    }

    @Test
    public void getTotalLength() {
        Object expected = Duration.ofClockUnits(0, 61, 4).getTotalLength();
        Object items =
            Duration.ofPositive().seconds(4).minutes(61).build()
                .getTotalLength();
        assertThat(items, is(expected));
    }

    @Test
    public void isPositive() {
        assertThat(
            Duration.ofCalendarUnits(0, 0, 0).isPositive(),
            is(false));
        assertThat(
            Duration.ofCalendarUnits(0, 0, 1).isPositive(),
            is(true));
        assertThat(
            Duration.ofCalendarUnits(0, 0, 1).inverse().isPositive(),
            is(false));
    }

    @Test
    public void isNegative() {
        assertThat(
            Duration.ofCalendarUnits(0, 0, 0).isNegative(),
            is(false));
        assertThat(
            Duration.ofCalendarUnits(0, 0, 1).isNegative(),
            is(false));
        assertThat(
            Duration.ofCalendarUnits(0, 0, 1).inverse().isNegative(),
            is(true));
    }

    @Test
    public void isEmpty() {
        assertThat(
            Duration.ofCalendarUnits(0, 0, 0).isEmpty(),
            is(true));
        assertThat(
            Duration.ofCalendarUnits(0, 0, 1).isEmpty(),
            is(false));
    }

    @Test
    public void plusCalendarUnits() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 4, 3);
        assertThat(
            datePeriod.plus(0, MONTHS),
            is(datePeriod));
        assertThat(
            datePeriod.plus(4, MONTHS),
            is(Duration.ofCalendarUnits(12, 8, 3)));
        assertThat(
            datePeriod.plus(-4, MONTHS),
            is(Duration.ofCalendarUnits(12, 0, 3)));
        assertThat(
            datePeriod.inverse().plus(3, MONTHS),
            is(Duration.ofCalendarUnits(12, 1, 3).inverse()));
        assertThat(
            datePeriod.plus(1, WEEKS).toString(),
            is("P12Y4M1W3D"));
        assertThat(
            Duration.of(4, WEEKS).plus(1, YEARS).toString(),
            is("P1Y4W"));
        assertThat(
            Duration.of(4, WEEKS).plus(1, DAYS).toString(),
            is("P4W1D"));
    }

    @Test
    public void plusClockUnits() {
        Duration<ClockUnit> timePeriod = Duration.ofClockUnits(0, 0, 3);
        assertThat(
            timePeriod.plus(1, MILLIS),
            is(Duration.ofClockUnits(0, 0, 3).plus(1000000, NANOS)));
        assertThat(
            timePeriod.plus(1, MICROS),
            is(Duration.ofClockUnits(0, 0, 3).plus(1000, NANOS)));

        // Dezimaltest
        timePeriod = Duration.of(0, SECONDS);
        long amount = 1123456789; // >= 1 Milliarde
        timePeriod = timePeriod.plus(amount, NANOS);
        assertThat(timePeriod.getPartialAmount(NANOS), is(amount));
        assertThat(timePeriod.getPartialAmount(SECONDS), is(0L));
    }

    @Test
    public void minusCalendarUnits() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 4, 3);
        assertThat(
            datePeriod.plus(-4, MONTHS),
            is(Duration.ofCalendarUnits(12, 0, 3)));
    }

    @Test
    public void minusClockUnits() {
        Duration<IsoUnit> timePeriod =
            Duration.ofPositive().nanos(123456789).build();
        IsoUnit unit = MILLIS;
        assertThat(
            timePeriod.plus(-1, unit),
            is(Duration.ofPositive().nanos(122456789).build()));
        unit = MICROS;
        assertThat(
            timePeriod.plus(-1, unit),
            is(Duration.ofPositive().nanos(123455789).build()));
    }

    @Test(expected=IllegalStateException.class)
    public void plusWithMixedSigns() {
        Duration.ofCalendarUnits(12, 4, 3).plus(-5, CalendarUnit.MONTHS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void plusWithUnitsOfSameLength() {
        Duration.of(1, CalendarUnit.QUARTERS.unlessInvalid())
            .plus(1, CalendarUnit.QUARTERS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void minusWithUnitsOfSameLength() {
        Duration.of(1, CalendarUnit.MONTHS.withCarryOver())
            .plus(-1, CalendarUnit.MONTHS);
    }

    @Test
    public void withCalendarUnits() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 4, 3);
        assertThat(
            datePeriod.with(5, MONTHS),
            is(Duration.ofCalendarUnits(12, 5, 3)));
        assertThat(
            datePeriod.with(0, MONTHS),
            is(Duration.ofCalendarUnits(12, 0, 3)));
        assertThat(
            Duration.ofCalendarUnits(12, 4, 10).with(1, WEEKS).toString(),
            is("P12Y4M1W10D"));
        assertThat(
            Duration.of(4, WEEKS).with(3, DAYS).toString(),
            is("P4W3D"));
        assertThat(
            Duration.of(4, WEEKS).with(3, MONTHS).toString(),
            is("P3M4W"));
        assertThat(
            Duration.of(4, WEEKS).with(-2, WEEKS),
            is(Duration.of(-2, WEEKS)));
        assertThat(
            Duration.ofCalendarUnits(0, 5, 0).inverse().with(6, MONTHS),
            is(Duration.ofCalendarUnits(0, 6, 0)));
        assertThat(
            Duration.ofCalendarUnits(0, 5, 30).inverse().with(-6, MONTHS),
            is(Duration.ofCalendarUnits(0, 6, 30).inverse()));
    }

    @Test
    public void withClockUnits() {
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(0, 0, 3).plus(123456789, NANOS);
        assertThat(
            timePeriod.with(1, MILLIS),
            is(Duration.ofClockUnits(0, 0, 3).plus(1000000, NANOS)));
        assertThat(
            timePeriod.with(1, MICROS),
            is(Duration.ofClockUnits(0, 0, 3).plus(1000, NANOS)));
    }

    @Test(expected=IllegalStateException.class)
    public void withItemsOfMixedSigns() {
        Duration.ofCalendarUnits(0, 5, 30).inverse().with(6, MONTHS);
    }

    @Test
    public void abs() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 4, 3);
        assertThat(datePeriod.inverse().abs(), is(datePeriod));
        assertThat(datePeriod.abs(), is(datePeriod));
    }

    @Test
    public void negate() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 4, 3);
        assertThat(datePeriod.inverse(), is(datePeriod.multipliedBy(-1)));
        assertThat(datePeriod.inverse().inverse(), is(datePeriod));

        datePeriod = Duration.of(0, DAYS);
        assertThat(datePeriod.inverse(), is(datePeriod));
    }

    @Test
    public void multipliedBy() {
        Duration<CalendarUnit> datePeriod = Duration.ofCalendarUnits(12, 4, 3);
        assertThat(
            datePeriod.multipliedBy(-1).toString(),
            is("-P12Y4M3D"));
        assertThat(
            datePeriod.multipliedBy(-2).toString(),
            is("-P24Y8M6D"));
        assertThat(
            datePeriod.multipliedBy(3).toString(),
            is("P36Y12M9D"));
        assertThat(
            datePeriod.multipliedBy(0).isEmpty(),
            is(true));
        assertThat(
            datePeriod.multipliedBy(1),
            is(datePeriod));
    }

    @Test
    public void union() {
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3).inverse();
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 4).plus(758000000, NANOS).inverse();
        List<Duration<IsoUnit>> result =
            Duration.ofZero().plus(datePeriod).union(timePeriod);
        assertThat(
            result.size(),
            is(1));
        assertThat(
            result.get(0).toString(),
            is("-P12Y4M3DT150H2M4,758000000S"));
        assertThat(
            Duration.ofZero().plus(datePeriod).union(timePeriod),
            is(Duration.ofZero().plus(timePeriod).union(datePeriod)));

        Duration<CalendarUnit> p1 = Duration.ofCalendarUnits(0, 0, 4);
        Duration<CalendarUnit> p2 = Duration.ofCalendarUnits(0, 1, 34).inverse();
        Duration<CalendarUnit> expected =
            Duration.ofCalendarUnits(0, 1, 30).inverse();
        assertThat(p1.union(p2).get(0), is(expected));
        assertThat(p2.union(p1).get(0), is(expected));

        String period1 =
		  Duration.ofZero()
			.plus(1, CalendarUnit.QUARTERS)
			.union(Duration.of(1, CalendarUnit.MONTHS.unlessInvalid()))
			.get(0)
			.toString();
        assertThat(
            period1,
            is("P1Q1{M-UNLESS_INVALID}"));

        String period2 =
		  Duration.ofZero()
			.plus(1, CalendarUnit.weekBasedYears())
			.union(Duration.of(5, CalendarUnit.MONTHS))
			.get(0)
			.toString();
        assertThat(
            period2,
            is("P1{WEEK_BASED_YEARS}5M"));
    }

    @Test
    public void unionWithMixedSigns() {
		Duration<CalendarUnit> p1 = Duration.ofCalendarUnits(0, 5, 4);
		Duration<CalendarUnit> p2 = Duration.ofCalendarUnits(0, 4, 34).inverse();
		List<Duration<CalendarUnit>> durations = p1.union(p2);
        assertThat(durations.size(), is(2));
		assertThat(durations.get(0), is(p1));
		assertThat(durations.get(1), is(p2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void unionWithUnitsOfSameLength() {
		Duration.ofZero()
			.plus(1, CalendarUnit.QUARTERS)
			.union(Duration.of(1, CalendarUnit.QUARTERS.unlessInvalid()));
    }

    @Test
    public void plusTimeSpan() {
        Duration<CalendarUnit> p1 = Duration.ofCalendarUnits(0, 0, 10);
        Duration<CalendarUnit> p2 = Duration.ofCalendarUnits(0, 4, 20);
        assertThat(p1.plus(p2), is(Duration.ofCalendarUnits(0, 4, 30)));

        p1 = Duration.ofCalendarUnits(0, 0, 2);
        p2 = Duration.of(1, WEEKS);
        assertThat(
            p1.plus(p2).toString(),
            is("P1W2D"));
        assertThat(
            p2.plus(p1).toString(),
            is("P1W2D"));
        assertThat(
            p2.plus(Duration.ofCalendarUnits(1, 0, 2)).toString(),
            is("P1Y1W2D"));
        assertThat(
            Duration.of(0, SECONDS).plus(createClockPeriod()),
            is(Duration.of(0, SECONDS).plus(123000, NANOS)));

    }

    @Test
    public void testToString() throws ParseException {
        String period = "-P12Y4M3DT150H2M0,075800000S";
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 0).plus(75800000, NANOS);
        String formatted1 =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse().toString();
        String formatted2 =
            Duration.parsePeriod(formatted1).toString();
        assertThat(formatted1, is(period));
        assertThat(formatted2, is(period)); // roundtrip

        period = "P13W2D";
        datePeriod = Duration.of(13, WEEKS).plus(2, DAYS);
        assertThat(datePeriod.toString(), is(period));

        period = "PT2,123456789S";
        timePeriod = Duration.of(2, SECONDS).plus(123456789, NANOS);
        assertThat(timePeriod.toString(), is(period));
    }

    @Test
    public void testToStringWithSpecialUnitAtEndOfMonth() {
        Duration<IsoDateUnit> datePeriod =
            Duration.of(4, CalendarUnit.MONTHS.atEndOfMonth());
        assertThat(datePeriod.toString(), is("P4{M-END_OF_MONTH}"));
    }

    @Test
    public void testToStringWithSpecialUnitNextValidDate() {
        Duration<IsoDateUnit> datePeriod =
            Duration.of(4, CalendarUnit.MONTHS.nextValidDate());
        assertThat(datePeriod.toString(), is("P4{M-NEXT_VALID_DATE}"));
    }

    @Test
    public void testToStringWithSpecialUnitUnlessInvalid() {
        Duration<IsoDateUnit> datePeriod =
            Duration.of(4, CalendarUnit.YEARS.unlessInvalid());
        assertThat(datePeriod.toString(), is("P4{Y-UNLESS_INVALID}"));
    }

    @Test
    public void testToStringWithSpecialUnitCarryOver() {
        Duration<IsoDateUnit> datePeriod =
            Duration.of(4, CalendarUnit.YEARS.withCarryOver());
        assertThat(datePeriod.toString(), is("P4{Y-CARRY_OVER}"));
    }

    @Test
    public void testToStringWithSpecialUnitWeekBasedYears() {
        Duration<IsoDateUnit> datePeriod =
            Duration.of(4, CalendarUnit.weekBasedYears());
        assertThat(datePeriod.toString(), is("P4{WEEK_BASED_YEARS}"));
    }

    @Test
    public void testToStringISO() throws ParseException {
        String period = "P12Y4M3DT150H2M0,075800000S";
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 0).plus(75800000, NANOS);
        String formatted1 =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).toString();
        String formatted2 =
            Duration.parsePeriod(formatted1).toString();
        assertThat(formatted1, is(period));
        assertThat(formatted2, is(period)); // roundtrip

        period = "P93D";
        datePeriod = Duration.of(13, WEEKS).plus(2, DAYS);
        assertThat(datePeriod.toStringISO(), is(period));

        period = "P3W";
        datePeriod = Duration.of(3, WEEKS);
        assertThat(datePeriod.toStringISO(), is(period));

        period = "PT2,123456789S";
        timePeriod = Duration.of(2, SECONDS).plus(123456789, NANOS);
        assertThat(timePeriod.toStringISO(), is(period));
    }

    @Test
    public void testToStringXML1() throws ParseException {
        String period = "-P12Y4M3DT150H2M0.075800000S";
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 0).plus(75800000, NANOS);
        String formatted1 =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse().toStringXML();
        String formatted2 = Duration.parsePeriod(formatted1).toStringXML();
        assertThat(formatted1, is(period));
        assertThat(formatted2, is(period)); // roundtrip
    }

    @Test
    public void testToStringXML2() {
        assertThat(
            Duration.of(13, WEEKS).toStringXML(),
            is("P91D")); // 13 * 7
    }

    @Test(expected=ChronoException.class)
    public void testToStringISONegative() {
        Duration.of(3, DAYS).inverse().toStringISO();
    }

    @Test(expected=ChronoException.class)
    public void testToStringISOSpecialUnit() {
        Duration.of(3, DAYS.atEndOfMonth()).toStringISO();
    }

    @Test(expected=ChronoException.class)
    public void testToStringXMLSpecialUnit() {
        Duration.of(3, DAYS.atEndOfMonth()).toStringXML();
    }

    @Test
    public void parse() throws Exception {
        String period = "-P12Y4M3DT150H2M4,758S";
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 4).plus(758000000, NANOS);
        Duration<IsoUnit> expResult =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse();
        assertThat(Duration.parsePeriod(period), is(expResult));

        period = "P4M3DT150H2M4S";
        datePeriod = Duration.ofCalendarUnits(0, 4, 3);
        timePeriod = Duration.ofClockUnits(150, 2, 4);
        expResult = Duration.ofZero().plus(datePeriod).plus(timePeriod);
        assertThat(Duration.parsePeriod(period), is(expResult));

        period = "P2Y4M3D";
        datePeriod = Duration.ofCalendarUnits(2, 4, 3);
        expResult = Duration.ofZero().plus(datePeriod);
        assertThat(Duration.parsePeriod(period), is(expResult));

        period = "-PT7H340M0,007S";
        timePeriod = Duration.ofClockUnits(7, 340, 0).plus(7000000, NANOS);
        expResult = Duration.ofZero().plus(timePeriod).inverse();
        assertThat(Duration.parsePeriod(period), is(expResult));

        period = "P0000Y04M03D";
        datePeriod = Duration.ofCalendarUnits(0, 4, 3);
        expResult = Duration.ofZero().plus(datePeriod);
        assertThat(Duration.parsePeriod(period), is(expResult));

        period = "P12Y4M2W";
        datePeriod = Duration.ofCalendarUnits(12, 4, 0).plus(2, WEEKS);
        expResult = Duration.ofZero().plus(datePeriod);
        assertThat(Duration.parsePeriod(period), is(expResult));

        period = "P2W3D";
        datePeriod = Duration.of(3, DAYS).plus(2, WEEKS);
        expResult = Duration.ofZero().plus(datePeriod);
        assertThat(Duration.parsePeriod(period), is(expResult));
    }

    @Test(expected=ParseException.class)
    public void parseWithoutPeriodSymbol() throws Exception {
        try {
            String period = "-12Y4M30D";
            Duration.parsePeriod(period); // P fehlt
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(1));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithoutUnits() throws Exception {
        try {
            String period = "P12";
            Duration.parsePeriod(period); // Einheitensymbol fehlt
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(3));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithoutItems() throws Exception {
        try {
            String period = "P";
            Duration.parsePeriod(period); // Zeitfeld fehlt
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(1));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithoutTime() throws Exception {
        try {
            String period = "P12DT";
            Duration.parsePeriod(period); // Uhrzeitfeld fehlt
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(5));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongOrder() throws Exception {
        try {
            String period = "-P4M12Y3DT150H2M4,758S";
            Duration.parsePeriod(period); // falsche Reihenfolge der Einheiten
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(6));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongSymbolZ() throws Exception {
        try {
            String period = "-P12Y3DT150Z2M4,758S";
            Duration.parsePeriod(period); // falsches Symbol Z
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(11));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongSymbolHHNoAmount() throws Exception {
        try {
            String period = "-P12Y3DT150HH2M4,758S";
            Duration.parsePeriod(period); // doppeltes Symbol H ohne Betrag
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(12));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongSymbolHHAndAmount() throws Exception {
        try {
            String period = "-P12Y3DT150H6H2M4,758S";
            Duration.parsePeriod(period); // doppeltes Symbol H mit Betrag
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(13));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongDecimal1() throws Exception {
        try {
            String period = "-P12Y3DT150H2M4.S";
            Duration.parsePeriod(period); // Dezimalfehler
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(16));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongDecimal2() throws Exception {
        try {
            String period = "-P12Y3DT150H2M.2S";
            Duration.parsePeriod(period); // Dezimalfehler
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(14));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongDecimal3() throws Exception {
        try {
            String period = "-P12Y3DT150H2M0;2S";
            Duration.parsePeriod(period); // Dezimalfehler
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(15));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongSignStyle1() throws Exception {
        try {
            String period = "P-12Y-3D";
            Duration.parsePeriod(period); // Vorzeichenfehler
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(1));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongSignStyle2() throws Exception {
        try {
            String period = "P12Y-3D";
            Duration.parsePeriod(period); // Vorzeichenfehler
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(4));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseWithWrongTail() throws Exception {
        try {
            String period = "P12Y3D ";
            Duration.parsePeriod(period); // Leerzeichen am Ende
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(6));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseSpecialUnits() throws Exception {
        try {
            String period = "P4{Y-CARRY_OVER}";
            Duration.parsePeriod(period);
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(2));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseCalendarPeriodWithTimeComponent1() throws Exception {
        try {
            String period = "P12Y3DT20H";
            Duration.parseCalendarPeriod(period);
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(6));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseCalendarPeriodWithTimeComponent2() throws Exception {
        try {
            String period = "PT20H";
            Duration.parseCalendarPeriod(period);
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(1));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test(expected=ParseException.class)
    public void parseClockPeriodWithCalendarComponent() throws Exception {
        try {
            String period = "P12Y3D";
            Duration.parseClockPeriod(period);
        } catch (ParseException pe) {
            assertThat(pe.getErrorOffset(), is(1));
            System.out.println(pe.getMessage());
            throw pe;
        }
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void testEquals() throws ParseException {
        String period = "-P12Y4M3DT150H2M4,0758S";
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 4).plus(75800000, ClockUnit.NANOS);
        Duration<IsoUnit> test1 =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse();
        Duration<IsoUnit> test2 = Duration.parsePeriod(period);
        Duration<IsoUnit> test3 = Duration.parsePeriod("P2Y").plus(-2, YEARS);

        assertThat(test1.equals(test2), is(true));
        assertThat(test1.equals(Duration.of(0, DAYS)), is(false));
        assertThat(test1.equals(null), is(false));
        assertThat(Duration.of(0, DAYS).equals(null), is(false));
        assertThat(test3.equals(Duration.of(0, DAYS)), is(true));
    }

    @Test
    public void testEqualsWithSpecialUnits() throws ParseException {
        assertThat(
            Duration.of(1, YEARS)
                .equals(Duration.of(1, CalendarUnit.weekBasedYears())),
            is(false));
        assertThat(
            Duration.of(1, MONTHS)
                .equals(Duration.of(1, MONTHS.unlessInvalid())),
            is(false));
        Duration<IsoUnit> period =
            Duration.ofPositive().months(4).days(3).build()
            .plus(Duration.of(2, CalendarUnit.weekBasedYears()));
        Duration<?> expected =
            Duration.ofZero().plus(Duration.ofCalendarUnits(0, 4, 3))
            .plus(Duration.of(2, CalendarUnit.weekBasedYears()));
        assertThat(period.equals(expected), is(true));
    }

    @Test
    public void testHashCode() throws ParseException {
        String period = "-P12Y4M3DT150H2M4,0758S";
        Duration<CalendarUnit> datePeriod =
            Duration.ofCalendarUnits(12, 4, 3);
        Duration<ClockUnit> timePeriod =
            Duration.ofClockUnits(150, 2, 4).plus(75800000, ClockUnit.NANOS);
        Duration<IsoUnit> test1 =
            Duration.ofZero().plus(datePeriod).plus(timePeriod).inverse();
        Duration<IsoUnit> test2 = Duration.parsePeriod(period);
        assertThat(test1.hashCode(), is(test2.hashCode()));
    }

    private static TimeSpan<ClockUnit> createClockPeriod() {

        return new TimeSpan<ClockUnit>() {
            @Override
            public List<Item<ClockUnit>> getTotalLength() {
                return Collections.singletonList(Item.of(123, MICROS));
            }
            @Override
            public boolean isNegative() {
                return false;
            }
            @Override
            public boolean contains(ClockUnit unit) {
                throw new UnsupportedOperationException();
            }
            @Override
            public long getPartialAmount(ClockUnit unit) {
                throw new UnsupportedOperationException();
            }
            @Override
            public boolean isPositive() {
                return true;
            }
            @Override
            public boolean isEmpty() {
                return false;
            }
            @Override
            public <T extends TimePoint<? super ClockUnit, T>>
            T addTo(T time) {
                throw new UnsupportedOperationException();
            }
            @Override
            public <T extends TimePoint<? super ClockUnit, T>>
            T subtractFrom(T time) {
                throw new UnsupportedOperationException();
            }
        };

    }

    @Test
    public void ofSingleUnitMillis() {
        Duration<ClockUnit> dur = Duration.of(123, MILLIS);
        assertThat(dur.toString(), is("PT0,123000000S"));
        assertThat(dur.getTotalLength().size(), is(1));
        assertThat(dur.isPositive(), is(true));
        assertThat(dur.isNegative(), is(false));
        assertThat(dur.isEmpty(), is(false));
        Item<ClockUnit> item = dur.getTotalLength().get(0);
        assertThat(item.getAmount(), is(123000000L));
        assertThat(item.getUnit(), is(NANOS));
    }

    @Test
    public void ofSingleUnitMicros() {
        Duration<ClockUnit> dur = Duration.of(123456, MICROS);
        assertThat(dur.toString(), is("PT0,123456000S"));
        assertThat(dur.getTotalLength().size(), is(1));
        assertThat(dur.isPositive(), is(true));
        assertThat(dur.isNegative(), is(false));
        assertThat(dur.isEmpty(), is(false));
        Item<ClockUnit> item = dur.getTotalLength().get(0);
        assertThat(item.getAmount(), is(123456000L));
        assertThat(item.getUnit(), is(NANOS));
    }

    @Test
    public void ofSingleUnitNanos() {
        Duration<ClockUnit> dur = Duration.of(123456789, NANOS);
        assertThat(dur.toString(), is("PT0,123456789S"));
        assertThat(dur.getTotalLength().size(), is(1));
        assertThat(dur.isPositive(), is(true));
        assertThat(dur.isNegative(), is(false));
        assertThat(dur.isEmpty(), is(false));
        Item<ClockUnit> item = dur.getTotalLength().get(0);
        assertThat(item.getAmount(), is(123456789L));
        assertThat(item.getUnit(), is(NANOS));
    }

    @Test
    public void ofMillisPlusMicros1() {
        Duration<ClockUnit> dur = Duration.of(123, MILLIS).plus(1000, MICROS);
        assertThat(dur.getTotalLength().size(), is(1));
        Item<ClockUnit> item = dur.getTotalLength().get(0);
        assertThat(item.getAmount(), is(124000000L));
        assertThat(item.getUnit(), is(NANOS));
    }

    @Test
    public void ofMillisPlusMicros2() {
        Duration<ClockUnit> dur = Duration.of(123, MILLIS).plus(1001, MICROS);
        assertThat(dur.getTotalLength().size(), is(1));
        Item<ClockUnit> item = dur.getTotalLength().get(0);
        assertThat(item.getAmount(), is(124001000L));
        assertThat(item.getUnit(), is(NANOS));
    }

}
