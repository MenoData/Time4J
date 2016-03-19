package net.time4j.format.expert;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;

import java.text.ParseException;
import java.util.Locale;

import net.time4j.PlainTimestamp;
import net.time4j.SystemClock;
import net.time4j.format.Attributes;
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
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("MM-dd", PatternType.CLDR).build()
                .withDefault(PlainDate.YEAR, 2012);
        PlainDate date = fmt.parse("05-21");
        assertThat(date, is(PlainDate.of(2012, 5, 21)));
    }

    @Test
    public void missingToday() throws ParseException {
        ChronoFormatter<PlainTimestamp> fmt =
            ChronoFormatter.ofTimestampPattern("HH:mm", PatternType.CLDR, Locale.ROOT)
                .withDefaultSupplier(PlainDate.COMPONENT, () -> SystemClock.inLocalView().today());
        PlainTimestamp tsp = fmt.parse("14:45");
        assertThat(tsp, is(SystemClock.inLocalView().today().atTime(14, 45)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void dateFormatWithDefaultHour() throws ParseException {
        ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
            .addPattern("MM-dd", PatternType.CLDR).build()
            .withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 12);
    }

    @Test
    public void missingMonth() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("yyyy-dd", PatternType.CLDR).build()
                .withDefault(PlainDate.MONTH_AS_NUMBER, 11);
        PlainDate date = fmt.parse("2012-21");
        assertThat(date, is(PlainDate.of(2012, 11, 21)));
    }

    @Test(expected=ParseException.class)
    public void noReplaceMonth() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .addPattern("MMM[/]dd[/]yyyy", PatternType.CLDR).build()
                .withDefault(PlainDate.MONTH_OF_YEAR, Month.NOVEMBER);
        fmt.parse("21/2012");
    }

    @Test
    public void replaceWithDefaultMonth() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.US)
                .startSection(Attributes.PROTECTED_CHARACTERS, 8)
                .addText(PlainDate.MONTH_OF_YEAR)
                .endSection()
                .addPattern("[/]dd[/]yyyy", PatternType.CLDR)
                .build()
                .withDefault(PlainDate.MONTH_OF_YEAR, Month.NOVEMBER);
        PlainDate date = fmt.parse("21/2012");
        assertThat(date, is(PlainDate.of(2012, 11, 21)));
    }

    @Test(expected=ParseException.class)
    public void noReplaceOfWrongYear() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .addPattern("uuuu[-]MM[-]dd", PatternType.CLDR).build()
                .withDefault(PlainDate.YEAR, 1985);
        fmt.parse("10-21"); // 10 is not a valid year
    }

    @Test
    public void replaceWrongYear() throws ParseException {
        ChronoFormatter<PlainDate> fmt =
            ChronoFormatter.setUp(PlainDate.class, Locale.ROOT)
                .startSection(Attributes.PROTECTED_CHARACTERS, 6)
                .addInteger(
                    PlainDate.YEAR, 4, 9, SignPolicy.SHOW_WHEN_BIG_NUMBER)
                .endSection()
                .addPattern("[-]MM[-]dd", PatternType.CLDR)
                .build()
                .withDefault(PlainDate.YEAR, 1985);
        PlainDate date = fmt.parse("10-21");
        assertThat(date, is(PlainDate.of(1985, 10, 21)));
    }

    @Test(expected=ParseException.class)
    public void noReplaceOfHour() throws ParseException {
        ChronoFormatter<PlainTime> fmt =
            ChronoFormatter.setUp(PlainTime.class, Locale.ROOT)
                .addPattern("HH[:]mm[:]ss", PatternType.CLDR).build()
                .withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 15);
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
                .withDefault(PlainTime.DIGITAL_HOUR_OF_DAY, 15);
        PlainTime time = fmt.parse("10:21");
        assertThat(time, is(PlainTime.of(15, 10, 21)));
    }

}
