package net.time4j;



import java.time.DayOfWeek;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class WeekdayValueTest {

    @Test
    public void valueOf_int() {
        for (int i = 0; i < 7; i++) {
            assertThat(Weekday.valueOf(i + 1), is(Weekday.values()[i]));
        }
    }

    @Test
    public void valueOf_int_Weekmodel() {
        assertThat(
            Weekday.valueOf(1, Weekmodel.of(Locale.US)),
            is(Weekday.SUNDAY));
    }

    @Test
    public void valueOf_YearMonthDay() {
        assertThat(
            Weekday.valueOf(2014, Month.MARCH, 3),
            is(Weekday.MONDAY));
    }

    @Test
    public void values_Weekmodel() {
        Weekday[] array = Weekday.values(Weekmodel.of(Locale.US));
        assertThat(array[0], is(Weekday.SUNDAY));
        assertThat(array[1], is(Weekday.MONDAY));
        assertThat(array[2], is(Weekday.TUESDAY));
        assertThat(array[3], is(Weekday.WEDNESDAY));
        assertThat(array[4], is(Weekday.THURSDAY));
        assertThat(array[5], is(Weekday.FRIDAY));
        assertThat(array[6], is(Weekday.SATURDAY));
    }

    @Test
    public void getValue() {
        for (int i = 0; i < 7; i++) {
            assertThat(Weekday.values()[i].getValue(), is(i + 1));
        }
    }

    @Test
    public void getValue_Weekmodel() {
        for (int i = 0; i < 7; i++) {
            int expected = (i == 6) ? 1 : i + 2;

            assertThat(
                Weekday.values()[i].getValue(Weekmodel.of(Locale.US)),
                is(expected));
        }
    }

    @Test
    public void next() {
        assertThat(Weekday.MONDAY.next(), is(Weekday.TUESDAY));
        assertThat(Weekday.SUNDAY.next(), is(Weekday.MONDAY));
    }

    @Test
    public void previous() {
        assertThat(Weekday.MONDAY.previous(), is(Weekday.SUNDAY));
        assertThat(Weekday.THURSDAY.previous(), is(Weekday.WEDNESDAY));
    }

    @Test
    public void roll() {
        assertThat(Weekday.FRIDAY.roll(-6), is(Weekday.SATURDAY));
    }

    @Test
    public void test() {
        assertThat(Weekday.FRIDAY.test(PlainDate.of(2014, 4, 11)), is(true));
    }

    @Test
    public void threetenConversion() {
        assertThat(Weekday.MONDAY.toTemporalAccessor(), is(DayOfWeek.MONDAY));
        assertThat(Weekday.TUESDAY.toTemporalAccessor(), is(DayOfWeek.TUESDAY));
        assertThat(Weekday.WEDNESDAY.toTemporalAccessor(), is(DayOfWeek.WEDNESDAY));
        assertThat(Weekday.THURSDAY.toTemporalAccessor(), is(DayOfWeek.THURSDAY));
        assertThat(Weekday.FRIDAY.toTemporalAccessor(), is(DayOfWeek.FRIDAY));
        assertThat(Weekday.SATURDAY.toTemporalAccessor(), is(DayOfWeek.SATURDAY));
        assertThat(Weekday.SUNDAY.toTemporalAccessor(), is(DayOfWeek.SUNDAY));

        assertThat(Weekday.from(DayOfWeek.MONDAY), is(Weekday.MONDAY));
        assertThat(Weekday.from(DayOfWeek.TUESDAY), is(Weekday.TUESDAY));
        assertThat(Weekday.from(DayOfWeek.WEDNESDAY), is(Weekday.WEDNESDAY));
        assertThat(Weekday.from(DayOfWeek.THURSDAY), is(Weekday.THURSDAY));
        assertThat(Weekday.from(DayOfWeek.FRIDAY), is(Weekday.FRIDAY));
        assertThat(Weekday.from(DayOfWeek.SATURDAY), is(Weekday.SATURDAY));
        assertThat(Weekday.from(DayOfWeek.SUNDAY), is(Weekday.SUNDAY));
    }

}