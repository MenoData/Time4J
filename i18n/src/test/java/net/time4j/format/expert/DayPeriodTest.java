package net.time4j.format.expert;

import net.time4j.DayPeriod;
import net.time4j.PlainTime;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DayPeriodTest {

    @Test
    public void displayMidnight() {
        assertThat(
            PlainTime.midnightAtEndOfDay().get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("midnight"));
        assertThat(
            PlainTime.midnightAtStartOfDay().get(
                DayPeriod.of(Locale.GERMAN).approximate()),
            is("Mitternacht"));
    }

    @Test
    public void displayMorning() {
        assertThat(
            PlainTime.of(11, 59).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("am"));
        assertThat(
            PlainTime.of(11, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("in the morning"));
    }

    @Test
    public void displayNoon() {
        assertThat(
            PlainTime.of(12).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("noon"));
        assertThat(
            PlainTime.of(12).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("noon"));
    }

    @Test
    public void displayAfternoon() {
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("pm"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.GERMAN).fixed(TextWidth.ABBREVIATED, OutputContext.FORMAT)),
            is("nachm."));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("in the afternoon"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.GERMAN).approximate()),
            is("nachmittags"));
    }

    @Test
    public void displayEvening() {
        assertThat(
            PlainTime.of(20, 45).get(
                DayPeriod.of(Locale.ENGLISH).approximate(TextWidth.WIDE, OutputContext.STANDALONE)
            ),
            is("evening"));
    }

    @Test
    public void displayNight() {
        assertThat(
            PlainTime.of(5, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate(TextWidth.WIDE, OutputContext.FORMAT)
            ),
            is("at night"));
    }

    @Test
    public void startEndForMidnight() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(0)),
            is(PlainTime.of(0)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(0)),
            is(PlainTime.of(0, 1)));
    }

    @Test
    public void startEndForNight1() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(4)),
            is(PlainTime.of(0, 1)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(4)),
            is(PlainTime.of(6)));
    }

    @Test
    public void startEndForMorning() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(9)),
            is(PlainTime.of(6)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(9)),
            is(PlainTime.of(12)));
    }

    @Test
    public void startEndForNoon() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(12)),
            is(PlainTime.of(12)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(12)),
            is(PlainTime.of(12, 1)));
    }

    @Test
    public void startEndForAfternoon() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(15)),
            is(PlainTime.of(12, 1)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(15)),
            is(PlainTime.of(18)));
    }

    @Test
    public void startEndForEvening() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(20)),
            is(PlainTime.of(18)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(20)),
            is(PlainTime.of(21)));
    }

    @Test
    public void startEndForNight2() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(23)),
            is(PlainTime.of(21)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(23)),
            is(PlainTime.of(0)));
    }

    @Test
    public void fallback() {
        assertThat(
            PlainTime.of(5).get(DayPeriod.of(new Locale("xyz")).approximate()),
            is("AM"));
        assertThat(
            PlainTime.of(12).get(DayPeriod.of(new Locale("xyz")).approximate()),
            is("PM"));
    }

}
