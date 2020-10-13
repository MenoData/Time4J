package net.time4j.calendar.hindu;

import net.time4j.calendar.IndianMonth;
import net.time4j.format.Attributes;
import net.time4j.format.NumberSystem;
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
public class HinduFormatTest {

    @Test
    public void printMY() {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("G, MMMM yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family());
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            f.print(cal),
            is("K.Y, Magha 3101"));
    }

    @Test
    public void parseMY() throws ParseException {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("G, MMMM yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family())
                .withCalendarVariant(AryaSiddhanta.SOLAR.variant())
                .withDefault(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(1));
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 1);
        assertThat(
            f.parse("K.Y, Magha 3101"),
            is(cal));
    }

    @Test
    public void printDMY() {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("G, d. MMMM yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family());
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            f.print(cal),
            is("K.Y, 19. Magha 3101"));
    }

    @Test
    public void parseDMY() throws ParseException {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("G, d. MMMM yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family())
                .withCalendarVariant(AryaSiddhanta.SOLAR.variant());
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            f.parse("K.Y, 19. Magha 3101"),
            is(cal));
    }

    @Test
    public void printOrdinalDate() {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("D yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family());
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(
            f.print(cal),
            is("293 3101"));
    }

    @Test
    public void parseOrdinalDate() throws ParseException {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("D yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family())
                .withCalendarVariant(AryaSiddhanta.SOLAR.variant());
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(f.parse("293 3101"), is(cal));
        assertThat(cal.getDayOfYear(), is(293));
    }

    @Test
    public void printLeapMonthWide() {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("G, MMMM yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family());
        assertThat(
            f.print(createLeapMonth()),
            is("V.S., adhika Vaisakha 1549"));
    }

    @Test
    public void printLeapMonthShort() {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter
                .ofPattern("G, M yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family())
                .with(HinduPrimitive.ADHIKA_INDICATOR, '*')
                .with(HinduPrimitive.ADHIKA_IS_TRAILING, true)
                .with(Attributes.NUMBER_SYSTEM, NumberSystem.DEVANAGARI);
        assertThat(
            f.print(createLeapMonth()),
            is("V.S., \u0968* \u0967\u096B\u096A\u096F"));
    }

    private static HinduCalendar createLeapMonth() {
        return HinduCalendar.of(
            HinduRule.AMANTA.variant(),
            HinduEra.VIKRAMA,
            1549,
            HinduMonth.of(IndianMonth.VAISHAKHA).withLeap(),
            HinduDay.valueOf(3));
    }

}
