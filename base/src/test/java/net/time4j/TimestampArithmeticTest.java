package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.ClockUnit.HOURS;
import static net.time4j.ClockUnit.MILLIS;
import static net.time4j.ClockUnit.MINUTES;
import static net.time4j.ClockUnit.NANOS;
import static net.time4j.PlainTime.MICRO_OF_SECOND;
import static net.time4j.PlainTime.MILLI_OF_SECOND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TimestampArithmeticTest {

    @Test
    public void plusMonths() {
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(1, MONTHS),
            is(PlainTimestamp.of(2012, 2, 29, 12, 45)));
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(48, MONTHS),
            is(PlainTimestamp.of(2016, 1, 31, 12, 45)));
    }

    @Test
    public void plusDays() {
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(30, DAYS),
            is(PlainTimestamp.of(2012, 3, 1, 12, 45)));
    }

    @Test
    public void plusHours() {
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(3, HOURS),
            is(PlainTimestamp.of(2012, 1, 31, 15, 45)));
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(30, HOURS),
            is(PlainTimestamp.of(2012, 2, 1, 18, 45)));
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(55, HOURS),
            is(PlainTimestamp.of(2012, 2, 2, 19, 45)));
    }

    @Test
    public void plusMinutes() {
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(1501, MINUTES),
            is(PlainTimestamp.of(2012, 2, 1, 13, 46)));
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 23, 59).plus(1, MINUTES),
            is(PlainTimestamp.of(2012, 2, 1, 0, 0)));
        assertThat(
            PlainTimestamp.of(2012, 2, 1, 0, 0).plus(1, MINUTES),
            is(PlainTimestamp.of(2012, 2, 1, 0, 1)));
    }

    @Test
    public void minusMinutes() {
        int amount = 28 * 24 * 60 + 22 * 60 + 58;
        assertThat(
            PlainTimestamp.of(2012, 2, 29, 11, 43, 59).minus(amount, MINUTES),
            is(PlainTimestamp.of(2012, 1, 31, 12, 45, 59)));
        assertThat(
            PlainTimestamp.of(2012, 2, 1, 0, 0).minus(1, MINUTES),
            is(PlainTimestamp.of(2012, 1, 31, 23, 59)));
        assertThat(
            PlainTimestamp.of(2012, 2, 1, 0, 1).minus(1, MINUTES),
            is(PlainTimestamp.of(2012, 2, 1, 0, 0)));
    }

    @Test
    public void plusMillis() {
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45).plus(36000001, MILLIS),
            is(
                PlainDate.of(2012, 1, 31)
                .at(PlainTime.of(22, 45, 0, 1000000))));
    }

    @Test
    public void plusNanos() {
        assertThat(
            PlainTimestamp.of(2012, 1, 31, 12, 45, 30).plus(123456789, NANOS),
            is(
                PlainDate.of(2012, 1, 31)
                .at(PlainTime.of(12, 45, 30, 123456789))));
    }

    @Test(expected=ArithmeticException.class)
    public void plusNanosOnMax1() {
        PlainTimestamp.axis().getMaximum().plus(1, NANOS);
    }

    @Test(expected=ArithmeticException.class)
    public void plusNanosOnMax2() {
        PlainTimestamp.axis().getMaximum().plus(Duration.of(1, NANOS));
    }

    @Test
    public void monthsBetween() {
        assertThat(
            MONTHS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 45)
            ),
            is(0L));
        assertThat(
            MONTHS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 44, 59)
            ),
            is(0L));
        assertThat(
            MONTHS.between(
                PlainTimestamp.of(2012, 1, 29, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 45)
            ),
            is(1L));
        assertThat(
            MONTHS.between(
                PlainTimestamp.of(2012, 1, 29, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 44, 59)
            ),
            is(0L));
        assertThat(
            PlainTimestamp.of(2012, 1, 29, 12, 45).until(
                PlainTimestamp.of(2012, 3, 28, 12, 44, 59),
                MONTHS.withCarryOver()
            ),
            is(1L));
    }

    @Test
    public void daysBetween() {
        assertThat(
            DAYS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 45)
            ),
            is(29L));
        assertThat(
            DAYS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 44, 59)
            ),
            is(28L));
    }

    @Test
    public void hoursBetween() {
        assertThat(
            HOURS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 13, 47)
            ),
            is(29 * 24L + 1));
        assertThat(
            HOURS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 45)
            ),
            is(29 * 24L));
        assertThat(
            HOURS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 44, 59)
            ),
            is(28 * 24L + 23));
        assertThat(
            HOURS.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 11, 44, 59)
            ),
            is(28 * 24L + 22));
    }

    @Test
    public void minutesBetween() {
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 15, 47, 30)
            ),
            is(29 * 24 * 60L + 182));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 47)
            ),
            is(29 * 24 * 60L + 2));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 45)
            ),
            is(29 * 24 * 60L));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 12, 44, 59)
            ),
            is(28 * 24 * 60L + 23 * 60 + 59));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45),
                PlainTimestamp.of(2012, 2, 29, 11, 43, 59)
            ),
            is(28 * 24 * 60L + 22 * 60 + 58));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45, 37),
                PlainTimestamp.of(2012, 2, 29, 11, 43, 38)
            ),
            is(28 * 24 * 60L + 22 * 60 + 58));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45, 37),
                PlainTimestamp.of(2012, 2, 29, 11, 43, 37)
            ),
            is(28 * 24 * 60L + 22 * 60 + 58));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 1, 31, 12, 45, 37),
                PlainTimestamp.of(2012, 2, 29, 11, 43, 36)
            ),
            is(28 * 24 * 60L + 22 * 60 + 57));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 2, 29, 11, 43, 59),
                PlainTimestamp.of(2012, 1, 31, 12, 45)
            ),
            is(-28 * 24 * 60L - 22 * 60 - 58));
        assertThat(
            MINUTES.between(
                PlainTimestamp.of(2012, 2, 29, 11, 43, 36),
                PlainTimestamp.of(2012, 1, 31, 12, 45, 37)
            ),
            is(-28 * 24 * 60L - 22 * 60 - 57));
    }

    @Test
    public void millisBetween() {
        assertThat(
            MILLIS.between(
                PlainTimestamp
                    .of(2012, 1, 31, 11, 43, 59)
                    .with(MILLI_OF_SECOND, 3),
                PlainTimestamp
                    .of(2012, 2, 29, 11, 43, 59)
                    .with(MILLI_OF_SECOND, 4)
            ),
            is(29 * 86400L * 1000 + 1));
        assertThat(
            MILLIS.between(
                PlainTimestamp
                    .of(2012, 1, 31, 11, 43, 59)
                    .with(MILLI_OF_SECOND, 4),
                PlainTimestamp
                    .of(2012, 2, 29, 11, 43, 59)
                    .with(MILLI_OF_SECOND, 4)
            ),
            is(29 * 86400L * 1000));
        assertThat(
            MILLIS.between(
                PlainTimestamp
                    .of(2012, 1, 31, 11, 43, 59)
                    .with(MILLI_OF_SECOND, 5),
                PlainTimestamp
                    .of(2012, 2, 29, 11, 43, 59)
                    .with(MILLI_OF_SECOND, 4)
            ),
            is(29 * 86400L * 1000 - 1));
        assertThat(
            MILLIS.between(
                PlainTimestamp
                    .of(2012, 1, 31, 11, 43, 59)
                    .with(MICRO_OF_SECOND, 3018),
                PlainTimestamp
                    .of(2012, 2, 29, 11, 43, 59)
                    .with(MICRO_OF_SECOND, 3018)
            ),
            is(29 * 86400L * 1000));
        assertThat(
            MILLIS.between(
                PlainTimestamp
                    .of(2012, 1, 31, 11, 43, 59)
                    .with(MICRO_OF_SECOND, 3019),
                PlainTimestamp
                    .of(2012, 2, 29, 11, 43, 59)
                    .with(MICRO_OF_SECOND, 3018)
            ),
            is(29 * 86400L * 1000 - 1));
        assertThat(
            MILLIS.between(
                PlainTimestamp
                    .of(2012, 1, 31, 11, 43, 59)
                    .with(MICRO_OF_SECOND, 4019),
                PlainTimestamp
                    .of(2012, 2, 29, 11, 43, 59)
                    .with(MICRO_OF_SECOND, 3018)
            ),
            is(29 * 86400L * 1000 - 2));
    }

}