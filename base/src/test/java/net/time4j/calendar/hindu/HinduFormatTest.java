package net.time4j.calendar.hindu;

import net.time4j.calendar.IndianMonth;
import net.time4j.format.TextWidth;
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
                .withCalendarVariant(AryaSiddhanta.SOLAR)
                .withDefault(HinduCalendar.DAY_OF_MONTH, HinduDay.valueOf(1));
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 1);
        assertThat(
            f.parse("K.Y, Magha 3101"),
            is(cal));
    }

    @Test
    public void parseDMY() throws ParseException {
        ChronoFormatter<HinduCalendar> f =
            ChronoFormatter.ofPattern("G, d. MMMM yyyy", PatternType.CLDR, Locale.ENGLISH, HinduCalendar.family())
                .withCalendarVariant(AryaSiddhanta.SOLAR);
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
                .withCalendarVariant(AryaSiddhanta.SOLAR);
        HinduCalendar cal = HinduCalendar.ofOldSolar(3101, HinduMonth.of(IndianMonth.MAGHA).getRasi(), 19);
        assertThat(f.parse("293 3101"), is(cal));
        assertThat(cal.getDayOfYear(), is(293));
    }

}
