package net.time4j.calendar.bahai;

import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class FormatTest {

    @Test
    public void printNormalDate() {
        ChronoFormatter<BadiCalendar> f = stdFormat();
        assertThat(
            f.print(BadiCalendar.of(5, 11, BadiMonth.JALAL, 13)),
            is("1.5.11.2.13"));
    }

    @Test
    public void parseNormalDate() throws ParseException {
        ChronoFormatter<BadiCalendar> f = stdFormat();
        assertThat(
            f.parse("1.5.11.2.13"),
            is(BadiCalendar.of(5, 11, BadiMonth.JALAL, 13)));
    }

    @Test
    public void printIntercalaryDate() {
        ChronoFormatter<BadiCalendar> f = stdFormat();
        assertThat(
            f.print(BadiCalendar.ofIntercalary(5, 11, 2)),
            is("1.5.11.Aiyam-e Ha'.2"));
    }

    @Test
    public void parseIntercalaryDate() throws ParseException {
        ChronoFormatter<BadiCalendar> f = stdFormat();
        assertThat(
            f.parse("1.5.11.Aiyam-e Ha'.2"),
            is(BadiCalendar.ofIntercalary(5, 11, 2)));
    }

    @Test
    public void html() {
        ChronoFormatter<BadiCalendar> f =
            ChronoFormatter
                .ofPattern("k-v-y-MMMM-d", PatternType.DYNAMIC, Locale.ENGLISH, BadiCalendar.axis())
                .with(BadiCalendar.TEXT_CONTENT_ATTRIBUTE, FormattedContent.HTML);
        assertThat(
            f.print(BadiCalendar.of(5, 11, BadiMonth.MASHIYYAT, 15)),
            is("1-5-11-Ma<span style=\"text-decoration: underline;\">sh</span>íyyat-15"));
    }

    private static ChronoFormatter<BadiCalendar> stdFormat() {
        return ChronoFormatter.ofPattern(
            "k.v.y.m.d|k.v.y.A.d",
            PatternType.DYNAMIC,
            Locale.GERMAN,
            BadiCalendar.axis());
    }

}
