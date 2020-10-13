package net.time4j.range;

import net.time4j.Weekday;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class SpanOfWeekdaysTest {

    @Test
    public void betweenMondayAndFriday() {
        assertThat(
            SpanOfWeekdays.betweenMondayAndFriday(),
            is(SpanOfWeekdays.between(Weekday.MONDAY, Weekday.FRIDAY)));
    }

    @Test
    public void getStart() {
        assertThat(
            SpanOfWeekdays.betweenMondayAndFriday().getStart(),
            is(Weekday.MONDAY));
    }

    @Test
    public void getEnd() {
        assertThat(
            SpanOfWeekdays.betweenMondayAndFriday().getEnd(),
            is(Weekday.FRIDAY));
    }

    @Test
    public void iterator() {
        int i = 0;
        for (Weekday day : SpanOfWeekdays.between(Weekday.SATURDAY, Weekday.FRIDAY)) {
            i++;
            switch (i) {
                case 1:
                    assertThat(day, is(Weekday.SATURDAY));
                    break;
                case 2:
                    assertThat(day, is(Weekday.SUNDAY));
                    break;
                case 3:
                    assertThat(day, is(Weekday.MONDAY));
                    break;
                case 4:
                    assertThat(day, is(Weekday.TUESDAY));
                    break;
                case 5:
                    assertThat(day, is(Weekday.WEDNESDAY));
                    break;
                case 6:
                    assertThat(day, is(Weekday.THURSDAY));
                    break;
                case 7:
                    assertThat(day, is(Weekday.FRIDAY));
                    break;
                default:
                    fail("Unexpected length of iterator.");
            }
        }
    }

    @Test
    public void length() {
        assertThat(SpanOfWeekdays.betweenMondayAndFriday().length(), is(5));
        assertThat(SpanOfWeekdays.on(Weekday.SATURDAY).length(), is(1));
        assertThat(SpanOfWeekdays.between(Weekday.SATURDAY, Weekday.FRIDAY).length(), is(7));
    }

    @Test
    public void chronology() {
        assertThat(
            SpanOfWeekdays.chronology().getRegisteredElements(),
            is(new HashSet<>(Arrays.asList(SpanOfWeekdays.START, SpanOfWeekdays.END))));
    }

    @Test
    public void formatter() throws ParseException {
        ChronoFormatter<SpanOfWeekdays> f =
            SpanOfWeekdays.formatter("SSSS[ 'to' EEEE]", Locale.ENGLISH);
        SpanOfWeekdays span = SpanOfWeekdays.betweenMondayAndFriday();
        SpanOfWeekdays single = SpanOfWeekdays.on(Weekday.SUNDAY);
        assertThat(
            f.format(span),
            is("Monday to Friday"));
        assertThat(
            f.parse("Monday to Friday"),
            is(span));
        assertThat(
            f.format(single),
            is("Sunday to Sunday"));
        assertThat(
            f.parse("Sunday"),
            is(single));
    }

}