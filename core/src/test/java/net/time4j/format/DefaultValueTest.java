package net.time4j.format;

import net.time4j.PatternType;
import net.time4j.PlainDate;
import net.time4j.PlainTime;

import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class DefaultValueTest {

    @Test
    public void missingYear() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("MM-dd", PatternType.CLDR)
                     .withDefault(PlainDate.YEAR, 2012);
        PlainDate date = fmt.parse("05-21");
        assertThat(date, is(PlainDate.of(2012, 5, 21)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void dateFormatWithDefaultHour() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("MM-dd", PatternType.CLDR)
                     .withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 12);
    }

    @Test
    public void missingMonth() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("yyyy-dd", PatternType.CLDR)
                     .withDefault(PlainDate.MONTH_AS_NUMBER, 11);
        PlainDate date = fmt.parse("2012-21");
        assertThat(date, is(PlainDate.of(2012, 11, 21)));
    }

    @Test(expected=ParseException.class)
    public void noReplaceOfWrongYear() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("uuuu[-]MM[-]dd", PatternType.CLDR)
                     .withDefault(PlainDate.YEAR, 1985);
        fmt.parse("10-21");
    }

    @Test
    public void replaceWrongYear() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            PlainDate.localFormatter("uuuu[-]MM[-]dd", PatternType.CLDR)
                     .withDefault(PlainDate.YEAR, 1985)
                     .with(Attributes.USE_DEFAULT_WHEN_ERROR, true);
        PlainDate date = fmt.parse("10-21");
        assertThat(date, is(PlainDate.of(1985, 10, 21)));
    }

    @Test(expected=ParseException.class)
    public void noReplaceOfHour() throws ParseException {
        ChronoFormatter<PlainTime> fmt =
            PlainTime.localFormatter("HH[:]mm[:]ss", PatternType.CLDR)
                     .withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 15)
                     .with(Attributes.USE_DEFAULT_WHEN_ERROR, true);
        PlainTime time = fmt.parse("10:21");
        assertThat(time, is(PlainTime.of(15, 10, 21)));
    }

    @Test
    public void replaceOfMissingHour() throws ParseException {
        ChronoFormatter<PlainTime> fmt =
            ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
                .startSection(Attributes.PROTECTED_CHARACTERS, 6)
                .addFixedInteger(PlainTime.DIGITAL_HOUR_OF_DAY, 2)
                .endSection()
                .startOptionalSection()
                .addLiteral(':')
                .endSection()
                .addFixedInteger(PlainTime.MINUTE_OF_HOUR, 2)
                .startOptionalSection()
                .addLiteral(':')
                .endSection()
                .addFixedInteger(PlainTime.SECOND_OF_MINUTE, 2)
                .build()
                .withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 15)
                .with(Attributes.USE_DEFAULT_WHEN_ERROR, true);
        PlainTime time = fmt.parse("10:21");
        assertThat(time, is(PlainTime.of(15, 10, 21)));
    }

}
