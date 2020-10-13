package net.time4j.i18n;

import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CLDR24Test {

    @Test
    public void formatMidnightAtEndOfDay() throws ParseException {
        assertThat(
            ChronoFormatter.ofTimePattern(
                "HH:mm",
                PatternType.CLDR_24,
                Locale.ROOT
            ).with(Leniency.STRICT).format(PlainTime.midnightAtEndOfDay()),
            is("24:00"));
    }

    @Test
    public void parseMidnightAtEndOfDay() throws ParseException {
        assertThat(
            ChronoFormatter.ofTimePattern(
                "HH:mm",
                PatternType.CLDR_24,
                Locale.ROOT
            ).with(Leniency.STRICT).parse("24:00"),
            is(PlainTime.midnightAtEndOfDay()));
    }

    // Note: 24:00 not fully supported in context of PlainTimestamp
    @Test
    public void formatMidnightAtEndOfDayTSP() throws ParseException {
        assertThat(
            ChronoFormatter.ofTimestampPattern(
                "dd.MM.yyyy HH:mm",
                PatternType.CLDR_24,
                Locale.ROOT
            ).format(PlainTimestamp.of(2015, 6, 30, 24, 0)),
            is("01.07.2015 00:00"));
    }

    // Note: 24:00 not fully supported in context of PlainTimestamp
    @Test
    public void parseMidnightAtEndOfDayTSP() throws ParseException {
        assertThat(
            ChronoFormatter.ofTimestampPattern(
                "dd.MM.yyyy HH:mm",
                PatternType.CLDR_24,
                Locale.ROOT
            ).parse("30.06.2015 24:00"), // throws an exception in strict mode
            is(PlainTimestamp.of(2015, 6, 30, 24, 0)));
    }

}