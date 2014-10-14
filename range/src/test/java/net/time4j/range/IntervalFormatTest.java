package net.time4j.range;

import net.time4j.Iso8601Format;
import net.time4j.PlainDate;
import net.time4j.format.Attributes;
import net.time4j.format.ChronoFormatter;
import net.time4j.format.ParseLog;

import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class IntervalFormatTest {

    @Test
    public void testToString() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_NEVER),
            is("20140227/20140514"));
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD),
            is("20140227/20140514"));
        assertThat(
            interval.print(formatter, BracketPolicy.SHOW_ALWAYS),
            is("[20140227/20140514]"));
    }

    @Test
    public void parse() {
        PlainDate start = PlainDate.of(2014, 2, 27);
        PlainDate end = PlainDate.of(2014, 5, 14);
        ChronoInterval<PlainDate> interval = DateInterval.between(start, end);
        ChronoFormatter<PlainDate> formatter =
            Iso8601Format.BASIC_CALENDAR_DATE;

        ParseLog plog = new ParseLog();
        assertThat(
            IntervalParser.of(formatter, BracketPolicy.SHOW_NEVER)
                .parse("20140227/20140514", plog, Attributes.empty()),
            is(interval));

        plog.reset();
        assertThat(
            IntervalParser.of(formatter, BracketPolicy.SHOW_WHEN_NON_STANDARD)
                .parse("20140227/20140514", plog, Attributes.empty()),
            is(interval));

        plog.reset();
        assertThat(
            IntervalParser.of(formatter, BracketPolicy.SHOW_ALWAYS)
                .parse("[20140227/20140514]", plog, Attributes.empty()),
            is(interval));

        plog.reset();
        assertThat(
            IntervalParser.of(
                DateIntervalFactory.INSTANCE,
                formatter,
                ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                    .startSection(Attributes.PROTECTED_CHARACTERS, 5)
                    .addFixedInteger(PlainDate.YEAR, 4)
                    .endSection()
                    .startSection(Attributes.PROTECTED_CHARACTERS, 3)
                    .addFixedInteger(PlainDate.MONTH_AS_NUMBER, 2)
                    .endSection()
                    .addFixedInteger(PlainDate.DAY_OF_MONTH, 2)
                    .build(),
                BracketPolicy.SHOW_ALWAYS
            ).parse("[20140227/0514]", plog, Attributes.empty()),
            is(interval));
    }

}