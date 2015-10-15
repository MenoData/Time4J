package net.time4j.i18n;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.format.DisplayMode;
import net.time4j.tz.Timezone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class FormatPatternTest {

    @Test
    public void datePattern() {
        PlainDate date = PlainDate.of(2015, 9, 10);
        assertThat(
            PlainDate.formatter(DisplayMode.LONG, Locale.GERMANY).format(date),
            is("10. September 2015"));
    }

    @Test
    public void timePattern() {
        PlainTime time = PlainTime.of(17, 45, 30);
        assertThat(
            PlainTime.formatter(DisplayMode.LONG, Locale.GERMANY).format(time),
            is("17:45:30")); // ohne Offset!!!
    }

    @Test
    public void dateTimePattern() {
        Moment m = Moment.UNIX_EPOCH;
        assertThat(
            Moment.formatter(DisplayMode.LONG, Locale.GERMANY, Timezone.of("Europe/Berlin").getID()).format(m),
            is("1. Januar 1970 01:00:00 MEZ"));
    }

}